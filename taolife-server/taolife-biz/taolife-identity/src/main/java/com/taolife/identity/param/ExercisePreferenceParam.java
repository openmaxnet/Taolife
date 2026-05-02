package com.taolife.identity.param;

import lombok.Data;

/**
 * 运动偏好参数
 */
@Data
public class ExercisePreferenceParam {

    /** 偏好运动类型：1有氧 2力量 3柔韧 4球类 5传统功法 */
    private Integer preferredExerciseType;

    /** 偏好运动强度：1低 2中 3高 */
    private Integer preferredExerciseIntensity;

    /** 偏好运动时间：1清晨 2上午 3下午 4傍晚 5晚上 */
    private Integer preferredExerciseTime;

    /** 运动时长(分钟) */
    private Integer preferredExerciseDuration;
}
