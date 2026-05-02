package com.taolife.plan.service.impl;

import com.taolife.aicore.client.AiClient;
import com.taolife.aicore.config.ChatConfigVO;
import com.taolife.aicore.model.ChatCompletionRequest;
import com.taolife.aicore.model.ChatCompletionResponse;
import com.taolife.aicore.model.Message;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.common.utils.UUIDKeyGeneratorUtil;
import com.taolife.plan.entity.UserPlan;
import com.taolife.plan.entity.PlanCycleReport;
import com.taolife.plan.entity.PlanTask;
import com.taolife.plan.mapper.UserPlanMapper;
import com.taolife.plan.mapper.PlanCycleReportMapper;
import com.taolife.plan.mapper.PlanTaskMapper;
import com.taolife.plan.service.IPlanCycleReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方案周期报告服务实现
 * 提供周期报告的生成、查询、已读标记等功能
 *
 * @author 文二
 * @date 2026-04-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanCycleReportServiceImpl implements IPlanCycleReportService {

    private final PlanCycleReportMapper planCycleReportMapper;
    private final UserPlanMapper userPlanMapper;
    private final PlanTaskMapper planTaskMapper;
    private final AiClient aiClient;
    private final IAiConfigService aiConfigService;

    /**
     * 获取用户未读的周期报告（仅返回当前活跃方案对应的上周期报告）
     *
     * @param accountId 账号ID
     * @return 未读报告实体，无未读时返回null
     */
    @Override
    public PlanCycleReport getUnreadReport(String accountId) {
        UserPlan activePlan = userPlanMapper.selectActiveByAccountId(accountId);
        if (activePlan != null) {
            return planCycleReportMapper.selectUnreadByAccountIdAndNewPlanId(accountId, activePlan.getId());
        }
        return planCycleReportMapper.selectUnreadByAccountId(accountId);
    }

    /**
     * 根据方案ID获取周期报告（用于历史方案查看自身执行总结）
     *
     * @param planId 方案ID（被总结的旧方案）
     * @param accountId 账号ID
     * @return 周期报告，无则返回null
     */
    @Override
    public PlanCycleReport getReportByPlanId(String planId, String accountId) {
        return planCycleReportMapper.selectByPlanIdAndAccountId(planId, accountId);
    }

    /**
     * 标记周期报告为已读
     * 校验报告归属后更新已读状态
     *
     * @param reportId  报告ID
     * @param accountId 账号ID
     */
    @Override
    public void markRead(String reportId, String accountId) {
        PlanCycleReport report = planCycleReportMapper.selectOneById(reportId);
        if (report != null && report.getAccountId().equals(accountId) && report.getIsRead() == 0) {
            report.setIsRead(1);
            report.setUpdateTime(LocalDateTime.now());
            planCycleReportMapper.update(report);
            log.info("周期报告标记为已读, reportId: {}", reportId);
        }
    }

    /**
     * 异步生成周期报告
     * 在新方案生成后异步触发旧方案的周期总结报告生成
     *
     * @param accountId 账号ID
     * @param oldPlanId 旧方案ID（被总结）
     * @param newPlanId 新方案ID（触发更新）
     */
    @Async
    @Override
    public void generateReportAsync(String accountId, String oldPlanId, String newPlanId) {
        try {
            generateReport(accountId, oldPlanId, newPlanId);
        } catch (Exception e) {
            log.error("周期报告生成失败, accountId: {}, oldPlanId: {}", accountId, oldPlanId, e);
        }
    }

    /**
     * 同步生成周期报告（内部方法，由 generateReportAsync 异步调用）
     * 收集旧方案的任务完成数据，调用AI生成总结报告并保存到数据库
     *
     * @param accountId 账号ID
     * @param oldPlanId 被总结的旧方案ID
     * @param newPlanId 触发总结的新方案ID
     */
    private void generateReport(String accountId, String oldPlanId, String newPlanId) {
        log.info("开始生成周期报告, accountId: {}, oldPlanId: {}", accountId, oldPlanId);

        // 1. 获取旧方案信息
        UserPlan oldPlan = userPlanMapper.selectById(oldPlanId);
        if (oldPlan == null) {
            log.warn("旧方案不存在, oldPlanId: {}", oldPlanId);
            return;
        }

        // 2. 查询旧方案的所有任务，统计完成情况
        List<PlanTask> allTasks = planTaskMapper.selectAllByUserPlanId(oldPlanId);
        long totalTasks = allTasks.size();
        long completedTasks = countByStatus(allTasks, 2);
        long skippedTasks = countByStatus(allTasks, 3);
        long pendingTasks = totalTasks - completedTasks - skippedTasks;
        int completionRate = totalTasks > 0 ? (int) (completedTasks * 100 / totalTasks) : 0;

        // 3. 按类型分别统计已完成数（饮食/运动/穴位/经络/生活）
        long foodCompleted = countCompletedByType(allTasks, 1);
        long exerciseCompleted = countCompletedByType(allTasks, 2);
        long acupointCompleted = countCompletedByType(allTasks, 3);
        long meridianCompleted = countCompletedByType(allTasks, 4);
        long lifestyleCompleted = countCompletedByType(allTasks, 5);

        // 4. 调用AI生成报告（失败时使用默认模板）
        // 从数据库读取周期报告提示词模板
        Map<String, Object> promptVariables = new HashMap<>();
        promptVariables.put("constitutionName", oldPlan.getConstitutionName() != null ? oldPlan.getConstitutionName() : "");
        promptVariables.put("seasonName", oldPlan.getSeasonName() != null ? oldPlan.getSeasonName() : "");
        promptVariables.put("cycleDays", String.valueOf(oldPlan.getCycleDays()));
        promptVariables.put("completionRate", String.valueOf(completionRate));
        promptVariables.put("total", String.valueOf(totalTasks));
        promptVariables.put("completed", String.valueOf(completedTasks));
        promptVariables.put("skipped", String.valueOf(skippedTasks));
        promptVariables.put("pending", String.valueOf(pendingTasks));
        promptVariables.put("foodCompleted", String.valueOf(foodCompleted));
        promptVariables.put("exerciseCompleted", String.valueOf(exerciseCompleted));
        promptVariables.put("acupointCompleted", String.valueOf(acupointCompleted));
        promptVariables.put("meridianCompleted", String.valueOf(meridianCompleted));
        promptVariables.put("lifestyleCompleted", String.valueOf(lifestyleCompleted));
        String prompt = aiConfigService.renderPrompt("cycle_report", promptVariables);

        String reportContent = callAiForReport(prompt);

        if (reportContent == null || reportContent.isEmpty()) {
            log.warn("AI生成周期报告失败, 使用默认报告");
            reportContent = buildDefaultReport(oldPlan, completionRate, completedTasks, totalTasks);
        }

        // 5. 保存报告到数据库
        PlanCycleReport report = new PlanCycleReport();
        String id = (String) new UUIDKeyGeneratorUtil().generate(null, "id");
        report.setId(id);
        report.setAccountId(accountId);
        report.setPlanId(oldPlanId);
        report.setNewPlanId(newPlanId);
        report.setReportContent(reportContent);
        report.setIsRead(0);
        report.setCreateTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        planCycleReportMapper.insert(report);

        log.info("周期报告生成完成, reportId: {}", id);
    }

    /**
     * 统计指定状态的任务数量
     *
     * @param tasks 任务列表
     * @param status 任务状态值
     * @return 该状态的任务数
     */
    private long countByStatus(List<PlanTask> tasks, int status) {
        return tasks.stream()
                .filter(t -> t.getStatus() != null && t.getStatus() == status)
                .count();
    }

    /**
     * 统计指定类型且已完成的任务数量
     *
     * @param tasks   任务列表
     * @param planType 方案类型（1-饮食 2-运动 3-穴位 4-经络 5-生活）
     * @return 该类型中已完成的任务数
     */
    private long countCompletedByType(List<PlanTask> tasks, int planType) {
        return tasks.stream()
                .filter(t -> t.getPlanType() != null && t.getPlanType() == planType
                        && t.getStatus() != null && t.getStatus() == 2)
                .count();
    }

    /**
     * 调用AI接口生成周期报告内容
     *
     * @param prompt 报告生成的提示词
     * @return AI生成的报告内容（Markdown），失败时返回null
     */
    private String callAiForReport(String prompt) {
        try {
            ChatConfigVO chatConfig = aiConfigService.getChatConfig();

            List<Message> messages = new java.util.ArrayList<>();
            messages.add(Message.system(aiConfigService.getCycleReportPrompt()));
            messages.add(Message.user(prompt));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(chatConfig.getModel())
                    .messages(messages)
                    .temperature(chatConfig.getTemperature())
                    .maxTokens(chatConfig.getMaxTokens())
                    .build();

            ChatCompletionResponse response = aiClient.chatCompletions(request, chatConfig);
            if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
        } catch (Exception e) {
            log.error("AI生成周期报告调用失败", e);
        }
        return null;
    }

    /**
     * 构建默认的周期报告（AI调用失败时的降级方案）
     * 根据完成率生成简单文字总结，引导用户继续坚持
     *
     * @param plan           旧方案
     * @param completionRate 完成率（百分比）
     * @param completed      已完成任务数
     * @param total          总任务数
     * @return 默认报告内容（Markdown）
     */
    private String buildDefaultReport(UserPlan plan, int completionRate, long completed, long total) {
        return String.format("""
                ## 周期调理报告

                **体质**：%s | **季节**：%s | **周期**：%d天

                ### 整体表现
                本周期共安排 %d 项任务，你完成了 %d 项，完成率为 %d%%。

                ### 总结
                %s

                ### 下周期建议
                坚持每日的饮食调理和生活习惯，逐步提高运动和穴位按摩的完成率，效果会越来越好。
                """,
                plan.getConstitutionName(),
                plan.getSeasonName(),
                plan.getCycleDays(),
                total, completed, completionRate,
                completionRate >= 80 ? "表现优秀！坚持就是最好的养生。" : "还有提升空间，继续加油！");
    }
}
