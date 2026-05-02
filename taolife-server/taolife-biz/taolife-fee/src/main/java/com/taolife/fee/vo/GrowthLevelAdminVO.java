package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 成长等级管理 VO（管理员用）
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class GrowthLevelAdminVO implements Serializable {

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

    /** 是否启用：0-否，1-是 */
    private Integer isEnabled;

    /** 排序号 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
