package com.taolife.identity.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户偏好/健康档案实体
 * 对应数据库表 tf_user_preference
 *
 * @author 文二
 * @date 2026-04-05
 */
@Data
@Table("tl_id_user_preference")
public class UserPreference implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 偏好ID */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 账号ID */
    private String accountId;

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

    /** 不喜欢的食材ID列表JSON */
    private String dislikedFoods;

    /** 过敏食材ID列表JSON */
    private String allergicFoods;

    /** 饮食限制标签 JSON List<String> 如["vegetarian","low_sugar"] */
    private String dietaryRestrictions;

    /** 口味偏好 JSON List<String> 如["sichuan","cantonese"] */
    private String preferredCuisines;

    /** 目标饮水量(ml) */
    private Integer dailyWaterIntake;

    /** 饮食目标描述 */
    private String dietaryGoal;

    // ──── 运动偏好 ────

    /** 偏好运动类型：1有氧 2力量 3柔韧 4球类 5传统功法 */
    private Integer preferredExerciseType;

    /** 偏好运动强度：1低 2中 3高 */
    private Integer preferredExerciseIntensity;

    /** 偏好运动时间：1清晨 2上午 3下午 4傍晚 5晚上 */
    private Integer preferredExerciseTime;

    /** 运动时长(分钟) */
    private Integer preferredExerciseDuration;

    // ──── 生活习惯 ────

    /** 就寝时间 如"23:00" */
    private String sleepTime;

    /** 起床时间 如"07:00" */
    private String wakeTime;

    /** 睡眠质量：1优 2良 3一般 4差 */
    private Integer sleepQuality;

    /** 压力水平：1低 2中 3高 */
    private Integer stressLevel;

    /** 吸烟状态：0从不 1已戒 2吸烟 */
    private Integer smokingStatus;

    /** 饮酒状态：0从不 1偶尔 2经常 */
    private Integer drinkingStatus;

    /** 职业 */
    private String occupation;

    // ──── 健康目标 ────

    /** 主要目标：1免疫 2睡眠 3体重 4压力 5消化 6体力 */
    private Integer healthGoalPrimary;

    /** 健康目标标签 JSON List<String> 如["immunity","sleep"] */
    private String healthGoals;

    // ──── AI偏好 ────

    /** AI回复风格：1专业 2亲切 3简洁 */
    private Integer aiTonePreference;

    /** AI详细程度：1简要 2适中 3详细 */
    private Integer aiDetailLevel;

    // ──── 行为追踪（内部字段） ────

    /** 偏好穴位ID列表JSON */
    private String preferredAcupoints;

    /** 食材偏好评分JSON */
    private String foodPreferenceScore;

    /** 运动偏好评分JSON */
    private String exercisePreferenceScore;

    /** 总交互次数 */
    private Integer totalInteractions;

    /** 最后学习时间 */
    private LocalDateTime lastLearnTime;

    // ──── 完善度 ────

    /** 偏好完善度 0-100 */
    private Integer preferenceCompleteness;

    /** 用户最后手动编辑时间 */
    private LocalDateTime lastUserEditTime;

    // ──── 基础字段 ────

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 是否删除：0-否 1-是 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;
}
