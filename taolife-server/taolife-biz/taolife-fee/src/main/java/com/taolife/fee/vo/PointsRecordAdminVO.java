package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分记录 VO（管理员用）
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class PointsRecordAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private String id;

    /** 账号ID */
    private String accountId;

    /** 积分变化（正数增加，负数减少） */
    private Integer pointsChange;

    /** 积分类型：1-签到，2-健康计划，3-分享，4-消费，5-过期 */
    private Integer pointsType;

    /** 业务类型 */
    private String businessType;

    /** 业务ID */
    private String businessId;

    /** 备注 */
    private String remark;

    /** 变化后余额 */
    private Integer balanceAfter;

    /** 创建时间 */
    private LocalDateTime createTime;
}
