package com.taolife.wisdom.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 运动项目实体
 * 对应数据库表 tl_wis_exercise
 *
 * @author 文二
 * @date 2026-03-24
 */
@Data
@Table("tl_wis_exercise")
public class Exercise implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运动ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 运动名称
     */
    private String name;

    /**
     * 拼音
     */
    private String namePinyin;

    /**
     * 运动分类：1-传统功法，2-有氧运动，3-力量训练，4-柔韧训练，5-休闲运动
     */
    private Integer category;

    /**
     * 运动强度：1-温和，2-轻度，3-中度，4-重度
     */
    private Integer intensity;

    /**
     * 适宜体质编码（JSON数组）
     */
    private String targetConstitutionCodes;

    /**
     * 不适宜体质编码（JSON数组）
     */
    private String contraConstitutionCodes;

    /**
     * 适宜季节（JSON数组：1-春，2-夏，3-秋，4-冬）
     */
    private String targetSeason;

    /**
     * 适宜年龄段：1-青年，2-中年，3-老年
     */
    private String targetAgeGroup;

    /**
     * 养生功效
     */
    private String efficacy;

    /**
     * 适宜症状
     */
    private String indications;

    /**
     * 禁忌
     */
    private String contraindications;

    /**
     * 运动描述
     */
    private String description;

    /**
     * 动作步骤
     */
    private String steps;

    /**
     * 建议时长（分钟）
     */
    private Integer durationMin;

    /**
     * 消耗卡路里
     */
    private Integer caloriesConsumption;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficultyLevel;

    /**
     * 演示视频
     */
    private String videoUrl;

    /**
     * 视频类型：1-直链(COS/VOD)，2-B站嵌入，3-其他外链
     */
    private Integer videoType;

    /**
     * 图片
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
