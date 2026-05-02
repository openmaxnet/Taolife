package com.taolife.identity.param;

import lombok.Data;

import java.util.List;

/**
 * 饮食偏好参数
 */
@Data
public class DietaryPreferenceParam {

    /** 偏好食物性质JSON */
    private String preferredFoodNature;

    /** 口味偏好：1清淡 2浓郁 3辛辣 4偏甜 */
    private Integer preferredFoodTexture;

    /** 不喜欢的食材ID列表 */
    private List<String> dislikedFoods;

    /** 过敏食材ID列表 */
    private List<String> allergicFoods;

    /** 饮食限制标签 如["vegetarian","low_sugar"] */
    private List<String> dietaryRestrictions;

    /** 口味偏好 如["sichuan","cantonese"] */
    private List<String> preferredCuisines;

    /** 目标饮水量(ml) */
    private Integer dailyWaterIntake;

    /** 饮食目标描述 */
    private String dietaryGoal;
}
