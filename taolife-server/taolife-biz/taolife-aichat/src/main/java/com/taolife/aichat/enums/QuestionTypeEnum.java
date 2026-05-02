package com.taolife.aichat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 问题类型枚举
 * 1-体质类，2-食疗类，3-穴位类，4-养生类，5-其他
 *
 * @author 文二
 * @date 2026-03-26
 */
@Getter
@AllArgsConstructor
public enum QuestionTypeEnum {

    /**
     * 体质类
     */
    CONSTITUTION(1, "体质类"),

    /**
     * 食疗类
     */
    FOOD_THERAPY(2, "食疗类"),

    /**
     * 穴位类
     */
    ACUPOINT(3, "穴位类"),

    /**
     * 养生类
     */
    HEALTH(4, "养生类"),

    /**
     * 其他
     */
    OTHER(5, "其他");

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
     * @return 枚举对象
     */
    public static QuestionTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (QuestionTypeEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}