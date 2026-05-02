package com.taolife.fee.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 积分商品保存参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class PointsGoodsSaveParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商品ID，为空表示新增 */
    private String id;

    /** 商品编码 */
    @NotBlank(message = "商品编码不能为空")
    private String goodsCode;

    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    private String goodsName;

    /** 商品类型：1-AI问答次数，2-体质评估次数，3-文章解锁，4-商城优惠券 */
    @NotNull(message = "商品类型不能为空")
    private Integer goodsType;

    /** 兑换值 */
    @NotNull(message = "兑换值不能为空")
    private Integer value;

    /** 所需积分 */
    @NotNull(message = "所需积分不能为空")
    private Integer pointsRequired;

    /** 每日限制次数 */
    private Integer dailyLimit;

    /** 总限制次数 */
    private Integer totalLimit;

    /** 图标URL */
    private String iconUrl;

    /** 商品描述 */
    private String description;

    /** 排序权重 */
    private Integer sortOrder;

    /** 是否启用：0-否，1-是 */
    private Integer isEnabled;
}
