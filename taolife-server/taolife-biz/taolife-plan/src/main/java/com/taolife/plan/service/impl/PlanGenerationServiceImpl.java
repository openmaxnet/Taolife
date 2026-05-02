package com.taolife.plan.service.impl;

import com.taolife.aicore.client.AiClient;
import com.taolife.aicore.config.ChatConfigVO;
import com.taolife.aicore.model.ChatCompletionRequest;
import com.taolife.aicore.model.ChatCompletionResponse;
import com.taolife.aicore.model.Message;
import com.taolife.aichat.entity.HealthDocument;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.wisdom.service.IFitnessService;
import com.taolife.aichat.service.IDocSearchService;
import com.taolife.wisdom.vo.FitnessResultVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.security.UserContext;
import com.taolife.common.utils.UUIDKeyGeneratorUtil;
import com.taolife.plan.entity.AcupointPlan;
import com.taolife.plan.entity.ExercisePlan;
import com.taolife.plan.entity.FoodPlan;
import com.taolife.plan.entity.LifestylePlan;
import com.taolife.plan.entity.MeridianPlan;
import com.taolife.plan.entity.UserPlan;
import com.taolife.plan.entity.PlanTask;
import com.taolife.identity.entity.UserPreference;
import com.taolife.plan.mapper.AcupointPlanMapper;
import com.taolife.plan.mapper.ExercisePlanMapper;
import com.taolife.plan.mapper.FoodPlanMapper;
import com.taolife.plan.mapper.LifestylePlanMapper;
import com.taolife.plan.mapper.MeridianPlanMapper;
import com.taolife.plan.mapper.UserPlanMapper;
import com.taolife.plan.mapper.PlanTaskMapper;
import com.taolife.identity.mapper.UserPreferenceMapper;
import com.taolife.plan.service.IPlanGenerationService;
import com.taolife.plan.service.IPlanCycleReportService;
import com.taolife.plan.service.IPlanContentMarkerService;
import com.taolife.plan.enums.PlanGenerationMessage;
import com.taolife.plan.vo.GeneratePlanVO;
import com.taolife.plan.vo.GenerationStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * 方案生成服务实现
 * 提供健康方案异步生成的业务逻辑，集成AI大模型生成个性化方案
 *
 * @author 文二
 * @date 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanGenerationServiceImpl implements IPlanGenerationService {

    private final UserPlanMapper userPlanMapper;
    private final PlanTaskMapper planTaskMapper;
    private final UserPreferenceMapper userPreferenceMapper;
    private final FoodPlanMapper foodPlanMapper;
    private final ExercisePlanMapper exercisePlanMapper;
    private final AcupointPlanMapper acupointPlanMapper;
    private final MeridianPlanMapper meridianPlanMapper;
    private final LifestylePlanMapper lifestylePlanMapper;
    private final IFitnessService constitutionService;
    private final AiClient aiClient;
    private final IDocSearchService docSearchService;
    private final IAiConfigService aiConfigService;
    private final IPlanCycleReportService planCycleReportService;
    private final IPlanContentMarkerService planContentMarkerService;

    private final UUIDKeyGeneratorUtil uuidKeyGeneratorUtil = new UUIDKeyGeneratorUtil();
    private final ConcurrentHashMap<String, GenerationStatusVO> taskStatusMap = new ConcurrentHashMap<>();

    /**
     * 提交方案生成任务
     * 前置校验（体质测评存在、无进行中任务）后创建异步生成任务并返回任务ID
     *
     * @param accountId 账号ID
     * @param cycleDays 方案周期天数（3-30天）
     * @return 生成任务信息（含任务ID和预估时间）
     */
    @Override
    public GeneratePlanVO submitGenerationTask(String accountId, int cycleDays) {
        // 前置校验1：必须有体质测评记录
        try {
            FitnessResultVO latestResult = constitutionService.getLatestResult(accountId);
            if (latestResult == null) {
                throw new BusinessException(ExceptionCode.PLAN_GENERATION_FAILED, "请先完成体质测评");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ExceptionCode.PLAN_GENERATION_FAILED, "获取体质信息失败，请稍后重试");
        }

        // 前置校验2：不能有正在进行的生成任务（防重复提交）
        boolean hasRunningTask = taskStatusMap.values().stream()
            .anyMatch(vo -> vo.getStatus() != null && (vo.getStatus() == 1 || vo.getStatus() == 2));
        if (hasRunningTask) {
            throw new BusinessException(ExceptionCode.PLAN_GENERATING, "方案生成中，请稍候");
        }

        // 生成任务ID
        String taskId = (String) uuidKeyGeneratorUtil.generate(null, "id");

        // 创建初始状态
        GenerationStatusVO statusVO = new GenerationStatusVO();
        statusVO.setTaskId(taskId);
        statusVO.setStatus(1); // pending
        statusVO.setProgress(0);
        taskStatusMap.put(taskId, statusVO);

        // 保存当前上下文（异步线程需要继承主线程的用户和安全上下文）
        UserContext currentUserContext = UserContext.get();
        SecurityContext securityContext = SecurityContextHolder.getContext();

        // 限制周期范围
        int validCycleDays = Math.max(3, Math.min(cycleDays, 30));

        // 提交异步任务（虚拟线程）
        Thread.ofVirtual().start(() -> {
            UserContext.set(currentUserContext);
            SecurityContextHolder.setContext(securityContext);
            try {
                generatePlan(taskId, accountId, validCycleDays);
            } catch (Exception e) {
                log.error("方案生成失败, taskId: {}", taskId, e);
                String errorMsg = "方案生成失败，请稍后重试";
                updateStatus(taskId, 4, 0, null, errorMsg);
            } finally {
                UserContext.clear();
                SecurityContextHolder.clearContext();
            }
        });

        // 构建返回结果
        GeneratePlanVO vo = new GeneratePlanVO();
        vo.setTaskId(taskId);
        vo.setMessage("方案生成中，请稍候...");
        vo.setEstimatedTime("2-5分钟");
        return vo;
    }

    /**
     * 查询方案生成任务状态
     *
     * @param taskId 生成任务ID
     * @return 任务状态VO（含进度和结果）
     */
    @Override
    public GenerationStatusVO getGenerationStatus(String taskId) {
        GenerationStatusVO statusVO = taskStatusMap.get(taskId);
        if (statusVO == null) {
            throw new BusinessException(ExceptionCode.PLAN_GENERATION_TASK_NOT_FOUND, "生成任务不存在");
        }
        return statusVO;
    }

    /**
     * 生成健康方案（核心逻辑，异步执行）
     *
     * @param taskId     任务ID
     * @param accountId  账号ID
     * @param cycleDays  方案周期天数
     */
    private void generatePlan(String taskId, String accountId, int cycleDays) {
        // 1. 更新状态为处理中
        updateStatus(taskId, 2, 10, null, null);

        // 1.1 将用户已有的进行中方案标记为已终止，并记录旧方案ID用于生成周期报告
        String oldPlanId = null;
        try {
            UserPlan existingPlan = userPlanMapper.selectActiveByAccountId(accountId);
            if (existingPlan != null) {
                oldPlanId = existingPlan.getId();
                existingPlan.setStatus(4); // TERMINATED
                existingPlan.setUpdateTime(LocalDateTime.now());
                userPlanMapper.update(existingPlan);
                log.info("旧方案标记为已终止, oldPlanId: {}", oldPlanId);
            }
        } catch (Exception e) {
            log.warn("检查旧方案状态失败, accountId: {}", accountId, e);
        }

        // 2. 查询体质信息
        String constitutionCode = "";
        String constitutionName = "未知体质";
        String constitutionDetail = "";
        try {
            FitnessResultVO latestResult = constitutionService.getLatestResult(accountId);
            if (latestResult != null) {
                constitutionCode = latestResult.getConstitutionCode();
                constitutionName = latestResult.getConstitutionName();
                constitutionDetail = "用户体质：" + constitutionName
                    + "（得分：" + latestResult.getScore() + "分）";
            }
        } catch (Exception e) {
            log.warn("获取体质信息失败, accountId: {}", accountId, e);
        }

        // 根据当前月份自动确定季节
        int currentMonth = LocalDate.now().getMonthValue();
        int season = currentMonth >= 3 && currentMonth <= 5 ? 1 : currentMonth >= 6 && currentMonth <= 8 ? 2 : currentMonth >= 9 && currentMonth <= 11 ? 3 : 4;
        String seasonName = getSeasonName(season);

        // 3. 查询用户偏好
        String preferenceDetail = "";
        try {
            UserPreference preference = userPreferenceMapper.selectByAccountId(accountId);
            if (preference != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("用户偏好信息：");

                // 身体数据
                if (preference.getHeight() != null) {
                    sb.append("身高：").append(preference.getHeight()).append("cm；");
                }
                if (preference.getWeight() != null) {
                    sb.append("体重：").append(preference.getWeight()).append("kg；");
                }
                if (preference.getBloodType() != null) {
                    String[] bloodTypes = {"", "A型", "B型", "AB型", "O型"};
                    if (preference.getBloodType() >= 1 && preference.getBloodType() <= 4) {
                        sb.append("血型：").append(bloodTypes[preference.getBloodType()]).append("；");
                    }
                }
                if (preference.getAllergyHistory() != null && !preference.getAllergyHistory().isEmpty()) {
                    sb.append("过敏史：").append(preference.getAllergyHistory()).append("；");
                }
                if (preference.getMedicalHistory() != null && !preference.getMedicalHistory().isEmpty()) {
                    sb.append("病史：").append(preference.getMedicalHistory()).append("；");
                }

                // 饮食偏好
                if (preference.getPreferredFoodNature() != null && !preference.getPreferredFoodNature().isEmpty()) {
                    sb.append("喜欢的食物性质：").append(preference.getPreferredFoodNature()).append("；");
                }
                if (preference.getPreferredFoodTexture() != null) {
                    String[] textures = {"", "清淡", "浓郁", "辛辣", "偏甜"};
                    if (preference.getPreferredFoodTexture() >= 1 && preference.getPreferredFoodTexture() <= 4) {
                        sb.append("口味偏好：").append(textures[preference.getPreferredFoodTexture()]).append("；");
                    }
                }
                if (preference.getDislikedFoods() != null && !preference.getDislikedFoods().isEmpty()) {
                    sb.append("不喜欢的食材：").append(preference.getDislikedFoods()).append("；");
                }
                if (preference.getAllergicFoods() != null && !preference.getAllergicFoods().isEmpty()) {
                    sb.append("过敏食材：").append(preference.getAllergicFoods()).append("；");
                }
                if (preference.getDietaryGoal() != null && !preference.getDietaryGoal().isEmpty()) {
                    sb.append("饮食目标：").append(preference.getDietaryGoal()).append("；");
                }

                // 运动偏好
                if (preference.getPreferredExerciseType() != null) {
                    String[] exerciseTypes = {"", "有氧运动", "力量训练", "柔韧性运动", "球类运动", "传统功法"};
                    if (preference.getPreferredExerciseType() >= 1 && preference.getPreferredExerciseType() <= 5) {
                        sb.append("偏好的运动类型：").append(exerciseTypes[preference.getPreferredExerciseType()]).append("；");
                    }
                }
                if (preference.getPreferredExerciseIntensity() != null) {
                    String[] intensities = {"", "低强度", "中强度", "高强度"};
                    if (preference.getPreferredExerciseIntensity() >= 1 && preference.getPreferredExerciseIntensity() <= 3) {
                        sb.append("偏好的运动强度：").append(intensities[preference.getPreferredExerciseIntensity()]).append("；");
                    }
                }
                if (preference.getPreferredExerciseTime() != null) {
                    String[] times = {"", "清晨", "上午", "下午", "傍晚", "晚上"};
                    if (preference.getPreferredExerciseTime() >= 1 && preference.getPreferredExerciseTime() <= 5) {
                        sb.append("偏好的运动时间：").append(times[preference.getPreferredExerciseTime()]).append("；");
                    }
                }
                if (preference.getPreferredExerciseDuration() != null) {
                    sb.append("运动时长：").append(preference.getPreferredExerciseDuration()).append("分钟；");
                }

                // 生活习惯
                if (preference.getSleepTime() != null && !preference.getSleepTime().isEmpty()) {
                    sb.append("就寝时间：").append(preference.getSleepTime()).append("；");
                }
                if (preference.getWakeTime() != null && !preference.getWakeTime().isEmpty()) {
                    sb.append("起床时间：").append(preference.getWakeTime()).append("；");
                }
                if (preference.getStressLevel() != null) {
                    String[] stressLevels = {"", "低", "中", "高"};
                    if (preference.getStressLevel() >= 1 && preference.getStressLevel() <= 3) {
                        sb.append("压力水平：").append(stressLevels[preference.getStressLevel()]).append("；");
                    }
                }

                // 健康目标
                if (preference.getHealthGoalPrimary() != null) {
                    String[] goals = {"", "增强免疫", "改善睡眠", "体重管理", "缓解压力", "改善消化", "增强体力"};
                    if (preference.getHealthGoalPrimary() >= 1 && preference.getHealthGoalPrimary() <= 6) {
                        sb.append("主要健康目标：").append(goals[preference.getHealthGoalPrimary()]).append("；");
                    }
                }

                // 穴位偏好
                if (preference.getPreferredAcupoints() != null && !preference.getPreferredAcupoints().isEmpty()) {
                    sb.append("关注的穴位：").append(preference.getPreferredAcupoints()).append("；");
                }

                String result = sb.toString();
                if (!result.equals("用户偏好信息：")) {
                    preferenceDetail = result;
                }
            }
        } catch (Exception e) {
            log.warn("获取用户偏好失败, accountId: {}", accountId, e);
        }

        // 4. 知识检索（聚合1次）
        Integer topK = aiConfigService.getKnowledgeTopK();
        String knowledgeContext = retrieveKnowledgeContext(constitutionDetail, seasonName, topK);
        updateStatus(taskId, 2, 20, null, null);

        // 5. 单次AI调用生成完整方案（含模拟进度）
        PlanGenerateResult result = generateAllContent(
            taskId, cycleDays, constitutionName, seasonName,
            constitutionDetail, preferenceDetail, knowledgeContext
        );

        // 5.1 对方案内容进行结构化标记（将食材/穴位/经络名替换为可点击链接）
        try {
            result.foodContent = planContentMarkerService.processFoodContent(result.foodContent);
            result.acupointContent = planContentMarkerService.processAcupointContent(result.acupointContent);
            result.meridianContent = planContentMarkerService.processMeridianContent(result.meridianContent);
        } catch (Exception e) {
            log.warn("方案内容标记失败，继续使用原始内容", e);
        }

        // 6-10. 保存方案数据到数据库
        try {
            LocalDate today = LocalDate.now();

            // 创建各分类方案记录并保存内容
            String foodPlanId = saveFoodPlan(result.foodContent, result.foodTags, constitutionCode, season);
            String exercisePlanId = saveExercisePlan(result.exerciseContent, result.exerciseTags, constitutionCode, season);
            String acupointPlanId = saveAcupointPlan(result.acupointContent, result.acupointTags, constitutionCode, season);
            String meridianPlanId = saveMeridianPlan(result.meridianContent, result.meridianTags, constitutionCode, season);
            String lifestylePlanId = saveLifestylePlan(result.lifestyleContent, result.lifestyleTags, constitutionCode, season);

            // 创建健康方案记录（存储分类方案ID）
            UserPlan plan = new UserPlan();
            plan.setAccountId(accountId);
            plan.setConstitutionCode(constitutionCode);
            plan.setConstitutionName(constitutionName);
            plan.setSeason(season);
            plan.setSeasonName(seasonName);
            plan.setFoodPlanId(foodPlanId);
            plan.setExercisePlanId(exercisePlanId);
            plan.setAcupointPlanId(acupointPlanId);
            plan.setMeridianPlanId(meridianPlanId);
            plan.setLifestylePlanId(lifestylePlanId);
            plan.setStartDate(today);
            plan.setEndDate(today.plusDays(cycleDays));
            plan.setCycleDays(cycleDays);
            plan.setStatus(1);
            plan.setCompletionRate(BigDecimal.ZERO);
            plan.setAiAdjustmentCount(0);
            plan.setShareCount(0);
            plan.setPlanTitle(result.planTitle);
            plan.setPlanTags(result.planTags);
            plan.setDailyFocuses(result.dailyFocuses);
            plan.setPlanDate(today);
            plan.setTotalTasks(calculateTotalTasks(cycleDays));
            plan.setCompletedTasks(0);
            plan.setAdjustmentCount(0);
            plan.setCreateTime(LocalDateTime.now());
            plan.setUpdateTime(LocalDateTime.now());
            userPlanMapper.insert(plan);

            // 基于方案内容生成整个周期的任务
            generateCycleTasks(plan, result.foodContent, result.exerciseContent,
                    result.acupointContent, result.meridianContent, result.lifestyleContent);

            // 更新分类方案记录，关联用户方案ID
            updateCategoryPlanId(foodPlanId, plan.getId());
            updateCategoryPlanId(exercisePlanId, plan.getId());
            updateCategoryPlanId(acupointPlanId, plan.getId());
            updateCategoryPlanId(meridianPlanId, plan.getId());
            updateCategoryPlanId(lifestylePlanId, plan.getId());

            // 更新状态为已完成
            updateStatus(taskId, 3, 100, plan.getId(), null);

            // 如果有旧方案，异步生成周期报告
            if (oldPlanId != null) {
                try {
                    planCycleReportService.generateReportAsync(accountId, oldPlanId, plan.getId());
                } catch (Exception e) {
                    log.warn("触发周期报告生成失败, oldPlanId: {}", oldPlanId, e);
                }
            }

            log.info("方案生成完成, taskId: {}, planId: {}, accountId: {}", taskId, plan.getId(), accountId);
        } catch (Exception e) {
            log.error("方案数据保存失败, taskId: {}, accountId: {}", taskId, accountId, e);
            updateStatus(taskId, 4, 0, null, "方案保存失败，请稍后重试");
            return;
        }
    }

    /**
     * 聚合知识库检索（一次性）
     * 合并5个子方案的检索需求为一个综合查询
     */
    private String retrieveKnowledgeContext(String constitutionDetail, String seasonName, int topK) {
        try {
            String query = constitutionDetail + " " + seasonName + " 饮食 运动 穴位按摩 经络调理 起居情志 养生调理";
            List<HealthDocument> docs = docSearchService.retrieve(query, topK);
            if (docs != null && !docs.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("【相关知识库内容】\n");
                for (int i = 0; i < docs.size(); i++) {
                    HealthDocument doc = docs.get(i);
                    sb.append(i + 1).append(". ").append(doc.getTitle()).append("\n");
                    sb.append("   ").append(doc.getContent()).append("\n");
                }
                return sb.toString();
            }
        } catch (Exception e) {
            log.warn("知识库检索失败", e);
        }
        return "";
    }

    /**
     * 单次AI调用生成完整方案
     * 包含5个子方案、整体标题标签、每日焦点，一次返回
     *
     * @param taskId            任务ID（用于进度模拟）
     * @param cycleDays         周期天数
     * @param constitutionName  体质名称
     * @param seasonName        季节名称
     * @param constitutionDetail 体质详情
     * @param preferenceDetail  用户偏好
     * @param knowledgeContext  知识库上下文
     * @return PlanGenerateResult 完整方案结果
     */
    private PlanGenerateResult generateAllContent(String taskId, int cycleDays,
                                                   String constitutionName, String seasonName,
                                                   String constitutionDetail, String preferenceDetail,
                                                   String knowledgeContext) {
        // 从数据库读取方案生成提示词模板
        Map<String, Object> promptVariables = new HashMap<>();
        promptVariables.put("constitutionDetail", constitutionDetail != null ? constitutionDetail : "");
        promptVariables.put("seasonName", seasonName != null ? seasonName : "未知");
        promptVariables.put("knowledgeContext", knowledgeContext != null && !knowledgeContext.contains("未找到相关知识库内容") ? knowledgeContext : "");
        promptVariables.put("cycleDays", String.valueOf(cycleDays));
        promptVariables.put("preferences", preferenceDetail != null ? preferenceDetail : "");
        String userPrompt = aiConfigService.renderPrompt("plan_generation", promptVariables);

        // 系统提示词使用聊天的系统提示词
        String systemPrompt = aiConfigService.getChatSystemPrompt();

        // 启动模拟进度 Timer（30% → 90%，每2秒递增10%）
        AtomicInteger simulatedProgress = new AtomicInteger(30);
        ScheduledExecutorService progressTimer = Executors.newSingleThreadScheduledExecutor();
        progressTimer.scheduleAtFixedRate(() -> {
            int p = simulatedProgress.addAndGet(10);
            if (p <= 90) {
                updateStatus(taskId, 2, p, null, null);
            }
        }, 2, 2, TimeUnit.SECONDS);

        try {
            // 构建 AI 请求
            ChatConfigVO chatConfig = aiConfigService.getChatConfig();

            List<Message> messages = new ArrayList<>();
            messages.add(Message.system(systemPrompt));
            messages.add(Message.user(userPrompt));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(chatConfig.getModel())
                    .messages(messages)
                    .temperature(chatConfig.getTemperature())
                    .maxTokens(chatConfig.getMaxTokens())
                    .jsonObjectResponse(true)
                    .build();

            // 带 3 分钟超时的 AI 调用（使用局部虚拟线程执行器）
            ChatCompletionResponse response;
            try (var vtExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
                Future<ChatCompletionResponse> future =
                    vtExecutor.submit(() -> aiClient.chatCompletions(request, chatConfig));
                response = future.get(3, TimeUnit.MINUTES);
            }

            // 停止模拟进度
            progressTimer.shutdownNow();

            // 校验响应
            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new BusinessException(ExceptionCode.AI_GENERATION_FAILED, "AI返回结果为空，请重试");
            }

            String rawContent = response.getChoices().get(0).getMessage().getContent();
            if (rawContent == null || rawContent.isEmpty()) {
                throw new BusinessException(ExceptionCode.AI_GENERATION_FAILED, "AI返回内容为空，请重试");
            }

            log.info("完整方案AI生成成功, tokens: {}",
                response.getUsage() != null ? response.getUsage().getTotalTokens() : "unknown");

            String content = extractFinalResponse(rawContent);
            log.info("完整方案AI返回内容: {}", content);

            updateStatus(taskId, 2, 95, null, null);
            return parseEntirePlanJson(content, cycleDays, constitutionName, seasonName);

        } catch (TimeoutException e) {
            progressTimer.shutdownNow();
            log.error("完整方案AI生成超时（3分钟）, taskId: {}", taskId);
            throw new BusinessException(ExceptionCode.PLAN_GENERATION_TASK_TIMEOUT, "方案生成超时，请稍后重试");
        } catch (BusinessException e) {
            progressTimer.shutdownNow();
            throw e;
        } catch (Exception e) {
            progressTimer.shutdownNow();
            log.error("完整方案AI生成失败, taskId: {}", taskId, e);
            throw new BusinessException(ExceptionCode.AI_GENERATION_FAILED, "AI服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 解析完整方案JSON
     * 从AI返回的大JSON中提取各子方案、标题标签、每日焦点
     * 关键字段缺失时抛异常
     */
    private PlanGenerateResult parseEntirePlanJson(String content, int cycleDays,
                                                    String constitutionName, String seasonName) {
        PlanGenerateResult result = new PlanGenerateResult();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(content);
        } catch (Exception e) {
            log.error("解析AI返回的JSON失败, content长度: {}", content.length(), e);
            throw new BusinessException(ExceptionCode.AI_GENERATION_FAILED, "方案生成结果解析失败，请重试");
        }

        // 解析5个子方案（必须全部存在）
        result.foodContent = extractSubPlanContent(root, "food");
        result.foodTags = extractSubPlanTags(root, "food", mapper);
        result.exerciseContent = extractSubPlanContent(root, "exercise");
        result.exerciseTags = extractSubPlanTags(root, "exercise", mapper);
        result.acupointContent = extractSubPlanContent(root, "acupoint");
        result.acupointTags = extractSubPlanTags(root, "acupoint", mapper);
        result.meridianContent = extractSubPlanContent(root, "meridian");
        result.meridianTags = extractSubPlanTags(root, "meridian", mapper);
        result.lifestyleContent = extractSubPlanContent(root, "lifestyle");
        result.lifestyleTags = extractSubPlanTags(root, "lifestyle", mapper);

        // 校验：至少3个子方案有内容
        long nonEmptyCount = 0;
        if (!result.foodContent.isEmpty()) nonEmptyCount++;
        if (!result.exerciseContent.isEmpty()) nonEmptyCount++;
        if (!result.acupointContent.isEmpty()) nonEmptyCount++;
        if (!result.meridianContent.isEmpty()) nonEmptyCount++;
        if (!result.lifestyleContent.isEmpty()) nonEmptyCount++;
        if (nonEmptyCount < 3) {
            throw new BusinessException(ExceptionCode.AI_GENERATION_FAILED, "方案生成不完整，请重试");
        }

        // 解析整体标题和标签
        result.planTitle = root.has("title") ? root.get("title").asString() : null;
        if (result.planTitle == null || result.planTitle.isEmpty()) {
            result.planTitle = (seasonName != null ? seasonName : "") + constitutionName + "调理方案";
        }
        JsonNode tagsNode = root.get("tags");
        result.planTags = (tagsNode != null && tagsNode.isArray()) ? mapper.writeValueAsString(tagsNode) : "[]";

        // 解析每日焦点（缺失时可接受）
        JsonNode focusesNode = root.get("dailyFocuses");
        if (focusesNode != null && focusesNode.isArray()) {
            List<String> focuses = new ArrayList<>();
            for (JsonNode focusNode : focusesNode) {
                String focus = focusNode.asString();
                if (focus.length() > 20) {
                    focus = focus.substring(0, 20);
                }
                focuses.add(focus);
            }
            result.dailyFocuses = mapper.writeValueAsString(focuses);
        } else {
            result.dailyFocuses = "[]";
        }

        return result;
    }

    /**
     * 从JSON节点提取子方案的content字段
     */
    private String extractSubPlanContent(JsonNode root, String planName) {
        JsonNode planNode = root.get(planName);
        if (planNode != null) {
            if (planNode.has("content")) {
                return planNode.get("content").asString();
            }
            if (planNode.isObject()) {
                return planNode.toString();
            }
            return planNode.asString();
        }
        return ""; // 缺失返回空，由调用方校验
    }

    /**
     * 从JSON节点提取子方案的tags字段
     */
    private String extractSubPlanTags(JsonNode root, String planName, ObjectMapper mapper) {
        JsonNode planNode = root.get(planName);
        if (planNode != null && planNode.has("tags")) {
            JsonNode tagsNode = planNode.get("tags");
            if (tagsNode.isArray()) {
                try {
                    return mapper.writeValueAsString(tagsNode);
                } catch (Exception e) {
                    log.warn("序列化{}方案标签失败", planName, e);
                }
            }
        }
        return "[]";
    }


    /**
     * 创建方案任务
     */
    private void createTask(String userPlanId, int planType, String taskName, String taskDescription,
                            LocalDate taskDate, int taskCategory, int priority,
                            String resourceType, String resourceId) {
        PlanTask task = new PlanTask();
        task.setUserPlanId(userPlanId);
        task.setPlanType(planType);
        task.setTaskName(taskName);
        task.setTaskDescription(taskDescription);
        task.setTargetCount(1);
        task.setCompletedCount(0);
        task.setTaskDate(taskDate);
        task.setTaskCategory(taskCategory);
        task.setPriority(priority);
        task.setResourceType(resourceType);
        task.setResourceId(resourceId);
        task.setStatus(1);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        planTaskMapper.insert(task);
    }

    /**
     * 基于方案内容生成整个周期的任务
     * 从AI生成的Markdown中提取具体项目（食材、运动、穴位等），分布到每个周期日
     *
     * @param plan           用户方案
     * @param foodContent    饮食方案内容
     * @param exerciseContent 运动方案内容
     * @param acupointContent 穴位方案内容
     * @param meridianContent 经络方案内容
     * @param lifestyleContent 生活方案内容
     */
    private void generateCycleTasks(UserPlan plan,
                                     String foodContent, String exerciseContent,
                                     String acupointContent, String meridianContent,
                                     String lifestyleContent) {
        int cycleDays = plan.getCycleDays() != null ? plan.getCycleDays() : 7;
        LocalDate startDate = plan.getStartDate() != null ? plan.getStartDate() : LocalDate.now();
        String planId = plan.getId();

        // 解析方案内容，提取具体项目
        List<String> foods = com.taolife.plan.util.PlanContentParserUtil.extractFoodItems(foodContent);
        List<String> exercises = com.taolife.plan.util.PlanContentParserUtil.extractExerciseItems(exerciseContent);
        List<String> acupoints = com.taolife.plan.util.PlanContentParserUtil.extractAcupointItems(acupointContent);
        List<String> meridians = com.taolife.plan.util.PlanContentParserUtil.extractMeridianItems(meridianContent);
        List<String> lifestyleItems = com.taolife.plan.util.PlanContentParserUtil.extractLifestyleItems(lifestyleContent);

        // 解析失败时使用兜底值
        if (foods.isEmpty()) foods = List.of("健脾食材", "补气食材", "养胃食材");
        if (exercises.isEmpty()) exercises = List.of("八段锦", "散步", "太极拳");
        if (acupoints.isEmpty()) acupoints = List.of("足三里", "关元", "合谷");
        if (meridians.isEmpty()) meridians = List.of("足阳明胃经", "足太阴脾经");
        if (lifestyleItems.isEmpty()) lifestyleItems = List.of("早睡早起", "午休20分钟", "冥想放松", "泡脚养生");

        int totalTasks = 0;

        for (int day = 0; day < cycleDays; day++) {
            LocalDate date = startDate.plusDays(day);
            int dayTasks = 0;

            // 饮食任务：每天都有
            String food1 = foods.get(day % foods.size());
            String food2 = foods.get((day + 1) % foods.size());
            createTask(planId, 1, "饮食调理",
                    "今日推荐: " + food1 + "、" + food2 + "，按饮食方案搭配三餐",
                    date, 1, 3, "food", planId);
            dayTasks++;

            // 生活任务：每天都有
            String lifestyle = lifestyleItems.get(day % lifestyleItems.size());
            createTask(planId, 5, lifestyle,
                    "按照生活方案执行: " + lifestyle,
                    date, 1, 2, "lifestyle", planId);
            dayTasks++;

            // 运动任务：奇数天（第0,2,4...天，约一半天数）
            if (day % 2 == 0) {
                String exercise = exercises.get(day % exercises.size());
                createTask(planId, 2, "运动锻炼",
                        "今日运动: " + exercise + "，注意运动时长和频率",
                        date, 1, 3, "exercise", planId);
                dayTasks++;
            }

            // 穴位任务：前半段周期的偶数天（第0,2,4...天中，day%4<2时）
            if (day % 4 < 2 && day % 2 == 0) {
                String ap1 = acupoints.get(day % acupoints.size());
                String ap2 = acupoints.get((day + 1) % acupoints.size());
                createTask(planId, 3, "穴位按摩",
                        "按揉穴位: " + ap1 + "、" + ap2 + "，每个3-5分钟",
                        date, 1, 2, "acupoint", planId);
                dayTasks++;
            }

            // 经络任务：前半段周期的偶数天（第0,2,4...天中，day%4>=2时）
            if (day % 4 >= 2 && day % 2 == 0) {
                String meridian = meridians.get(day % meridians.size());
                createTask(planId, 4, "经络调理",
                        "疏通经络: " + meridian + "，按方案方法操作15-20分钟",
                        date, 1, 2, "meridian", planId);
                dayTasks++;
            }

            totalTasks += dayTasks;
        }

        log.info("任务生成完成, planId: {}, cycleDays: {}, totalTasks: {}", planId, cycleDays, totalTasks);
    }

    /**
     * 根据季节编码获取季节名称
     */
    private String getSeasonName(Integer season) {
        if (season == null) return null;
        switch (season) {
            case 1: return "春";
            case 2: return "夏";
            case 3: return "秋";
            case 4: return "冬";
            default: return null;
        }
    }

    /**
     * 根据周期天数计算任务总数
     * 饮食+生活每天都有，运动约一半天数，穴位和经络交替约各占1/4
     */
    private int calculateTotalTasks(int cycleDays) {
        int total = 0;
        for (int day = 0; day < cycleDays; day++) {
            total += 2; // 饮食 + 生活
            if (day % 2 == 0) total++; // 运动
            if (day % 4 < 2) total++;  // 穴位
            if (day % 4 >= 2) total++; // 经络
        }
        return total;
    }

    /**
     * 更新任务状态
     */
    private void updateStatus(String taskId, int status, int progress, String result, String errorMessage) {
        GenerationStatusVO statusVO = taskStatusMap.get(taskId);
        if (statusVO != null) {
            statusVO.setStatus(status);
            statusVO.setProgress(progress);
            statusVO.setMessage(PlanGenerationMessage.getMessage(progress));
            if (result != null) statusVO.setResult(result);
            if (errorMessage != null) statusVO.setErrorMessage(errorMessage);
        }
    }

    /**
     * 保存食材方案
     */
    private String saveFoodPlan(String content, String tags, String constitutionCode, int season) {
        FoodPlan foodPlan = new FoodPlan();
        String id = (String) uuidKeyGeneratorUtil.generate(null, "id");
        foodPlan.setId(id);
        foodPlan.setName("AI生成食材方案");
        foodPlan.setCategory(1);
        foodPlan.setTargetConstitutionCodes(constitutionCode);
        foodPlan.setTargetSeason(String.valueOf(season));
        foodPlan.setGeneratedContent(content);
        foodPlan.setTags(tags);
        foodPlan.setIsDeleted(0);
        foodPlan.setIsDisabled(0);
        foodPlan.setViewCount(0);
        foodPlan.setCollectCount(0);
        foodPlan.setSortOrder(0);
        foodPlan.setCreateTime(LocalDateTime.now());
        foodPlan.setUpdateTime(LocalDateTime.now());
        foodPlanMapper.insert(foodPlan);
        return id;
    }

    /**
     * 保存运动方案
     */
    private String saveExercisePlan(String content, String tags, String constitutionCode, int season) {
        ExercisePlan exercisePlan = new ExercisePlan();
        String id = (String) uuidKeyGeneratorUtil.generate(null, "id");
        exercisePlan.setId(id);
        exercisePlan.setName("AI生成运动方案");
        exercisePlan.setCategory(1);
        exercisePlan.setTargetConstitutionCodes(constitutionCode);
        exercisePlan.setTargetSeason(String.valueOf(season));
        exercisePlan.setGeneratedContent(content);
        exercisePlan.setTags(tags);
        exercisePlan.setIsDeleted(0);
        exercisePlan.setIsDisabled(0);
        exercisePlan.setViewCount(0);
        exercisePlan.setCollectCount(0);
        exercisePlan.setSortOrder(0);
        exercisePlan.setCreateTime(LocalDateTime.now());
        exercisePlan.setUpdateTime(LocalDateTime.now());
        exercisePlanMapper.insert(exercisePlan);
        return id;
    }

    /**
     * 保存穴位方案
     */
    private String saveAcupointPlan(String content, String tags, String constitutionCode, int season) {
        AcupointPlan acupointPlan = new AcupointPlan();
        String id = (String) uuidKeyGeneratorUtil.generate(null, "id");
        acupointPlan.setId(id);
        acupointPlan.setName("AI生成穴位方案");
        acupointPlan.setCategory(1);
        acupointPlan.setTargetConstitutionCodes(constitutionCode);
        acupointPlan.setTargetSeason(String.valueOf(season));
        acupointPlan.setGeneratedContent(content);
        acupointPlan.setTags(tags);
        acupointPlan.setIsDeleted(0);
        acupointPlan.setIsDisabled(0);
        acupointPlan.setViewCount(0);
        acupointPlan.setCollectCount(0);
        acupointPlan.setSortOrder(0);
        acupointPlan.setCreateTime(LocalDateTime.now());
        acupointPlan.setUpdateTime(LocalDateTime.now());
        acupointPlanMapper.insert(acupointPlan);
        return id;
    }

    /**
     * 保存经络方案
     */
    private String saveMeridianPlan(String content, String tags, String constitutionCode, int season) {
        MeridianPlan meridianPlan = new MeridianPlan();
        String id = (String) uuidKeyGeneratorUtil.generate(null, "id");
        meridianPlan.setId(id);
        meridianPlan.setName("AI生成经络方案");
        meridianPlan.setCategory(1);
        meridianPlan.setTargetConstitutionCodes(constitutionCode);
        meridianPlan.setTargetSeason(String.valueOf(season));
        meridianPlan.setGeneratedContent(content);
        meridianPlan.setTags(tags);
        meridianPlan.setIsDeleted(0);
        meridianPlan.setIsDisabled(0);
        meridianPlan.setViewCount(0);
        meridianPlan.setCollectCount(0);
        meridianPlan.setSortOrder(0);
        meridianPlan.setCreateTime(LocalDateTime.now());
        meridianPlan.setUpdateTime(LocalDateTime.now());
        meridianPlanMapper.insert(meridianPlan);
        return id;
    }

    /**
     * 保存生活方案
     */
    private String saveLifestylePlan(String content, String tags, String constitutionCode, int season) {
        LifestylePlan lifestylePlan = new LifestylePlan();
        String id = (String) uuidKeyGeneratorUtil.generate(null, "id");
        lifestylePlan.setId(id);
        lifestylePlan.setName("AI生成生活方案");
        lifestylePlan.setCategory(1);
        lifestylePlan.setTargetConstitutionCodes(constitutionCode);
        lifestylePlan.setTargetSeason(String.valueOf(season));
        lifestylePlan.setGeneratedContent(content);
        lifestylePlan.setTags(tags);
        lifestylePlan.setIsDeleted(0);
        lifestylePlan.setIsDisabled(0);
        lifestylePlan.setViewCount(0);
        lifestylePlan.setCollectCount(0);
        lifestylePlan.setSortOrder(0);
        lifestylePlan.setCreateTime(LocalDateTime.now());
        lifestylePlan.setUpdateTime(LocalDateTime.now());
        lifestylePlanMapper.insert(lifestylePlan);
        return id;
    }

    /**
     * 更新分类方案的用户方案ID
     */
    private void updateCategoryPlanId(String planId, String userPlanId) {
        if (planId == null) return;
        // 根据ID前缀判断类型并更新（简化处理）
        FoodPlan foodPlan = foodPlanMapper.selectById(planId);
        if (foodPlan != null) {
            foodPlan.setUserPlanId(userPlanId);
            foodPlanMapper.update(foodPlan);
            return;
        }
        ExercisePlan exercisePlan = exercisePlanMapper.selectById(planId);
        if (exercisePlan != null) {
            exercisePlan.setUserPlanId(userPlanId);
            exercisePlanMapper.update(exercisePlan);
            return;
        }
        AcupointPlan acupointPlan = acupointPlanMapper.selectById(planId);
        if (acupointPlan != null) {
            acupointPlan.setUserPlanId(userPlanId);
            acupointPlanMapper.update(acupointPlan);
            return;
        }
        MeridianPlan meridianPlan = meridianPlanMapper.selectById(planId);
        if (meridianPlan != null) {
            meridianPlan.setUserPlanId(userPlanId);
            meridianPlanMapper.update(meridianPlan);
            return;
        }
        LifestylePlan lifestylePlan = lifestylePlanMapper.selectById(planId);
        if (lifestylePlan != null) {
            lifestylePlan.setUserPlanId(userPlanId);
            lifestylePlanMapper.update(lifestylePlan);
        }
    }

    /**
     * 完整方案生成结果包装类
     */
    private static class PlanGenerateResult {
        String foodContent, foodTags;
        String exerciseContent, exerciseTags;
        String acupointContent, acupointTags;
        String meridianContent, meridianTags;
        String lifestyleContent, lifestyleTags;
        String planTitle, planTags;
        String dailyFocuses;
    }

    /**
     * 从原始AI响应中提取think标签之后的最终回复内容
     */
    private String extractFinalResponse(String rawContent) {
        if (rawContent == null || rawContent.isEmpty()) {
            return rawContent;
        }
        int thinkEndIndex = rawContent.lastIndexOf("</think>");
        if (thinkEndIndex != -1) {
            return rawContent.substring(thinkEndIndex + "</think>".length()).trim();
        }
        return rawContent.trim();
    }
}
