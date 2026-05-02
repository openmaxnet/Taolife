package com.taolife.wisdom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章分类枚举
 * 1-养生方法，2-四季养生，3-节气养生，4-食疗方案，5-中医知识
 *
 * @author 文二
 * @date 2026-03-21
 */
@Getter
@AllArgsConstructor
public enum ArticleCategoryEnum {

    /**
     * 养生方法
     */
    HEALTH_METHOD(1, "养生方法"),

    /**
     * 四季养生
     */
    SEASONAL_HEALTH(2, "四季养生"),

    /**
     * 节气养生
     */
    SOLAR_TERM_HEALTH(3, "节气养生"),

    /**
     * 食疗方案
     */
    DIET_PLAN(4, "食疗方案"),

    /**
     * 中医知识
     */
    TCM_KNOWLEDGE(5, "中医知识");

    /**
     * 分类值
     */
    private final Integer value;

    /**
     * 分类名称
     */
    private final String name;

    /**
     * 根据value获取枚举
     *
     * @param value 分类值
     * @return 枚举对象
     */
    public static ArticleCategoryEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (ArticleCategoryEnum articleCategoryEnum : values()) {
            if (articleCategoryEnum.getValue().equals(value)) {
                return articleCategoryEnum;
            }
        }
        return null;
    }
}
