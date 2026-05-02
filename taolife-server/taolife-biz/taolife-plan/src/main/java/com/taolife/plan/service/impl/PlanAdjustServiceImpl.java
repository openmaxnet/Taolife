package com.taolife.plan.service.impl;

import com.mybatisflex.core.paginate.Page;
import tools.jackson.core.type.TypeReference;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aicore.client.AiClient;
import com.taolife.aicore.config.ChatConfigVO;
import com.taolife.aicore.model.ChatCompletionRequest;
import com.taolife.aicore.model.ChatCompletionResponse;
import com.taolife.aicore.model.Message;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.common.utils.UUIDKeyGeneratorUtil;
import com.taolife.plan.entity.AcupointPlan;
import com.taolife.plan.entity.ExercisePlan;
import com.taolife.plan.entity.FoodPlan;
import com.taolife.plan.entity.LifestylePlan;
import com.taolife.plan.entity.MeridianPlan;
import com.taolife.plan.entity.UserPlan;
import com.taolife.plan.entity.PlanAdjustRecord;
import com.taolife.plan.entity.PlanAdjustSuggestion;
import com.taolife.plan.enums.PlanStatusEnum;
import com.taolife.plan.enums.PlanTypeEnum;
import com.taolife.plan.enums.SeasonEnum;
import com.taolife.plan.mapper.AcupointPlanMapper;
import com.taolife.plan.mapper.ExercisePlanMapper;
import com.taolife.plan.mapper.FoodPlanMapper;
import com.taolife.plan.mapper.LifestylePlanMapper;
import com.taolife.plan.mapper.MeridianPlanMapper;
import com.taolife.plan.mapper.UserPlanMapper;
import com.taolife.plan.mapper.PlanAdjustRecordMapper;
import com.taolife.plan.mapper.PlanAdjustSuggestionMapper;
import com.taolife.plan.mapper.PlanTaskMapper;
import com.taolife.plan.param.AiAdjustParam;
import com.taolife.plan.param.BatchAiAdjustParam;
import com.taolife.plan.param.PlanAdjustRecordQueryParam;
import com.taolife.plan.param.ManualAdjustmentParam;
import com.taolife.plan.service.IPlanAdjustService;
import com.taolife.plan.vo.AdjustmentRecordVO;
import com.taolife.plan.vo.AiAdjustResultVO;
import com.taolife.plan.vo.BatchAiAdjustResultVO;
import com.taolife.plan.vo.SubPlanAdjustItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 方案调整服务实现类
 * 实现方案调整相关的具体业务逻辑
 *
 * @author 文二
 * @date 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanAdjustServiceImpl implements IPlanAdjustService {

    private final PlanAdjustRecordMapper adjustmentRecordMapper;
    private final UserPlanMapper userPlanMapper;
    private final FoodPlanMapper foodPlanMapper;
    private final ExercisePlanMapper exercisePlanMapper;
    private final AcupointPlanMapper acupointPlanMapper;
    private final MeridianPlanMapper meridianPlanMapper;
    private final LifestylePlanMapper lifestylePlanMapper;
    private final AiClient aiClient;
    private final IAiConfigService aiConfigService;
    private final PlanAdjustSuggestionMapper adjustmentSuggestionMapper;
    private final PlanTaskMapper planTaskMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 手动调整方案
     * 记录调整前后的内容，并更新用户方案
     *
     * @param accountId 账号ID
     * @param param     手动调整参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualAdjust(String accountId, ManualAdjustmentParam param) {
        // 查询用户方案
        UserPlan plan = userPlanMapper.selectById(param.getUserPlanId());
        if (plan == null) {
            throw new BusinessException(ExceptionCode.PLAN_NOT_FOUND, "方案不存在");
        }

        // 校验方案归属
        if (!plan.getAccountId().equals(accountId)) {
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "无权操作此方案");
        }

        // 根据方案类型获取调整前的内容
        String beforeContent = getPlanContent(plan, param.getPlanType());

        // 创建调整记录
        PlanAdjustRecord record = new PlanAdjustRecord();
        record.setUserPlanId(param.getUserPlanId());
        record.setPlanType(param.getPlanType());
        record.setAdjustmentType(2);
        record.setAdjustmentReason(param.getAdjustmentReason());
        record.setBeforeContent(beforeContent);
        record.setAfterContent(param.getAfterContent());
        record.setCreateTime(LocalDateTime.now());
        adjustmentRecordMapper.insert(record);

        // 更新方案内容
        updatePlanContent(plan, param.getPlanType(), param.getAfterContent());
        plan.setAiAdjustmentCount(plan.getAiAdjustmentCount() != null ? plan.getAiAdjustmentCount() + 1 : 1);
        plan.setLastAdjustmentTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());
        userPlanMapper.update(plan);

        log.info("用户手动调整方案成功, userPlanId: {}, planType: {}", param.getUserPlanId(), param.getPlanType());
    }

    /**
     * 分页查询调整记录
     *
     * @param userPlanId 用户方案ID
     * @param param      查询参数
     * @return 调整记录分页结果
     */
    @Override
    public PageResult<AdjustmentRecordVO> getAdjustmentRecordPage(String userPlanId, PlanAdjustRecordQueryParam param) {
        Page<PlanAdjustRecord> page = adjustmentRecordMapper.selectByUserPlanId(userPlanId, param);

        List<AdjustmentRecordVO> voList;
        if (page.getRecords() != null) {
            voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        } else {
            voList = Collections.emptyList();
        }

        return new PageResult<>(voList, (int) page.getPageNumber(), (int) page.getPageSize(), page.getTotalRow());
    }

    /**
     * 获取智能调整推荐标签
     *
     * @param accountId  账号ID
     * @param userPlanId 用户方案ID
     * @param planType   方案类型
     * @return 推荐标签列表
     */
    @Override
    public List<String> getAdjustmentSuggestions(String accountId, String userPlanId, Integer planType) {
        // 1. 校验方案：存在、归属、进行中
        UserPlan plan = userPlanMapper.selectById(userPlanId);
        validatePlanAccess(plan, accountId);

        // 2. 根据方案类型获取子方案ID
        String subPlanId = getSubPlanId(plan, planType);
        if (subPlanId == null) return Collections.emptyList();

        // 3. 查询缓存
        PlanAdjustSuggestion cached = adjustmentSuggestionMapper.selectBySubPlanId(subPlanId);

        // 4. 判断是否需要重新生成
        boolean needRegenerate = false;
        if (cached == null || cached.getSuggestions() == null) {
            needRegenerate = true;
        } else if (cached.getSuggestionsTime() != null) {
            // 检查方案是否在生成之后更新过
            if (plan.getUpdateTime() != null && cached.getSuggestionsTime().isBefore(plan.getUpdateTime())) {
                needRegenerate = true;
            }
            // 检查季节变化
            int currentSeason = SeasonEnum.getCurrentSeason().getValue();
            if (plan.getSeason() != null && plan.getSeason() != currentSeason) {
                needRegenerate = true;
            }
            // 限流：最少间隔1小时
            if (cached.getSuggestionsTime().plusHours(1).isAfter(LocalDateTime.now())) {
                needRegenerate = false; // 即使其他条件满足，也受限流控制
            }
        }

        // 5. 缓存有效则直接返回
        if (!needRegenerate && cached != null && cached.getSuggestions() != null) {
            try {
                return objectMapper.readValue(cached.getSuggestions(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                log.warn("解析缓存建议失败，重新生成", e);
                needRegenerate = true;
            }
        }

        // 6. 通过AI生成建议
        String currentContent = getPlanContent(plan, planType);
        if (currentContent == null || currentContent.isEmpty()) return Collections.emptyList();

        String constitutionDetail = plan.getConstitutionName() != null ? plan.getConstitutionName() : "未知";
        String seasonName = SeasonEnum.getCurrentSeason().getName();
        String planTypeName = PlanTypeEnum.getByValue(planType).getDisplayName();

        // 获取按方案类型的完成率
        int completionRate = getCompletionRateByType(plan, planType);

        Map<String, Object> variables = new HashMap<>();
        variables.put("constitutionDetail", constitutionDetail);
        variables.put("seasonName", seasonName);
        variables.put("planTypeName", planTypeName);
        variables.put("completionRate", String.valueOf(completionRate));
        variables.put("currentContent", currentContent);

        String prompt = aiConfigService.renderPrompt("plan_adjustment_suggestion", variables);
        String aiResponse = callAi(prompt);

        if (aiResponse == null || aiResponse.isEmpty()) {
            throw new BusinessException(ExceptionCode.AI_GENERATION_FAILED, "AI生成建议失败，请稍后重试");
        }

        List<String> suggestions;
        try {
            // AI可能直接返回JSON数组或包裹在Markdown代码块中
            String json = aiResponse.trim();
            if (json.startsWith("```")) {
                int start = json.indexOf('[');
                int end = json.lastIndexOf(']');
                if (start >= 0 && end > start) {
                    json = json.substring(start, end + 1);
                }
            }
            suggestions = objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("解析AI建议失败: {}", aiResponse, e);
            return Collections.emptyList();
        }

        // 7. 缓存结果
        PlanAdjustSuggestion entity = cached != null ? cached : new PlanAdjustSuggestion();
        entity.setSubPlanId(subPlanId);
        entity.setPlanType(planType);
        try {
            entity.setSuggestions(objectMapper.writeValueAsString(suggestions));
        } catch (Exception e) {
            entity.setSuggestions(aiResponse);
        }
        entity.setSuggestionsTime(LocalDateTime.now());

        if (cached != null) {
            adjustmentSuggestionMapper.update(entity);
        } else {
            entity.setId(String.valueOf(new UUIDKeyGeneratorUtil().generate(null, "id")));
            adjustmentSuggestionMapper.insert(entity);
        }

        return suggestions;
    }

    /**
     * AI调整方案（单条）
     *
     * @param accountId 账号ID
     * @param param     调整参数
     * @return 调整结果（含预览）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiAdjustResultVO aiAdjust(String accountId, AiAdjustParam param) {
        UserPlan plan = userPlanMapper.selectById(param.getUserPlanId());
        validatePlanAccess(plan, accountId);
        if (plan.getStatus() != PlanStatusEnum.ACTIVE.getValue()) {
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "只有生效中的方案可以调整");
        }

        String beforeContent = getPlanContent(plan, param.getPlanType());
        if (beforeContent == null || beforeContent.isEmpty()) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "方案内容不存在");
        }

        String constitutionDetail = plan.getConstitutionName() != null ? plan.getConstitutionName() : "未知";
        String planTypeName = PlanTypeEnum.getByValue(param.getPlanType()).getDisplayName();

        Map<String, Object> variables = new HashMap<>();
        variables.put("planTypeName", planTypeName);
        variables.put("constitutionDetail", constitutionDetail);
        variables.put("currentContent", beforeContent);
        variables.put("adjustmentRequest", param.getAdjustmentRequest());

        String prompt = aiConfigService.renderPrompt("plan_adjustment", variables);
        String afterContent = callAi(prompt);

        if (afterContent == null || afterContent.isEmpty()) {
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "AI调整失败，请稍后重试");
        }

        // 创建调整记录（未确认）
        PlanAdjustRecord record = new PlanAdjustRecord();
        record.setUserPlanId(param.getUserPlanId());
        record.setPlanType(param.getPlanType());
        record.setAdjustmentType(1); // AI自动调整
        record.setAdjustmentReason(param.getAdjustmentRequest());
        record.setBeforeContent(beforeContent);
        record.setAfterContent(afterContent);
        record.setAiPrompt(prompt);
        record.setAiResponse(afterContent);
        record.setIsConfirmed(0);
        record.setCreateTime(LocalDateTime.now());

        ChatConfigVO chatConfig = aiConfigService.getChatConfig();
        record.setAiModel(chatConfig.getModel());

        adjustmentRecordMapper.insert(record);

        AiAdjustResultVO result = new AiAdjustResultVO();
        result.setBeforeContent(beforeContent);
        result.setAfterContent(afterContent);
        result.setAdjustmentId(record.getId());
        return result;
    }

    /**
     * AI批量调整方案
     *
     * @param accountId 账号ID
     * @param param     批量调整参数
     * @return 批量调整结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchAiAdjustResultVO batchAiAdjust(String accountId, BatchAiAdjustParam param) {
        UserPlan plan = userPlanMapper.selectById(param.getUserPlanId());
        validatePlanAccess(plan, accountId);
        if (plan.getStatus() != PlanStatusEnum.ACTIVE.getValue()) {
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "只有生效中的方案可以调整");
        }

        // 读取全部5个子方案内容
        Map<String, String> planContents = new HashMap<>();
        for (PlanTypeEnum typeEnum : PlanTypeEnum.values()) {
            String content = getPlanContent(plan, typeEnum.getValue());
            planContents.put(typeEnum.getKey(), content != null ? content : "（无内容）");
        }

        String constitutionDetail = plan.getConstitutionName() != null ? plan.getConstitutionName() : "未知";
        String seasonName = SeasonEnum.getCurrentSeason().getName();

        Map<String, Object> variables = new HashMap<>();
        variables.put("constitutionDetail", constitutionDetail);
        variables.put("seasonName", seasonName);
        variables.put("foodContent", planContents.get("food"));
        variables.put("exerciseContent", planContents.get("exercise"));
        variables.put("acupointContent", planContents.get("acupoint"));
        variables.put("meridianContent", planContents.get("meridian"));
        variables.put("lifestyleContent", planContents.get("lifestyle"));
        variables.put("adjustmentRequest", param.getAdjustmentRequest());

        String prompt = aiConfigService.renderPrompt("plan_batch_adjustment", variables);
        String aiResponse = callAi(prompt);

        if (aiResponse == null || aiResponse.isEmpty()) {
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "AI调整失败，请稍后重试");
        }

        // 解析JSON响应
        Map<String, String> adjustedPlans;
        try {
            String json = aiResponse.trim();
            if (json.startsWith("```")) {
                int start = json.indexOf('{');
                int end = json.lastIndexOf('}');
                if (start >= 0 && end > start) json = json.substring(start, end + 1);
            }
            adjustedPlans = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.error("解析批量调整结果失败: {}", aiResponse, e);
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "AI返回格式异常，请重试");
        }

        ChatConfigVO chatConfig = aiConfigService.getChatConfig();
        List<SubPlanAdjustItem> items = new ArrayList<>();

        for (Map.Entry<String, String> entry : adjustedPlans.entrySet()) {
            String key = entry.getKey();
            PlanTypeEnum planTypeEnum = PlanTypeEnum.getByKey(key);
            if (planTypeEnum == null) continue;
            Integer planType = planTypeEnum.getValue();

            String beforeContent = getPlanContent(plan, planType);
            String afterContent = entry.getValue();

            if (afterContent == null || afterContent.isEmpty()) continue;

            PlanAdjustRecord record = new PlanAdjustRecord();
            record.setUserPlanId(param.getUserPlanId());
            record.setPlanType(planType);
            record.setAdjustmentType(1);
            record.setAdjustmentReason(param.getAdjustmentRequest());
            record.setBeforeContent(beforeContent);
            record.setAfterContent(afterContent);
            record.setAiModel(chatConfig.getModel());
            record.setAiPrompt(prompt);
            record.setAiResponse(afterContent);
            record.setIsConfirmed(0);
            record.setCreateTime(LocalDateTime.now());
            adjustmentRecordMapper.insert(record);

            SubPlanAdjustItem item = new SubPlanAdjustItem();
            item.setPlanType(planType);
            item.setPlanTypeName(PlanTypeEnum.getByValue(planType).getDisplayName());
            item.setBeforeContent(beforeContent);
            item.setAfterContent(afterContent);
            item.setAdjustmentId(record.getId());
            items.add(item);
        }

        BatchAiAdjustResultVO result = new BatchAiAdjustResultVO();
        result.setAdjustments(items);
        return result;
    }

    /**
     * 确认AI调整（单条）
     *
     * @param accountId     账号ID
     * @param adjustmentId  调整记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmAiAdjust(String accountId, String adjustmentId) {
        PlanAdjustRecord record = adjustmentRecordMapper.selectById(adjustmentId);
        if (record == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "调整记录不存在");
        }
        if (!Integer.valueOf(0).equals(record.getIsConfirmed())) {
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "该调整已确认");
        }

        UserPlan plan = userPlanMapper.selectById(record.getUserPlanId());
        if (plan == null) {
            throw new BusinessException(ExceptionCode.PLAN_NOT_FOUND, "方案不存在");
        }
        if (!plan.getAccountId().equals(accountId)) {
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "无权操作此方案");
        }
        if (plan.getStatus() != PlanStatusEnum.ACTIVE.getValue()) {
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "只有生效中的方案可以调整");
        }

        // 更新子方案内容
        updatePlanContent(plan, record.getPlanType(), record.getAfterContent());

        // 更新方案元数据
        plan.setAiAdjustmentCount(plan.getAiAdjustmentCount() != null ? plan.getAiAdjustmentCount() + 1 : 1);
        plan.setLastAdjustmentTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());
        userPlanMapper.update(plan);

        // 标记为已确认
        record.setIsConfirmed(1);
        adjustmentRecordMapper.update(record);

        // 清除该子方案的建议缓存
        String subPlanId = getSubPlanId(plan, record.getPlanType());
        if (subPlanId != null) {
            PlanAdjustSuggestion cached = adjustmentSuggestionMapper.selectBySubPlanId(subPlanId);
            if (cached != null) {
                adjustmentSuggestionMapper.deleteById(cached.getId());
            }
        }

        log.info("AI方案调整确认成功, recordId: {}, planType: {}", adjustmentId, record.getPlanType());
    }

    /**
     * 批量确认AI调整
     *
     * @param accountId      账号ID
     * @param adjustmentIds  调整记录ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchConfirmAiAdjust(String accountId, List<String> adjustmentIds) {
        for (String id : adjustmentIds) {
            confirmAiAdjust(accountId, id);
        }
    }

    // ========== 辅助方法 ==========

    /**
     * 校验方案归属权限
     *
     * @param plan      用户方案
     * @param accountId 账号ID
     */
    private void validatePlanAccess(UserPlan plan, String accountId) {
        if (plan == null) {
            throw new BusinessException(ExceptionCode.PLAN_NOT_FOUND, "方案不存在");
        }
        if (!plan.getAccountId().equals(accountId)) {
            throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "无权操作此方案");
        }
    }

    /**
     * 根据方案类型获取子方案ID
     *
     * @param plan     用户方案
     * @param planType 方案类型
     * @return 子方案ID
     */
    private String getSubPlanId(UserPlan plan, Integer planType) {
        if (planType == null) return null;
        switch (planType) {
            case 1: return plan.getFoodPlanId();
            case 2: return plan.getExercisePlanId();
            case 3: return plan.getAcupointPlanId();
            case 4: return plan.getMeridianPlanId();
            case 5: return plan.getLifestylePlanId();
            default: return null;
        }
    }

    /**
     * 获取按方案类型的完成率
     *
     * @param plan     用户方案
     * @param planType 方案类型
     * @return 完成率
     */
    private int getCompletionRateByType(UserPlan plan, Integer planType) {
        long total = planTaskMapper.countByUserPlanIdAndType(plan.getId(), planType);
        if (total == 0) return 0;
        long completed = planTaskMapper.countCompletedByUserPlanIdAndType(plan.getId(), planType);
        return (int) (completed * 100 / total);
    }

    /**
     * 调用AI接口
     *
     * @param userPrompt 用户提示词
     * @return AI响应内容
     */
    private String callAi(String userPrompt) {
        ChatConfigVO chatConfig = aiConfigService.getChatConfig();

        List<Message> messages = new ArrayList<>();
        messages.add(Message.user(userPrompt));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(chatConfig.getModel())
                .messages(messages)
                .temperature(0.5)
                .maxTokens(chatConfig.getMaxTokens())
                .build();

        ChatCompletionResponse response = aiClient.chatCompletions(request, chatConfig);
        if (response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        return null;
    }

    /**
     * 转换为VO
     *
     * @param record 调整记录实体
     * @return 调整记录VO
     */
    private AdjustmentRecordVO convertToVO(PlanAdjustRecord record) {
        AdjustmentRecordVO vo = new AdjustmentRecordVO();
        vo.setId(record.getId());
        vo.setPlanType(record.getPlanType());
        vo.setAdjustmentType(record.getAdjustmentType());
        vo.setAdjustmentReason(record.getAdjustmentReason());
        vo.setBeforeContent(truncateText(record.getBeforeContent(), 200));
        vo.setAfterContent(truncateText(record.getAfterContent(), 200));
        vo.setEffectivenessScore(record.getEffectivenessScore());
        vo.setCreateTime(formatDateTime(record.getCreateTime()));
        return vo;
    }

    /**
     * 根据方案类型获取方案内容
     *
     * @param plan     用户方案
     * @param planType 方案类型
     * @return 方案内容JSON
     */
    private String getPlanContent(UserPlan plan, Integer planType) {
        if (planType == null) {
            return null;
        }
        switch (planType) {
            case 1:
                if (plan.getFoodPlanId() != null) {
                    FoodPlan foodPlan = foodPlanMapper.selectById(plan.getFoodPlanId());
                    return foodPlan != null ? foodPlan.getGeneratedContent() : null;
                }
                return null;
            case 2:
                if (plan.getExercisePlanId() != null) {
                    ExercisePlan exercisePlan = exercisePlanMapper.selectById(plan.getExercisePlanId());
                    return exercisePlan != null ? exercisePlan.getGeneratedContent() : null;
                }
                return null;
            case 3:
                if (plan.getAcupointPlanId() != null) {
                    AcupointPlan acupointPlan = acupointPlanMapper.selectById(plan.getAcupointPlanId());
                    return acupointPlan != null ? acupointPlan.getGeneratedContent() : null;
                }
                return null;
            case 4:
                if (plan.getMeridianPlanId() != null) {
                    MeridianPlan meridianPlan = meridianPlanMapper.selectById(plan.getMeridianPlanId());
                    return meridianPlan != null ? meridianPlan.getGeneratedContent() : null;
                }
                return null;
            case 5:
                if (plan.getLifestylePlanId() != null) {
                    LifestylePlan lifestylePlan = lifestylePlanMapper.selectById(plan.getLifestylePlanId());
                    return lifestylePlan != null ? lifestylePlan.getGeneratedContent() : null;
                }
                return null;
            default:
                return null;
        }
    }

    /**
     * 根据方案类型更新方案内容
     *
     * @param plan      用户方案
     * @param planType  方案类型
     * @param content   新内容
     */
    private void updatePlanContent(UserPlan plan, Integer planType, String content) {
        if (planType == null) {
            return;
        }
        switch (planType) {
            case 1:
                if (plan.getFoodPlanId() != null) {
                    FoodPlan foodPlan = foodPlanMapper.selectById(plan.getFoodPlanId());
                    if (foodPlan != null) {
                        foodPlan.setGeneratedContent(content);
                        foodPlanMapper.update(foodPlan);
                    }
                }
                break;
            case 2:
                if (plan.getExercisePlanId() != null) {
                    ExercisePlan exercisePlan = exercisePlanMapper.selectById(plan.getExercisePlanId());
                    if (exercisePlan != null) {
                        exercisePlan.setGeneratedContent(content);
                        exercisePlanMapper.update(exercisePlan);
                    }
                }
                break;
            case 3:
                if (plan.getAcupointPlanId() != null) {
                    AcupointPlan acupointPlan = acupointPlanMapper.selectById(plan.getAcupointPlanId());
                    if (acupointPlan != null) {
                        acupointPlan.setGeneratedContent(content);
                        acupointPlanMapper.update(acupointPlan);
                    }
                }
                break;
            case 4:
                if (plan.getMeridianPlanId() != null) {
                    MeridianPlan meridianPlan = meridianPlanMapper.selectById(plan.getMeridianPlanId());
                    if (meridianPlan != null) {
                        meridianPlan.setGeneratedContent(content);
                        meridianPlanMapper.update(meridianPlan);
                    }
                }
                break;
            case 5:
                if (plan.getLifestylePlanId() != null) {
                    LifestylePlan lifestylePlan = lifestylePlanMapper.selectById(plan.getLifestylePlanId());
                    if (lifestylePlan != null) {
                        lifestylePlan.setGeneratedContent(content);
                        lifestylePlanMapper.update(lifestylePlan);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 截断文本
     *
     * @param text      原始文本
     * @param maxLength 最大长度
     * @return 截断后的文本
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    /**
     * 格式化日期时间
     *
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
