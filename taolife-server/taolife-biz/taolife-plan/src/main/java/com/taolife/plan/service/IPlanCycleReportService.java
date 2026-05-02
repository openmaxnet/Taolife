package com.taolife.plan.service;

import com.taolife.plan.entity.PlanCycleReport;

/**
 * 方案周期报告服务接口
 *
 * @author 文二
 * @date 2026-04-08
 */
public interface IPlanCycleReportService {

    /**
     * 获取用户未读的周期报告
     *
     * @param accountId 账号ID
     * @return 未读报告，无则返回null
     */
    PlanCycleReport getUnreadReport(String accountId);

    /**
     * 根据方案ID获取周期报告（用于历史方案查看自身执行总结）
     *
     * @param planId 方案ID
     * @param accountId 账号ID
     * @return 周期报告，无则返回null
     */
    PlanCycleReport getReportByPlanId(String planId, String accountId);

    /**
     * 标记报告为已读
     *
     * @param reportId 报告ID
     * @param accountId 账号ID
     */
    void markRead(String reportId, String accountId);

    /**
     * 异步生成周期报告（非首次生成方案时调用）
     *
     * @param accountId 账号ID
     * @param oldPlanId 被总结的旧方案ID
     * @param newPlanId 触发总结的新方案ID
     */
    void generateReportAsync(String accountId, String oldPlanId, String newPlanId);
}
