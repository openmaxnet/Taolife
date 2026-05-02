package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分兑换记录VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class PointsExchangeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private String id;
    /** 商品编码 */
    private String goodsCode;
    /** 商品名称 */
    private String goodsName;
    /** 商品类型：1-AI问答次数，2-体质评估次数 */
    private Integer goodsType;
    /** 消耗积分 */
    private Integer pointsCost;
    /** 兑换值 */
    private Integer exchangeValue;
    /** 状态：0-待处理，1-成功，2-已退还 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
}
