package com.taolife.plan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 方案类型枚举
 * 1-饮食方案，2-运动方案，3-穴位方案，4-经络方案，5-生活方案
 *
 * @author 文二
 * @date 2026-04-16
 */
@Getter
@AllArgsConstructor
public enum PlanTypeEnum {

    /**
     * 饮食方案
     */
    DIET(1, "food", "饮食方案"),

    /**
     * 运动方案
     */
    EXERCISE(2, "exercise", "运动方案"),

    /**
     * 穴位方案
     */
    ACUPOINT(3, "acupoint", "穴位按摩"),

    /**
     * 经络方案
     */
    MERIDIAN(4, "meridian", "经络调理"),

    /**
     * 生活方案
     */
    LIFESTYLE(5, "lifestyle", "生活起居");

    /**
     * 类型值
     */
    private final Integer value;

    /**
     * 方案key（用于批量调整JSON映射）
     */
    private final String key;

    /**
     * 方案显示名称（用于AI提示词和前端展示）
     */
    private final String displayName;

    /**
     * 根据值获取枚举
     *
     * @param value 类型值
     * @return 方案类型枚举，未匹配返回null
     */
    public static PlanTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (PlanTypeEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据key获取枚举
     *
     * @param key 方案key
     * @return 方案类型枚举，未匹配返回null
     */
    public static PlanTypeEnum getByKey(String key) {
        if (key == null) {
            return null;
        }
        for (PlanTypeEnum e : values()) {
            if (key.equals(e.getKey())) {
                return e;
            }
        }
        return null;
    }
}
