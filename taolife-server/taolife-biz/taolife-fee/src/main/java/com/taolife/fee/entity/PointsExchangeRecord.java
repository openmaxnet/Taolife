package com.taolife.fee.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分兑换记录实体
 * 对应数据库表 tf_points_exchange_record
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
@Table("tl_fee_points_exchange_record")
public class PointsExchangeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 账号ID */
    private String accountId;

    /** 积分商品ID */
    private String goodsId;

    /** 消耗积分 */
    private Integer pointsCost;

    /** 兑换值 */
    private Integer exchangeValue;

    /** 关联业务ID */
    private String businessId;

    /** 状态：0-待处理，1-成功，2-已退还 */
    private Integer status;

    /** 是否删除：0-否，1-是 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
