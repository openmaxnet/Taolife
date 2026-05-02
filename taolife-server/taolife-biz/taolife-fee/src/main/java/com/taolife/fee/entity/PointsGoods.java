package com.taolife.fee.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分商品实体
 * 对应数据库表 tf_points_goods
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
@Table("tl_fee_points_goods")
public class PointsGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 商品编码 */
    private String goodsCode;

    /** 商品名称 */
    private String goodsName;

    /** 商品类型：1-AI问答次数，2-体质评估次数，3-文章解锁，4-商城优惠券 */
    private Integer goodsType;

    /** 兑换值（如AI次数5） */
    private Integer value;

    /** 所需积分 */
    private Integer pointsRequired;

    /** 每日限制次数，0不限 */
    private Integer dailyLimit;

    /** 总限制次数，0不限 */
    private Integer totalLimit;

    /** 图标URL */
    private String iconUrl;

    /** 描述 */
    private String description;

    /** 排序权重 */
    private Integer sortOrder;

    /** 是否启用：0-否，1-是 */
    private Integer isEnabled;

    /** 是否删除：0-否，1-是 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
