package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员成长状态 VO（用户端用）
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class MemberGrowthStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前成长值 */
    private Integer growthValue;

    /** 当前等级值 */
    private Integer growthLevel;

    /** 当前等级名称 */
    private String growthLevelName;

    /** 当前等级最低成长值 */
    private Integer currentLevelMinGrowth;

    /** 下一级最低成长值 */
    private Integer nextLevelMinGrowth;

    /** 升级进度百分比 */
    private Integer progressPercent;

    /** 奖励AI对话次数 */
    private Integer bonusAiQuota;

    /** 积分加成倍率 */
    private Double bonusPointsMultiplier;

    /** 商城折扣 */
    private Double bonusStoreDiscount;

    /** 等级特权描述 */
    private String privilege;
}
