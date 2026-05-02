package com.taolife.plan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

/**
 * 季节枚举
 * 1-春，2-夏，3-秋，4-冬
 *
 * @author 文二
 * @date 2026-03-24
 */
@Getter
@AllArgsConstructor
public enum SeasonEnum {

    /**
     * 春季
     */
    SPRING(1, "春季"),

    /**
     * 夏季
     */
    SUMMER(2, "夏季"),

    /**
     * 秋季
     */
    AUTUMN(3, "秋季"),

    /**
     * 冬季
     */
    WINTER(4, "冬季");

    /**
     * 季节值
     */
    private final Integer value;

    /**
     * 季节名称
     */
    private final String name;

    /**
     * 根据值获取枚举
     *
     * @param value 季节值
     * @return 枚举对象
     */
    public static SeasonEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (SeasonEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 获取当前季节
     *
     * @return 当前季节
     */
    public static SeasonEnum getCurrentSeason() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 3 && month <= 5) {
            return SPRING;
        } else if (month >= 6 && month <= 8) {
            return SUMMER;
        } else if (month >= 9 && month <= 11) {
            return AUTUMN;
        } else {
            return WINTER;
        }
    }
}
