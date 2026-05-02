package com.taolife.wisdom.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 运动项目列表VO
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class ExerciseListVO implements Serializable {

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
    private Integer viewCount;
    private Integer collectCount;
}
