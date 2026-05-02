package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分规则 VO（管理员用）
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class PointsRuleAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private String id;

    /** 规则编码 */
    private String ruleCode;

    /** 规则名称 */
    private String ruleName;

    /** 积分类型：1-签到，2-健康计划，3-消费 */
    private Integer pointsType;

    /** 奖励积分数 */
    private Integer points;

    /** 条件类型：1-无条件，2-最低消费金额，3-连续天数 */
    private Integer conditionType;

    /** 条件值 */
    private Integer conditionValue;

    /** 每日获取上限（0=不限） */
    private Integer dailyLimit;

    /** 是否启用：0-否，1-是 */
    private Integer isEnabled;

    /** 排序号 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
