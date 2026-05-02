package com.taolife.plan.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户方案详情VO（管理后台，聚合5个子方案）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class UserPlanDetailAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 方案ID */
    private String id;
    /** 用户账号ID */
    private String accountId;
    /** 体质编码 */
    private String constitutionCode;
    /** 体质名称 */
    private String constitutionName;
    /** 季节（1-春 2-夏 3-秋 4-冬） */
    private Integer season;
    /** 季节名称 */
    private String seasonName;
    /** 开始日期 */
    private String startDate;
    /** 结束日期 */
    private String endDate;
    /** 周期天数 */
    private Integer cycleDays;
    /** 状态：1-进行中 2-已完成 3-已过期 4-已终止 */
    private Integer status;
    /** 完成率 */
    private BigDecimal completionRate;
    /** 总任务数 */
    private Integer totalTasks;
    /** 已完成任务数 */
    private Integer completedTasks;
    /** 用户评分 */
    private Integer userRating;
    /** 是否有效：0-无效 1-有效 */
    private Integer isEffective;
    /** 调整次数 */
    private Integer adjustmentCount;
    /** AI调整次数 */
    private Integer aiAdjustmentCount;
    /** 方案标题 */
    private String planTitle;
    /** 方案标签（JSON数组） */
    private String planTags;
    /** 用户备注 */
    private String userNotes;
    /** 用户反馈内容 */
    private String userFeedback;

    /** 饮食方案内容（Markdown） */
    private String foodPlanContent;
    /** 运动方案内容 */
    private String exercisePlanContent;
    /** 穴位方案内容 */
    private String acupointPlanContent;
    /** 经络方案内容 */
    private String meridianPlanContent;
    /** 生活方案内容 */
    private String lifestylePlanContent;

    /** 饮食方案标签 */
    private String foodPlanTags;
    /** 运动方案标签 */
    private String exercisePlanTags;
    /** 穴位方案标签 */
    private String acupointPlanTags;
    /** 经络方案标签 */
    private String meridianPlanTags;
    /** 生活方案标签 */
    private String lifestylePlanTags;

    /** 创建时间 */
    private String createTime;
    /** 更新时间 */
    private String updateTime;
}
