package com.taolife.plan.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 健康方案详情VO
 * 用于返回健康方案的详细信息
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class PlanDetailVO implements Serializable {

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
     * 季节：1=春 2=夏 3=秋 4=冬
     */
    private Integer season;

    /**
     * 季节名称
     */
    private String seasonName;

    /**
     * 开始日期（格式：yyyy-MM-dd）
     */
    private String startDate;

    /**
     * 结束日期（格式：yyyy-MM-dd）
     */
    private String endDate;

    /**
     * 状态：1=进行中 2=已完成 3=已暂停
     */
    private Integer status;

    /**
     * 周期天数
     */
    private Integer cycleDays;

    /**
     * 完成率
     */
    private BigDecimal completionRate;

    /**
     * 饮食方案内容（JSON）
     */
    private String foodPlanContent;

    /**
     * 运动方案内容（JSON）
     */
    private String exercisePlanContent;

    /**
     * 穴位方案内容（JSON）
     */
    private String acupointPlanContent;

    /**
     * 经络方案内容（JSON）
     */
    private String meridianPlanContent;

    /**
     * 生活方案内容（JSON）
     */
    private String lifestylePlanContent;

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
     * 用户备注
     */
    private String userNotes;

    /**
     * AI调整次数
     */
    private Integer aiAdjustmentCount;

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
     * 今日任务摘要
     */
    private TaskSummary taskSummary;

    /**
     * 今日任务摘要
     */
    @Data
    public static class TaskSummary implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 今日任务总数
         */
        private Integer todayTaskCount;

        /**
         * 已完成任务数
         */
        private Integer completedCount;
    }
}
