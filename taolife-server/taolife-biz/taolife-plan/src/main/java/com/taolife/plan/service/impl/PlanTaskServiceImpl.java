package com.taolife.plan.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.UserPlan;
import com.taolife.plan.entity.PlanTask;
import com.taolife.plan.mapper.UserPlanMapper;
import com.taolife.plan.mapper.PlanTaskMapper;
import com.taolife.plan.param.PlanTaskQueryParam;
import com.taolife.plan.param.TaskCompleteParam;
import com.taolife.plan.service.IPlanTaskService;
import com.taolife.fee.service.IPointsService;
import com.taolife.fee.service.IPointsRuleService;
import com.taolife.plan.vo.PlanTaskDetailVO;
import com.taolife.plan.vo.PlanTaskListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 方案任务服务实现类
 * 实现方案任务管理的具体业务逻辑
 *
 * @author 文二
 * @date 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanTaskServiceImpl implements IPlanTaskService {

    private final PlanTaskMapper planTaskMapper;
    private final UserPlanMapper userPlanMapper;
    private final IPointsService pointsService;
    private final IPointsRuleService pointsRuleService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 分页查询任务列表
     * 根据用户方案ID分页查询任务信息列表
     *
     * @param userPlanId 用户方案ID
     * @param param 查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<PlanTaskListVO> getTaskPage(String userPlanId, PlanTaskQueryParam param) {
        // 执行分页查询
        Page<PlanTask> page = planTaskMapper.selectByUserPlanId(userPlanId, param);

        // 转换为VO列表
        List<PlanTaskListVO> voList;
        if (page.getRecords() != null) {
            voList = page.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        } else {
            voList = Collections.emptyList();
        }

        return new PageResult<>(voList, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 获取任务详情
     * 根据任务ID查询任务详情，如果任务不存在则抛出业务异常
     *
     * @param id 任务ID
     * @return 任务详情
     */
    @Override
    public PlanTaskDetailVO getTaskDetail(String id) {
        // 根据ID查询任务
        PlanTask task = planTaskMapper.selectById(id);

        if (task == null) {
            throw new BusinessException(ExceptionCode.PLAN_TASK_NOT_FOUND, "方案任务不存在");
        }

        // 转换为详情VO
        return convertToDetailVO(task);
    }

    /**
     * 完成任务
     * 标记任务为已完成状态，并奖励积分
     *
     * @param accountId 账号ID
     * @param param 完成任务参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(String accountId, TaskCompleteParam param) {
        // 查询任务
        PlanTask task = planTaskMapper.selectById(param.getTaskId());
        if (task == null) {
            throw new BusinessException(ExceptionCode.PLAN_TASK_NOT_FOUND, "方案任务不存在");
        }

        // 查询用户方案并验证归属
        UserPlan userPlan = userPlanMapper.selectById(task.getUserPlanId());
        if (userPlan == null || !userPlan.getAccountId().equals(accountId)) {
            throw new BusinessException(ExceptionCode.PLAN_NOT_FOUND, "方案不存在");
        }

        // 校验任务状态
        if (task.getStatus() != null && task.getStatus() == 2) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "任务已完成");
        }

        // 更新任务状态
        task.setStatus(2);
        task.setCompletedCount(task.getCompletedCount() != null ? task.getCompletedCount() + 1 : 1);
        task.setUpdateTime(LocalDateTime.now());
        planTaskMapper.update(task);

        // 同步更新方案历史的完成率
        updatePlanHistoryCompletion(userPlan.getId());

        // 奖励积分
        int taskPoints = pointsRuleService.getPointsByCode("PLAN_TASK");
        pointsService.addPoints(accountId, taskPoints, 2, "PLAN_TASK", task.getId(), "完成养生方案任务");
    }

    /**
     * 跳过任务
     * 标记任务为已跳过状态
     *
     * @param accountId 账号ID
     * @param id 任务ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void skipTask(String accountId, String id) {
        // 查询任务
        PlanTask task = planTaskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(ExceptionCode.PLAN_TASK_NOT_FOUND, "方案任务不存在");
        }

        // 查询用户方案并验证归属
        UserPlan userPlan = userPlanMapper.selectById(task.getUserPlanId());
        if (userPlan == null || !userPlan.getAccountId().equals(accountId)) {
            throw new BusinessException(ExceptionCode.PLAN_NOT_FOUND, "方案不存在");
        }

        // 校验任务状态（已完成或已跳过的不可重复处理）
        if (task.getStatus() != null && (task.getStatus() == 2 || task.getStatus() == 3)) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "任务已处理");
        }

        // 更新任务状态
        task.setStatus(3);
        task.setUpdateTime(LocalDateTime.now());
        planTaskMapper.update(task);
    }

    /**
     * 查询指定方案某天的任务列表（用于任务汇总统计）
     *
     * @param userPlanId 用户方案ID
     * @param date       查询日期
     * @return 该日期下的任务列表
     */
    @Override
    public List<PlanTask> getTasksByDate(String userPlanId, LocalDate date) {
        return planTaskMapper.selectByUserPlanIdAndDate(userPlanId, date);
    }

    /**
     * 转换为列表VO
     *
     * @param task 任务实体
     * @return 列表VO
     */
    private PlanTaskListVO convertToListVO(PlanTask task) {
        PlanTaskListVO vo = new PlanTaskListVO();
        vo.setId(task.getId());
        vo.setPlanType(task.getPlanType());
        vo.setTaskName(task.getTaskName());
        // 简要显示任务描述，取前200个字符
        vo.setTaskDescription(truncateText(task.getTaskDescription(), 200));
        vo.setTaskDate(task.getTaskDate() != null ? task.getTaskDate().toString() : null);
        vo.setStatus(task.getStatus());
        vo.setPriority(task.getPriority());
        vo.setTaskCategory(task.getTaskCategory());
        vo.setTargetCount(task.getTargetCount());
        vo.setCompletedCount(task.getCompletedCount());
        vo.setResourceType(task.getResourceType());
        vo.setResourceId(task.getResourceId());
        return vo;
    }

    /**
     * 转换为详情VO
     *
     * @param task 任务实体
     * @return 详情VO
     */
    private PlanTaskDetailVO convertToDetailVO(PlanTask task) {
        PlanTaskDetailVO vo = new PlanTaskDetailVO();
        vo.setId(task.getId());
        vo.setPlanType(task.getPlanType());
        vo.setTaskName(task.getTaskName());
        vo.setTaskDescription(task.getTaskDescription());
        vo.setTaskDate(task.getTaskDate() != null ? task.getTaskDate().toString() : null);
        vo.setStatus(task.getStatus());
        vo.setPriority(task.getPriority());
        vo.setTaskCategory(task.getTaskCategory());
        vo.setTargetCount(task.getTargetCount());
        vo.setCompletedCount(task.getCompletedCount());
        vo.setResourceType(task.getResourceType());
        vo.setResourceId(task.getResourceId());
        vo.setCreateTime(formatDateTime(task.getCreateTime()));
        vo.setUpdateTime(formatDateTime(task.getUpdateTime()));
        return vo;
    }

    /**
     * 截断文本
     *
     * @param text 原始文本
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
     * @return 格式化后的字符串（yyyy-MM-dd HH:mm:ss）
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * 重新计算并更新方案的完成率和已完成任务数
     *
     * @param userPlanId 用户方案ID
     */
    private void updatePlanHistoryCompletion(String userPlanId) {
        try {
            // 统计该方案的总任务数和已完成数
            long total = planTaskMapper.countByUserPlanId(userPlanId);
            long completed = planTaskMapper.countCompletedByUserPlanId(userPlanId);

            int totalTasks = (int) total;
            int completedTasks = (int) completed;
            int completionRate = totalTasks > 0 ? (int) (completedTasks * 100 / totalTasks) : 0;

            // 更新用户方案的完成率和任务统计
            UserPlan userPlan = userPlanMapper.selectById(userPlanId);
            if (userPlan != null) {
                userPlan.setTotalTasks(totalTasks);
                userPlan.setCompletedTasks(completedTasks);
                userPlan.setCompletionRate(java.math.BigDecimal.valueOf(completionRate));
                userPlan.setUpdateTime(LocalDateTime.now());
                userPlanMapper.update(userPlan);
            }
        } catch (Exception e) {
            log.error("更新方案完成率失败, userPlanId: {}", userPlanId, e);
        }
    }
}
