package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员状态VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class MemberStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否会员 */
    private Boolean isActiveMember;

    /** 会员等级 */
    private Integer memberLevel;

    /** 会员等级名称 */
    private String memberLevelName;

    /** 到期时间 */
    private LocalDateTime expireTime;

    /** 剩余天数 */
    private Long remainDays;

    /** AI今日已用次数 */
    private Integer aiQuotaUsed;

    /** AI今日配额 */
    private Integer aiQuotaTotal;

    /** AI剩余次数 */
    private Integer aiQuotaRemaining;

    /** 最大活跃方案数 */
    private Integer maxActivePlans;

    /** 当前活跃方案数 */
    private Integer currentActivePlans;

    /** 积分倍率 */
    private Integer pointsMultiplier;

    /** 商城折扣 */
    private Double storeDiscount;

    /** 积分抵现比例 */
    private Integer pointsToYuanRatio;

    /** 成长值 */
    private Integer growthValue;

    /** 成长等级 */
    private Integer growthLevel;

    /** 成长等级名称 */
    private String growthLevelName;

    /** 成长等级额外AI配额 */
    private Integer bonusAiQuota;

    /** 成长等级额外积分倍率 */
    private Double bonusPointsMultiplier;

    /** 成长等级额外商城折扣 */
    private Double bonusStoreDiscount;
}
