package com.taolife.wisdom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 食材分类枚举
 * 1-谷物，2-蔬菜，3-水果，4-肉类，5-药材
 *
 * @author 文二
 * @date 2026-03-20
 */
@Getter
@AllArgsConstructor
public enum FoodCategoryEnum {

    /**
     * 谷物
     */
    GRAIN(1, "谷物"),

    /**
     * 蔬菜
     */
    VEGETABLE(2, "蔬菜"),

    /**
     * 水果
     */
    FRUIT(3, "水果"),

    /**
     * 肉类
     */
    MEAT(4, "肉类"),

    /**
     * 药材
     */
    HERBS(5, "药材");

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
    public static FoodCategoryEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (FoodCategoryEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
