package com.taolife.wisdom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 运动分类枚举
 * 1-传统功法，2-有氧运动，3-力量训练，4-柔韧训练，5-休闲运动
 *
 * @author 文二
 * @date 2026-03-24
 */
@Getter
@AllArgsConstructor
public enum ExerciseCategoryEnum {

    /**
     * 传统功法
     */
    TRADITIONAL(1, "传统功法"),

    /**
     * 有氧运动
     */
    AEROBIC(2, "有氧运动"),

    /**
     * 力量训练
     */
    STRENGTH(3, "力量训练"),

    /**
     * 柔韧训练
     */
    FLEXIBILITY(4, "柔韧训练"),

    /**
     * 休闲运动
     */
    LEISURE(5, "休闲运动");

    /**
     * 分类值
     */
    private final Integer value;

    /**
     * 分类名称
     */
    private final String name;

    /**
     * 根据值获取枚举
     *
     * @param value 分类值
     * @return 枚举对象
     */
    public static ExerciseCategoryEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (ExerciseCategoryEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
