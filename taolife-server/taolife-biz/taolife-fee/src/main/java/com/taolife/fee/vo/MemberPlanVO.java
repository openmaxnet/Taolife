package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员套餐VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class MemberPlanVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 套餐ID */
    private String id;

    /** 套餐代码 */
    private String planCode;

    /** 套餐名称 */
    private String planName;

    /** 会员等级 */
    private Integer memberLevel;

    /** 会员时长（天） */
    private Integer durationDays;

    /** 原价（分） */
    private Integer originalPrice;

    /** 现价（分） */
    private Integer currentPrice;

    /** 折扣标签 */
    private String discountLabel;

    /** AI每日配额 */
    private Integer aiDailyQuota;

    /** 最大活跃方案数 */
    private Integer maxActivePlans;

    /** 最大方案周期（天） */
    private Integer maxCycleDays;

    /** 最大调整次数 */
    private Integer maxAdjustments;

    /** 每月体质评估配额 */
    private Integer assessmentMonthlyQuota;

    /** 积分获取倍率 */
    private Integer pointsMultiplier;

    /** 商城折扣 */
    private Double storeDiscount;

    /** 积分抵现比例 */
    private Integer pointsToYuanRatio;

    /** 权益详情JSON */
    private String benefitsJson;

    /** 开通/续费奖励成长值 */
    private Integer subGrowthBonus;

    /** 排序 */
    private Integer sortOrder;

    /** 是否启用 */
    private Integer isEnabled;

    /** 创建时间 */
    private LocalDateTime createTime;
}
