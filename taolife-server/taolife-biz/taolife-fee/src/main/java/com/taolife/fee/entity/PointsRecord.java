package com.taolife.fee.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户积分记录实体
 * 对应数据库表 tf_points_record
 *
 * @author 文二
 * @date 2026-04-02
 */
@Data
@Table("tl_fee_points_record")
public class PointsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 积分变化（正数增加，负数减少）
     */
    private Integer pointsChange;

    /**
     * 积分类型：1-签到，2-健康计划，3-分享，4-消费，5-过期
     */
    private Integer pointsType;

    /**
     * 业务类型：daily_checkin/health_plan_task/share/redeem
     */
    private String businessType;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 变化后余额
     */
    private Integer balanceAfter;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
