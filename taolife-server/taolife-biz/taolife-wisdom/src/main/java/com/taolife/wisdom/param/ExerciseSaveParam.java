package com.taolife.wisdom.param;

import lombok.Data;

/**
 * 运动项目保存参数
 * 用于创建和修改运动项目信息
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class ExerciseSaveParam {

    private String name;
    private String namePinyin;
    private Integer category;
    private Integer intensity;
    private String targetConstitutionCodes;
    private String contraConstitutionCodes;
    private String targetSeason;
    private String targetAgeGroup;
    private String efficacy;
    private String indications;
    private String contraindications;
    private String description;
    private String steps;
    private Integer durationMin;
    private Integer caloriesConsumption;
    private Integer difficultyLevel;
    private String videoUrl;
    private Integer videoType;
    private String imageUrl;
    private Integer sortOrder;
}
