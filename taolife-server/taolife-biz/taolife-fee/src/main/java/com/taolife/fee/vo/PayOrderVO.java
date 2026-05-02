package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付订单VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class PayOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private String id;

    /** 订单编号 */
    private String orderNo;

    /** 账号ID */
    private String accountId;

    /** 套餐ID */
    private String memberPlanId;

    /** 套餐代码 */
    private String planCode;

    /** 套餐名称 */
    private String planName;

    /** 会员等级 */
    private Integer memberLevel;

    /** 会员时长（天） */
    private Integer durationDays;

    /** 订单金额（分） */
    private Integer orderAmount;

    /** 支付金额（分） */
    private Integer payAmount;

    /** 订单状态 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 微信预支付ID */
    private String wxPrepayId;

    /** 微信交易单号 */
    private String wxTransactionId;

    /** 支付时间 */
    private LocalDateTime paidTime;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
