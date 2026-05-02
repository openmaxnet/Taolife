package com.taolife.wisdom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 食材性质枚举
 * 1-寒，2-凉，3-平，4-温，5-热
 *
 * @author 文二
 * @date 2026-03-20
 */
@Getter
@AllArgsConstructor
public enum FoodNatureEnum {

    /**
     * 寒
     */
    COLD(1, "寒"),

    /**
     * 凉
     */
    COOL(2, "凉"),

    /**
     * 平
     */
    NEUTRAL(3, "平"),

    /**
     * 温
     */
    WARM(4, "温"),

    /**
     * 热
     */
    HOT(5, "热");

    /**
     * 性质值
     */
    private final Integer value;

    /**
     * 性质名称
     */
    private final String name;

    /**
     * 根据值获取枚举
     *
     * @param value 性质值
     * @return 枚举对象
     */
    public static FoodNatureEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (FoodNatureEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
