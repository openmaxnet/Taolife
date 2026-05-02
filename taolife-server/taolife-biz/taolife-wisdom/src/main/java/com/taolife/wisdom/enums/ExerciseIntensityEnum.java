package com.taolife.wisdom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 运动强度枚举
 * 1-温和，2-轻度，3-中度，4-重度
 *
 * @author 文二
 * @date 2026-03-24
 */
@Getter
@AllArgsConstructor
public enum ExerciseIntensityEnum {

    /**
     * 温和
     */
    GENTLE(1, "温和"),

    /**
     * 轻度
     */
    LIGHT(2, "轻度"),

    /**
     * 中度
     */
    MODERATE(3, "中度"),

    /**
     * 重度
     */
    VIGOROUS(4, "重度");

    /**
     * 强度值
     */
    private final Integer value;

    /**
     * 强度名称
     */
    private final String name;

    /**
     * 根据值获取枚举
     *
     * @param value 强度值
     * @return 枚举对象
     */
    public static ExerciseIntensityEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (ExerciseIntensityEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
