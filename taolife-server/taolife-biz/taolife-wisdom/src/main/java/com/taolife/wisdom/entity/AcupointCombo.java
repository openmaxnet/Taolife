package com.taolife.wisdom.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 穴位配伍实体
 * 对应数据库表 tl_wis_acupoint_combo
 *
 * @author 文二
 * @date 2026-03-24
 */
@Data
@Table("tl_wis_acupoint_combo")
public class AcupointCombo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配伍ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 配伍名称
     */
    private String name;

    /**
     * 分类：1-体质调理，2-症状调理，3-季节养生，4-日常保健
     */
    private Integer category;

    /**
     * 适用体质编码
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
     * 配伍描述
     */
    private String description;

    /**
     * 穴位ID列表（JSON数组）
     */
    private String acupointIds;

    /**
     * 穴位名称列表
     */
    private String acupointNames;

    /**
     * 按摩顺序：1-依次，2-同时
     */
    private Integer sequence;

    /**
     * 操作方法
     */
    private String operationMethod;

    /**
     * 每次按摩时长（分钟）
     */
    private Integer durationMin;

    /**
     * 每日建议次数
     */
    private Integer frequencyPerDay;

    /**
     * 功效说明
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
     * 示意图
     */
    private String imageUrl;

    /**
     * 演示视频
     */
    private String videoUrl;

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
