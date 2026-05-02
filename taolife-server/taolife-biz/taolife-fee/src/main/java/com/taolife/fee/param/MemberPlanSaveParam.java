package com.taolife.fee.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 会员套餐保存参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class MemberPlanSaveParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 套餐ID，为空表示新增 */
    private String id;

    /** 套餐代码 */
    @NotBlank(message = "套餐代码不能为空")
    private String planCode;

    /** 套餐名称 */
    @NotBlank(message = "套餐名称不能为空")
    private String planName;

    /** 会员等级：1-月卡，2-年卡，3-终身 */
    @NotNull(message = "会员等级不能为空")
    private Integer memberLevel;

    /** 会员时长（天） */
    @NotNull(message = "会员时长不能为空")
    private Integer durationDays;

    /** 原价（分） */
    @NotNull(message = "原价不能为空")
    private Integer originalPrice;

    /** 现价（分） */
    @NotNull(message = "现价不能为空")
    private Integer currentPrice;

    /** 折扣标签 */
    private String discountLabel;

    /** 每日AI配额 */
    private Integer aiDailyQuota;

    /** 最大同时进行方案数 */
    private Integer maxActivePlans;

    /** 最大方案周期（天） */
    private Integer maxCycleDays;

    /** 方案AI调整次数 */
    private Integer maxAdjustments;

    /** 每月体质评估次数 */
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

    /** 排序权重 */
    private Integer sortOrder;

    /** 是否启用：0-否，1-是 */
    private Integer isEnabled;
}
