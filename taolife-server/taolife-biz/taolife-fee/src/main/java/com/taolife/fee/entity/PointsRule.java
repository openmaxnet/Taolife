package com.taolife.fee.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分规则实体
 * 对应数据库表 tf_points_rule
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
@Table("tl_fee_points_rule")
public class PointsRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 规则编码 */
    private String ruleCode;

    /** 规则名称 */
    private String ruleName;

    /** 积分类型：1-签到，2-健康计划，3-消费 */
    private Integer pointsType;

    /** 奖励积分数 */
    private Integer points;

    /** 条件类型：1-无条件，2-最低消费金额（分），3-连续天数 */
    private Integer conditionType;

    /** 条件值（根据conditionType含义不同） */
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
