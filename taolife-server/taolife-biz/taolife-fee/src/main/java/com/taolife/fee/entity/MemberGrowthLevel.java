package com.taolife.fee.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员成长等级实体
 * 对应数据库表 tf_member_growth_level
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
@Table("tl_fee_growth_level")
public class MemberGrowthLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 等级值（从1开始递增） */
    private Integer level;

    /** 等级名称 */
    private String levelName;

    /** 最小成长值 */
    private Integer minGrowthValue;

    /** 奖励AI对话次数 */
    private Integer bonusAiQuota;

    /** 积分加成倍数 */
    private Double bonusPointsMultiplier;

    /** 商城折扣（0-1，如0.9表示9折） */
    private Double bonusStoreDiscount;

    /** 等级特权描述 */
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
