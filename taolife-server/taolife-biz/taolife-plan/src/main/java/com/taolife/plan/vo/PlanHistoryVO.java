package com.taolife.plan.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 方案历史VO
 * 用于返回健康方案的历史记录
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class PlanHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方案ID
     */
    private String id;

    /**
     * 方案日期（格式：yyyy-MM-dd）
     */
    private String planDate;

    /**
     * 体质名称
     */
    private String constitutionName;

    /**
     * 季节名称
     */
    private String seasonName;

    /**
     * 完成率
     */
    private BigDecimal completionRate;

    /**
     * 总任务数
     */
    private Integer totalTasks;

    /**
     * 已完成任务数
     */
    private Integer completedTasks;

    /**
     * 用户评分（1-5星）
     */
    private Integer userRating;

    /**
     * 是否有效：0=未评价 1=有效 2=无效
     */
    private Integer isEffective;

    /**
     * 调整次数
     */
    private Integer adjustmentCount;

    /**
     * 方案标题
     */
    private String planTitle;

    /**
     * 方案标签（JSON数组）
     */
    private String planTags;
}
