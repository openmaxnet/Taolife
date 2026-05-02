package com.taolife.fee.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员套餐实体
 * 对应数据库表 tf_member_plan
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
@Table("tl_fee_member_plan")
public class MemberPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 套餐代码 */
    private String planCode;

    /** 套餐名称 */
    private String planName;

    /** 会员等级：1-月卡，2-年卡，3-终身 */
    private Integer memberLevel;

    /** 会员时长（天）：月卡30天，年卡365天，终身0表示永久 */
    private Integer durationDays;

    /** 原价（分） */
    private Integer originalPrice;

    /** 现价（分） */
    private Integer currentPrice;

    /** 折扣标签 */
    private String discountLabel;

    /** 每日AI配额：月卡50，年卡200，终身-1表示无限 */
    private Integer aiDailyQuota;

    /** 最大同时进行方案数 */
    private Integer maxActivePlans;

    /** 最大方案周期（天） */
    private Integer maxCycleDays;

    /** 方案AI调整次数限制：月卡5，年卡无限（-1） */
    private Integer maxAdjustments;

    /** 每月体质评估次数：月卡3，终身无限 */
    private Integer assessmentMonthlyQuota;

    /** 积分获取倍率 */
    private Integer pointsMultiplier;

    /** 商城折扣（0.95=95折） */
    private Double storeDiscount;

    /** 积分抵扣比例（50积分=1元） */
    private Integer pointsToYuanRatio;

    /** 权益详情JSON */
    private String benefitsJson;

    /** 开通/续费奖励成长值 */
    private Integer subGrowthBonus;

    /** 排序权重 */
    private Integer sortOrder;

    /** 是否启用 */
    private Integer isEnabled;

    /** 是否删除：0-否，1-是 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
