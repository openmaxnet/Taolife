package com.taolife.plan.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 方案摘要VO
 * 用于getHealthPlan接口，仅包含列表展示所需字段，不含方案内容
 *
 * @author 文二
 * @date 2026-04-08
 */
@Data
public class PlanSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方案ID
     */
    private String id;

    /**
     * 体质编码
     */
    private String constitutionCode;

    /**
     * 体质名称
     */
    private String constitutionName;

    /**
     * 方案状态：1-进行中 2-已完成 3-已过期 4-已终止
     */
    private Integer status;

    /**
     * AI生成的方案标题
     */
    private String planTitle;

    /**
     * 方案标签（JSON数组）
     */
    private String planTags;

    /**
     * 今日焦点/重点（当天那句）
     */
    private String todayFocus;

    /**
     * 饮食方案标签
     */
    private String foodPlanTags;

    /**
     * 运动方案标签
     */
    private String exercisePlanTags;

    /**
     * 穴位方案标签
     */
    private String acupointPlanTags;

    /**
     * 经络方案标签
     */
    private String meridianPlanTags;

    /**
     * 生活方案标签
     */
    private String lifestylePlanTags;

    /**
     * 今日任务摘要
     */
    private TaskSummary taskSummary;

    @Data
    public static class TaskSummary implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer todayTaskCount;
        private Integer completedCount;
    }
}
