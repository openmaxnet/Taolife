package com.taolife.wisdom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 经络分类枚举
 * 1-十二正经，2-奇经八脉
 *
 * @author 文二
 * @date 2026-03-24
 */
@Getter
@AllArgsConstructor
public enum MeridianCategoryEnum {

    /**
     * 十二正经
     */
    REGULAR_MERIDIAN(1, "十二正经"),

    /**
     * 奇经八脉
     */
    EXTRA_MERIDIAN(2, "奇经八脉");

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
    public static MeridianCategoryEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (MeridianCategoryEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}