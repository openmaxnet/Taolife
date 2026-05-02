package com.taolife.plan.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 生活方案实体类
 * 对应数据库表 tl_plan_lifestyle_plan
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
@Table("tl_plan_lifestyle_plan")
public class LifestylePlan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方案ID（主键）
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
     * 适用体质编码（JSON数组）
     */
    private String targetConstitutionCodes;

    /**
     * 适用季节（JSON数组：1-春，2-夏，3-秋，4-冬）
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
     * 睡眠建议
     */
    private String sleepAdvice;

    /**
     * 睡眠时间建议JSON
     */
    private String sleepTime;

    /**
     * 情志调节建议
     */
    private String emotionAdvice;

    /**
     * 情志调节方法JSON
     */
    private String emotionMethods;

    /**
     * 起居建议
     */
    private String livingAdvice;

    /**
     * 起居时间安排JSON
     */
    private String livingSchedule;

    /**
     * 其他建议
     */
    private String otherAdvice;

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
