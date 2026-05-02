package com.taolife.plan.vo;

import lombok.Data;

/**
 * 周期报告VO
 *
 * @author 文二
 * @date 2026-04-08
 */
@Data
public class PlanCycleReportVO {

    /**
     * 报告ID
     */
    private String id;

    /**
     * 被总结的方案ID
     */
    private String planId;

    /**
     * 触发总结的新方案ID
     */
    private String newPlanId;

    /**
     * 报告内容（Markdown）
     */
    private String reportContent;
}
