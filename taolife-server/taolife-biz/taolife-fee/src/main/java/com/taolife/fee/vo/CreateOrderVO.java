package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建订单VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class CreateOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单编号 */
    private String orderNo;

    /** 预支付会话ID */
    private String prepayId;

    /** 支付参数（调起支付的必需参数） */
    private String payParams;

    /** 时间戳 */
    private String timeStamp;
    /** 随机字符串 */
    private String nonceStr;
    /** 订单详情扩展字符串 */
    private String packageStr;
    /** 签名类型 */
    private String signType;
    /** 签名 */
    private String paySign;
}
