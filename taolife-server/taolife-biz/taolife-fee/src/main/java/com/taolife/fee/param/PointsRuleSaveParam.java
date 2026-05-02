package com.taolife.fee.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 积分规则保存参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class PointsRuleSaveParam {

    /** 规则编码 */
    @NotBlank(message = "规则编码不能为空")
    private String ruleCode;

    /** 规则名称 */
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    /** 积分类型：1-签到，2-健康计划，3-消费 */
    @NotNull(message = "积分类型不能为空")
    private Integer pointsType;

    /** 奖励积分数 */
    @NotNull(message = "积分数不能为空")
    private Integer points;

    /** 条件类型：1-无条件，2-最低消费金额（分），3-连续天数 */
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
}
