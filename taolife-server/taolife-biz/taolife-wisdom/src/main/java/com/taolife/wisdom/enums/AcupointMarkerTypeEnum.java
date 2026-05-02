package com.taolife.wisdom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 穴位标注类型枚举
 * 1-普通，2-重要，3-关键
 *
 * @author 文二
 * @date 2026-03-24
 */
@Getter
@AllArgsConstructor
public enum AcupointMarkerTypeEnum {

    /**
     * 普通
     */
    NORMAL(1, "普通"),

    /**
     * 重要
     */
    IMPORTANT(2, "重要"),

    /**
     * 关键
     */
    KEY(3, "关键");

    /**
     * 标注类型值
     */
    private final Integer value;

    /**
     * 标注类型名称
     */
    private final String name;

    /**
     * 根据值获取枚举
     *
     * @param value 标注类型值
     * @return 枚举对象
     */
    public static AcupointMarkerTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (AcupointMarkerTypeEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}