package com.taolife.plan.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 运动方案实体
 * 对应数据库表 tl_plan_exercise_plan
 *
 * @author 文二
 * @date 2026-03-24
 */
@Data
@Table("tl_plan_exercise_plan")
public class ExercisePlan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方案ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 方案名称
     */
    private String name;

    /**
     * 分类：1-体质调理，2-季节养生，3-症状调理，4-日常保健
     */
    private Integer category;

    /**
     * 适用体质编码
     */
    private String targetConstitutionCodes;

    /**
     * 适用季节：1-春，2-夏，3-秋，4-冬
     */
    private String targetSeason;

    /**
     * 针对症状
     */
    private String targetSymptom;

    /**
     * 方案描述
     */
    private String description;

    /**
     * 运动项目列表JSON
     */
    private String exerciseList;

    /**
     * 每周运动次数
     */
    private Integer frequencyPerWeek;

    /**
     * 每次运动时长（分钟）
     */
    private Integer durationPerTime;

    /**
     * 最佳运动时间：1-清晨，2-上午，3-下午，4-傍晚，5-晚上
     */
    private String bestTime;

    /**
     * 注意事项
     */
    private String precautions;

    /**
     * 禁忌
     */
    private String contraindications;

    /**
     * 封面图片
     */
    private String imageUrl;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 收藏次数
     */
    private Integer collectCount;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 是否禁用：0-否，1-是
     */
    private Integer isDisabled;

    /**
     * 用户方案ID（用于标识用户生成的方案）
     */
    private String userPlanId;

    /**
     * 用户方案历史ID（用于标识用户生成的方案历史）
     */
    private String userHistoryId;

    /**
     * AI生成的方案内容（Markdown格式）
     */
    private String generatedContent;

    /**
     * 方案标签，JSON数组格式
     */
    private String tags;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
