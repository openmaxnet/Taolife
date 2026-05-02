package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分兑换记录管理端VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class PointsExchangeAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private String id;
    /** 用户账号ID */
    private String accountId;
    /** 用户昵称 */
    private String accountNickname;
    /** 积分商品ID */
    private String goodsId;
    /** 商品编码 */
    private String goodsCode;
    /** 商品名称 */
    private String goodsName;
    /** 消耗积分 */
    private Integer pointsCost;
    /** 兑换值 */
    private Integer exchangeValue;
    /** 关联业务ID */
    private String businessId;
    /** 状态：0-待处理，1-成功，2-已退还 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
