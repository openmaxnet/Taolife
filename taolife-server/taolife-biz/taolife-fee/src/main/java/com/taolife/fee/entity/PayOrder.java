package com.taolife.fee.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付订单实体
 * 对应数据库表 tf_pay_order
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
@Table("tl_fee_pay_order")
public class PayOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 订单编号 */
    private String orderNo;

    /** 账号ID */
    private String accountId;

    /** 会员套餐ID */
    private String memberPlanId;

    /** 套餐代码 */
    private String planCode;

    /** 会员等级 */
    private Integer memberLevel;

    /** 会员时长（天） */
    private Integer durationDays;

    /** 订单金额（分） */
    private Integer orderAmount;

    /** 实际支付金额（分） */
    private Integer payAmount;

    /** 订单状态：0-待支付，1-已支付，2-已取消，3-已退款，4-已关闭 */
    private Integer status;

    /** 微信预支付ID */
    private String wxPrepayId;

    /** 微信交易单号 */
    private String wxTransactionId;

    /** 微信OpenID */
    private String wxOpenid;

    /** 支付时间 */
    private LocalDateTime paidTime;

    /** 订单过期时间 */
    private LocalDateTime expireTime;

    /** 备注 */
    private String remark;

    /** 是否删除：0-否，1-是 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
