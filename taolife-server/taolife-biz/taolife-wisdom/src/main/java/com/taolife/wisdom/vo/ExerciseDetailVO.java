package com.taolife.wisdom.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 运动项目详情VO
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class ExerciseDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String namePinyin;
    private Integer category;
    private String categoryName;
    private Integer intensity;
    private String intensityName;
    private Integer difficultyLevel;
    private Integer durationMin;
    private Integer caloriesConsumption;
    private String efficacy;
    private String imageUrl;
    private Integer videoType;
    private String videoUrl;
    private Integer viewCount;
    private Integer collectCount;
    private String description;
    private String steps;
    private String indications;
    private String contraindications;
    private String targetConstitutionCodes;
    private String contraConstitutionCodes;
    private String targetSeason;
    private String targetAgeGroup;

    private List<ExerciseListVO> relatedExercises;
}
