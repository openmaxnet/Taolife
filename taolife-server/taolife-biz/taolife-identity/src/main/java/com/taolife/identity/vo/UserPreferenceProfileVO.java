package com.taolife.identity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户偏好/健康档案聚合VO
 */
@Data
public class UserPreferenceProfileVO {

    /** 偏好ID */
    private String id;

    // ──── 身体数据 ────

    /** 身高(cm) */
    private BigDecimal height;

    /** 体重(kg) */
    private BigDecimal weight;

    /** 血型：1-A 2-B 3-AB 4-O */
    private Integer bloodType;

    /** 过敏史 */
    private String allergyHistory;

    /** 病史 */
    private String medicalHistory;

    // ──── 饮食偏好 ────

    /** 偏好食物性质JSON */
    private String preferredFoodNature;

    /** 口味偏好：1清淡 2浓郁 3辛辣 4偏甜 */
    private Integer preferredFoodTexture;

    /** 不喜欢的食材ID列表 */
    private List<String> dislikedFoods;

    /** 过敏食材ID列表 */
    private List<String> allergicFoods;

    /** 饮食限制标签 */
    private List<String> dietaryRestrictions;

    /** 口味偏好 */
    private List<String> preferredCuisines;

    /** 目标饮水量(ml) */
    private Integer dailyWaterIntake;

    /** 饮食目标描述 */
    private String dietaryGoal;

    // ──── 运动偏好 ────

    /** 偏好运动类型 */
    private Integer preferredExerciseType;

    /** 偏好运动强度 */
    private Integer preferredExerciseIntensity;

    /** 偏好运动时间 */
    private Integer preferredExerciseTime;

    /** 运动时长(分钟) */
    private Integer preferredExerciseDuration;

    // ──── 生活习惯 ────

    /** 就寝时间 */
    private String sleepTime;

    /** 起床时间 */
    private String wakeTime;

    /** 睡眠质量 */
    private Integer sleepQuality;

    /** 压力水平 */
    private Integer stressLevel;

    /** 吸烟状态 */
    private Integer smokingStatus;

    /** 饮酒状态 */
    private Integer drinkingStatus;

    /** 职业 */
    private String occupation;

    // ──── 健康目标 ────

    /** 主要目标 */
    private Integer healthGoalPrimary;

    /** 健康目标标签 */
    private List<String> healthGoals;

    // ──── AI偏好 ────

    /** AI回复风格 */
    private Integer aiTonePreference;

    /** AI详细程度 */
    private Integer aiDetailLevel;

    // ──── 完善度 ────

    /** 偏好完善度 0-100 */
    private Integer preferenceCompleteness;

    /** 用户最后手动编辑时间 */
    private LocalDateTime lastUserEditTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
