package com.taolife.plan.service;

import com.taolife.common.utils.PageResult;
import com.taolife.plan.param.PlanTaskQueryParam;

import java.time.LocalDate;
import java.util.List;
import com.taolife.plan.param.TaskCompleteParam;
import com.taolife.plan.vo.PlanTaskDetailVO;
import com.taolife.plan.vo.PlanTaskListVO;

/**
 * 方案任务服务接口
 * 定义方案任务相关的业务操作
 *
 * @author 文二
 * @date 2026-04-06
 */
public interface IPlanTaskService {

    /**
     * 分页查询任务列表
     * 根据用户方案ID分页查询任务信息列表
     *
     * @param userPlanId 用户方案ID
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<PlanTaskListVO> getTaskPage(String userPlanId, PlanTaskQueryParam param);

    /**
     * 获取任务详情
     * 根据任务ID获取任务的详细信息
     *
     * @param id 任务ID
     * @return 任务详细信息
     */
    PlanTaskDetailVO getTaskDetail(String id);

    /**
     * 完成任务
     * 标记任务为已完成状态，并奖励积分
     *
     * @param accountId 账号ID
     * @param param 完成任务参数
     */
    void completeTask(String accountId, TaskCompleteParam param);

    /**
     * 跳过任务
     * 标记任务为已跳过状态
     *
     * @param accountId 账号ID
     * @param id 任务ID
     */
    void skipTask(String accountId, String id);

    /**
     * 查询指定方案某天的任务列表（用于任务汇总统计）
     *
     * @param userPlanId 用户方案ID
     * @param date       查询日期
     * @return 该日期下的任务列表
     */
    List<com.taolife.plan.entity.PlanTask> getTasksByDate(String userPlanId, LocalDate date);
}
