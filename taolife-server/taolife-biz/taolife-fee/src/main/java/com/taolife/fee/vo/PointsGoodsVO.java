package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 积分商品VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class PointsGoodsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private String id;
    /** 商品编码 */
    private String goodsCode;
    /** 商品名称 */
    private String goodsName;
    /** 商品类型：1-AI问答次数，2-体质评估次数，3-文章解锁，4-商城优惠券 */
    private Integer goodsType;
    /** 兑换值 */
    private Integer value;
    /** 所需积分 */
    private Integer pointsRequired;
    /** 每日限制次数 */
    private Integer dailyLimit;
    /** 图标URL */
    private String iconUrl;
    /** 商品描述 */
    private String description;
}
