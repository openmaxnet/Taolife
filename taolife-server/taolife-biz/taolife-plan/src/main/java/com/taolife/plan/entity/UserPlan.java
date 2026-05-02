package com.taolife.plan.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户养生方案记录实体（重构优化）
 * 对应数据库表 tl_plan_user_plan
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
@Table("tl_plan_user_plan")
public class UserPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID（主键）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 体质编码
     */
    private String constitutionCode;

    /**
     * 体质名称
     */
    private String constitutionName;

    /**
     * 季节：1-春，2-夏，3-秋，4-冬
     */
    private Integer season;

    /**
     * 季节名称
     */
    private String seasonName;

    /**
     * 食材方案ID
     */
    private String foodPlanId;

    /**
     * 运动方案ID
     */
    private String exercisePlanId;

    /**
     * 穴位方案ID
     */
    private String acupointPlanId;

    /**
     * 经络方案ID
     */
    private String meridianPlanId;

    /**
     * 生活方案ID
     */
    private String lifestylePlanId;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 方案周期（天）
     */
    private Integer cycleDays;

    /**
     * 状态：1-进行中，2-已完成，3-已暂停
     */
    private Integer status;

    /**
     * 方案快照日期
     */
    private LocalDate planDate;

    /**
     * 总任务数
     */
    private Integer totalTasks;

    /**
     * 已完成任务数
     */
    private Integer completedTasks;

    /**
     * 用户反馈
     */
    private String userFeedback;

    /**
     * 用户评分：1-5星
     */
    private Integer userRating;

    /**
     * 是否有效：0-未评价 1-有效 2-无效
     */
    private Integer isEffective;

    /**
     * 调整原因
     */
    private String adjustmentReason;

    /**
     * 调整次数
     */
    private Integer adjustmentCount;

    /**
     * 完成率
     */
    private BigDecimal completionRate;

    /**
     * AI调整次数
     */
    private Integer aiAdjustmentCount;

    /**
     * 最后调整时间
     */
    private LocalDateTime lastAdjustmentTime;

    /**
     * 用户备注
     */
    private String userNotes;

    /**
     * AI生成的方案标题
     */
    private String planTitle;

    /**
     * 方案标签（JSON数组）
     */
    private String planTags;

    /**
     * 每日焦点（JSON数组）
     */
    private String dailyFocuses;

    /**
     * 分享次数
     */
    private Integer shareCount;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
