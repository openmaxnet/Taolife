package com.taolife.fee.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 成长等级保存参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class GrowthLevelSaveParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 等级ID（修改时必填） */
    private String id;

    /** 等级序号 */
    @NotNull(message = "等级序号不能为空")
    private Integer level;

    /** 等级名称 */
    @NotBlank(message = "等级名称不能为空")
    private String levelName;

    /** 最低成长值 */
    @NotNull(message = "最低成长值不能为空")
    private Integer minGrowthValue;

    /** 奖励AI对话次数 */
    @NotNull(message = "AI配额不能为空")
    private Integer bonusAiQuota;

    /** 积分加成倍率 */
    @NotNull(message = "积分倍率不能为空")
    private Double bonusPointsMultiplier;

    /** 商城折扣（0-1，如0.9表示9折） */
    @NotNull(message = "商城折扣不能为空")
    private Double bonusStoreDiscount;

    /** 特权描述 */
    private String privilege;

    /** 是否启用：0-否，1-是 */
    @NotNull(message = "是否启用不能为空")
    private Integer isEnabled;

    /** 排序号 */
    @NotNull(message = "排序不能为空")
    private Integer sortOrder;
}
