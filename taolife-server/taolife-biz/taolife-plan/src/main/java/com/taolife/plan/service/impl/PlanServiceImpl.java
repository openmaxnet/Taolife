package com.taolife.plan.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.AcupointPlan;
import com.taolife.plan.entity.ExercisePlan;
import com.taolife.plan.entity.FoodPlan;
import com.taolife.plan.entity.LifestylePlan;
import com.taolife.plan.entity.MeridianPlan;
import com.taolife.plan.entity.UserPlan;
import com.taolife.plan.entity.PlanAdjustRecord;
import com.taolife.plan.entity.PlanTask;
import com.taolife.plan.mapper.AcupointPlanMapper;
import com.taolife.plan.mapper.ExercisePlanMapper;
import com.taolife.plan.mapper.FoodPlanMapper;
import com.taolife.plan.mapper.LifestylePlanMapper;
import com.taolife.plan.mapper.MeridianPlanMapper;
import com.taolife.plan.mapper.UserPlanMapper;
import com.taolife.plan.mapper.PlanAdjustRecordMapper;
import com.taolife.plan.param.PlanQueryParam;
import com.taolife.plan.param.PlanFeedbackParam;
import com.taolife.plan.service.IPlanService;
import com.taolife.plan.service.IPlanTaskService;
import com.taolife.plan.vo.PlanDetailVO;
import com.taolife.plan.vo.PlanHistoryVO;
import com.taolife.plan.vo.PlanSummaryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 健康方案服务实现
 *
 * @author 文二
 * @date 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements IPlanService {

    private final UserPlanMapper userPlanMapper;
    private final IPlanTaskService planTaskService;
    private final PlanAdjustRecordMapper adjustmentRecordMapper;
    private final FoodPlanMapper foodPlanMapper;
    private final ExercisePlanMapper exercisePlanMapper;
    private final AcupointPlanMapper acupointPlanMapper;
    private final MeridianPlanMapper meridianPlanMapper;
    private final LifestylePlanMapper lifestylePlanMapper;

    /**
     * 获取当前用户的健康方案概览
     * 查询最新方案并组装今日任务汇总及各分类方案标签
     *
     * @param accountId 账号ID
     * @return 方案概览VO，无方案时返回null
     */
    @Override
    public PlanSummaryVO getHealthPlan(String accountId) {
        UserPlan plan = userPlanMapper.selectLatestPlan(accountId);
        if (plan == null) {
            return null;
        }

        PlanSummaryVO vo = new PlanSummaryVO();
        vo.setId(plan.getId());
        vo.setConstitutionCode(plan.getConstitutionCode());
        vo.setConstitutionName(plan.getConstitutionName());
        vo.setStatus(plan.getStatus());
        vo.setPlanTitle(plan.getPlanTitle());
        vo.setPlanTags(plan.getPlanTags());
        vo.setTodayFocus(extractTodayFocus(plan));

        // 获取今日任务汇总
        List<PlanTask> tasks = planTaskService.getTasksByDate(plan.getId(), LocalDate.now());
        PlanSummaryVO.TaskSummary taskSummary = new PlanSummaryVO.TaskSummary();
        taskSummary.setTodayTaskCount(tasks.size());
        long completedCount = tasks.stream().filter(t -> t.getStatus() != null && t.getStatus() == 2).count();
        taskSummary.setCompletedCount((int) completedCount);
        vo.setTaskSummary(taskSummary);

        // 获取各分类方案的标签
        vo.setFoodPlanTags(foodPlanMapper.selectTagsById(plan.getFoodPlanId()));
        vo.setExercisePlanTags(exercisePlanMapper.selectTagsById(plan.getExercisePlanId()));
        vo.setAcupointPlanTags(acupointPlanMapper.selectTagsById(plan.getAcupointPlanId()));
        vo.setMeridianPlanTags(meridianPlanMapper.selectTagsById(plan.getMeridianPlanId()));
        vo.setLifestylePlanTags(lifestylePlanMapper.selectTagsById(plan.getLifestylePlanId()));

        return vo;
    }

    /**
     * 获取健康方案详情
     * 查询方案基础信息并聚合5个子方案的内容和今日任务汇总
     *
     * @param id 方案ID
     * @return 方案详情VO
     */
    @Override
    public PlanDetailVO getHealthPlanDetail(String id) {
        UserPlan plan = userPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException(ExceptionCode.PLAN_NOT_FOUND, "方案不存在");
        }

        PlanDetailVO vo = buildPlanDetailVO(plan);

        // 获取今日任务汇总
        List<PlanTask> tasks = planTaskService.getTasksByDate(plan.getId(), LocalDate.now());
        PlanDetailVO.TaskSummary taskSummary = new PlanDetailVO.TaskSummary();
        taskSummary.setTodayTaskCount(tasks.size());
        long completedCount = tasks.stream().filter(t -> t.getStatus() != null && t.getStatus() == 2).count();
        taskSummary.setCompletedCount((int) completedCount);
        vo.setTaskSummary(taskSummary);

        return vo;
    }

    /**
     * 分页查询用户的历史方案
     * 按时间范围筛选并转换为历史VO列表
     *
     * @param accountId 账号ID
     * @param param     查询参数（含分页和时间范围）
     * @return 历史方案分页结果
     */
    @Override
    public PageResult<PlanHistoryVO> getPlanHistoryPage(String accountId, PlanQueryParam param) {
        LocalDate startDate = (param.getStartDate() != null && !param.getStartDate().isEmpty())
                ? LocalDate.parse(param.getStartDate()) : null;
        LocalDate endDate = (param.getEndDate() != null && !param.getEndDate().isEmpty())
                ? LocalDate.parse(param.getEndDate()) : null;

        Page<UserPlan> page = userPlanMapper.selectHistoryPage(
                new Page<>(param.getPageNo(), param.getPageSize()), accountId, startDate, endDate);

        List<PlanHistoryVO> voList = page.getRecords().stream()
                .map(this::convertToHistoryVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 提交方案反馈或调整效果反馈
     * 根据反馈类型分别更新方案记录或调整记录的反馈内容
     *
     * @param accountId 账号ID
     * @param param     反馈参数（含类型、目标ID、内容、评分）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitFeedback(String accountId, PlanFeedbackParam param) {
        switch (param.getFeedbackType()) {
            case 1: {
                // 方案反馈
                UserPlan plan = userPlanMapper.selectById(param.getTargetId());
                if (plan == null) {
                    throw new BusinessException(ExceptionCode.PLAN_NOT_FOUND, "方案记录不存在");
                }
                if (!plan.getAccountId().equals(accountId)) {
                    throw new BusinessException(ExceptionCode.OPERATION_NOT_ALLOWED, "无权操作此方案");
                }
                plan.setUserFeedback(param.getContent());
                plan.setUserRating(param.getRating());
                plan.setUpdateTime(LocalDateTime.now());
                userPlanMapper.update(plan);
                break;
            }
            case 2: {
                // 调整效果反馈
                PlanAdjustRecord record = adjustmentRecordMapper.selectById(param.getTargetId());
                if (record == null) {
                    throw new BusinessException(ExceptionCode.PLAN_ADJUSTMENT_NOT_FOUND, "调整记录不存在");
                }
                record.setUserFeedback(param.getContent());
                record.setEffectivenessScore(param.getRating());
                adjustmentRecordMapper.update(record);
                break;
            }
            default:
                throw new BusinessException(ExceptionCode.PARAM_ERROR, "反馈类型不正确");
        }
    }

    /**
     * 构建方案详情VO
     * 将方案实体转换为包含5个子方案内容的详情VO
     *
     * @param plan 用户方案实体
     * @return 方案详情VO
     */
    private PlanDetailVO buildPlanDetailVO(UserPlan plan) {
        PlanDetailVO vo = new PlanDetailVO();
        vo.setId(plan.getId());
        vo.setConstitutionCode(plan.getConstitutionCode());
        vo.setConstitutionName(plan.getConstitutionName());
        vo.setSeason(plan.getSeason());
        vo.setSeasonName(plan.getSeasonName());
        vo.setStartDate(plan.getStartDate() != null ? plan.getStartDate().toString() : null);
        vo.setEndDate(plan.getEndDate() != null ? plan.getEndDate().toString() : null);
        vo.setStatus(plan.getStatus());
        vo.setCycleDays(plan.getCycleDays());
        vo.setCompletionRate(plan.getCompletionRate());

        // 从分类方案表中获取内容
        if (plan.getFoodPlanId() != null) {
            FoodPlan foodPlan = foodPlanMapper.selectById(plan.getFoodPlanId());
            if (foodPlan != null) {
                vo.setFoodPlanContent(foodPlan.getGeneratedContent());
                vo.setFoodPlanTags(foodPlan.getTags());
            }
        }
        if (plan.getExercisePlanId() != null) {
            ExercisePlan exercisePlan = exercisePlanMapper.selectById(plan.getExercisePlanId());
            if (exercisePlan != null) {
                vo.setExercisePlanContent(exercisePlan.getGeneratedContent());
                vo.setExercisePlanTags(exercisePlan.getTags());
            }
        }
        if (plan.getAcupointPlanId() != null) {
            AcupointPlan acupointPlan = acupointPlanMapper.selectById(plan.getAcupointPlanId());
            if (acupointPlan != null) {
                vo.setAcupointPlanContent(acupointPlan.getGeneratedContent());
                vo.setAcupointPlanTags(acupointPlan.getTags());
            }
        }
        if (plan.getMeridianPlanId() != null) {
            MeridianPlan meridianPlan = meridianPlanMapper.selectById(plan.getMeridianPlanId());
            if (meridianPlan != null) {
                vo.setMeridianPlanContent(meridianPlan.getGeneratedContent());
                vo.setMeridianPlanTags(meridianPlan.getTags());
            }
        }
        if (plan.getLifestylePlanId() != null) {
            LifestylePlan lifestylePlan = lifestylePlanMapper.selectById(plan.getLifestylePlanId());
            if (lifestylePlan != null) {
                vo.setLifestylePlanContent(lifestylePlan.getGeneratedContent());
                vo.setLifestylePlanTags(lifestylePlan.getTags());
            }
        }

        vo.setUserNotes(plan.getUserNotes());
        vo.setAiAdjustmentCount(plan.getAiAdjustmentCount());
        vo.setPlanTitle(plan.getPlanTitle());
        vo.setPlanTags(plan.getPlanTags());
        vo.setTodayFocus(extractTodayFocus(plan));
        return vo;
    }

    /**
     * 将方案实体转换为历史VO
     *
     * @param plan 用户方案实体
     * @return 历史方案VO
     */
    private PlanHistoryVO convertToHistoryVO(UserPlan plan) {
        PlanHistoryVO vo = new PlanHistoryVO();
        vo.setId(plan.getId());
        vo.setPlanDate(plan.getPlanDate() != null ? plan.getPlanDate().toString() : null);
        vo.setConstitutionName(plan.getConstitutionName());
        vo.setSeasonName(plan.getSeasonName());
        vo.setCompletionRate(plan.getCompletionRate());
        vo.setTotalTasks(plan.getTotalTasks());
        vo.setCompletedTasks(plan.getCompletedTasks());
        vo.setUserRating(plan.getUserRating());
        vo.setIsEffective(plan.getIsEffective());
        vo.setAdjustmentCount(plan.getAdjustmentCount());
        vo.setPlanTitle(plan.getPlanTitle());
        vo.setPlanTags(plan.getPlanTags());
        return vo;
    }

    /**
     * 从每日焦点数组中提取当天的焦点句子
     */
    private String extractTodayFocus(UserPlan plan) {
        if (plan == null || plan.getDailyFocuses() == null || plan.getDailyFocuses().isEmpty()) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<String> focuses = mapper.readValue(plan.getDailyFocuses(),
            new TypeReference<List<String>>() {});
            if (focuses == null || focuses.isEmpty()) {
                return null;
            }
            // 计算今天是方案第几天（从0开始）
            LocalDate startDate = plan.getStartDate();
            if (startDate == null) {
                return focuses.get(0);
            }
            long dayIndex = java.time.temporal.ChronoUnit.DAYS.between(startDate, LocalDate.now());
            int index = (int) dayIndex;
            if (index < 0) index = 0;
            if (index >= focuses.size()) index = focuses.size() - 1;
            return focuses.get(index);
        } catch (Exception e) {
            log.warn("解析每日焦点失败, dailyFocuses: {}", plan.getDailyFocuses(), e);
            return null;
        }
    }
}
