package com.taolife.identity.param;

import lombok.Data;

import java.util.List;

/**
 * 健康目标参数
 */
@Data
public class HealthGoalsParam {

    /** 主要目标：1免疫 2睡眠 3体重 4压力 5消化 6体力 */
    private Integer healthGoalPrimary;

    /** 健康目标标签 如["immunity","sleep"] */
    private List<String> healthGoals;
}
