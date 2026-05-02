package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 成长等级 VO（用户端用）
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class GrowthLevelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 等级ID */
    private String id;

    /** 等级值 */
    private Integer level;

    /** 等级名称 */
    private String levelName;

    /** 最低成长值 */
    private Integer minGrowthValue;

    /** 奖励AI对话次数 */
    private Integer bonusAiQuota;

    /** 积分加成倍率 */
    private Double bonusPointsMultiplier;

    /** 商城折扣 */
    private Double bonusStoreDiscount;

    /** 特权描述 */
    private String privilege;

    /** 是否为当前等级 */
    private Boolean currentLevel;
}
