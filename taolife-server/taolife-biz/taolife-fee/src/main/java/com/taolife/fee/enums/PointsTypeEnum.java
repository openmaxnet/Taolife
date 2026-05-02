package com.taolife.fee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 积分类型枚举
 * 1-签到，2-健康计划，3-消费
 *
 * @author 文二
 * @date 2026-04-02
 */
@Getter
@AllArgsConstructor
public enum PointsTypeEnum {

    /**
     * 签到
     */
    CHECKIN(1, "签到"),

    /**
     * 健康计划
     */
    HEALTH_PLAN(2, "健康计划"),

    /**
     * 消费
     */
    CONSUME(3, "消费"),

    /**
     * 广告奖励
     */
    AD_REWARD(4, "广告奖励");

    /**
     * 类型值
     */
    private final Integer value;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 根据值获取枚举
     *
     * @param value 类型值
     * @return 积分类型枚举
     */
    public static PointsTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (PointsTypeEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
