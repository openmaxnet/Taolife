package com.taolife.aichat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 敏感词类型枚举
 * 1-医疗诊断，2-政治敏感，3-不当内容
 *
 * @author 文二
 * @date 2026-03-26
 */
@Getter
@AllArgsConstructor
public enum SensitiveWordTypeEnum {

    /**
     * 医疗诊断
     */
    MEDICAL_DIAGNOSIS(1, "医疗诊断"),

    /**
     * 政治敏感
     */
    POLITICAL(2, "政治敏感"),

    /**
     * 不当内容
     */
    INAPPROPRIATE(3, "不当内容");

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
    public static SensitiveWordTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (SensitiveWordTypeEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}