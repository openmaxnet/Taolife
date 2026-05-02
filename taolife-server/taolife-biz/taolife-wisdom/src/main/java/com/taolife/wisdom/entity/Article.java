package com.taolife.wisdom.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 养生知识文章实体
 * 对应数据库表 tl_wis_article
 *
 * @author 文二
 * @date 2026-03-21
 */
@Data
@Table("tl_wis_article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID（UUID）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 分类：1-养生方法，2-四季养生，3-节气养生，4-食疗方案，5-中医知识
     */
    private Integer category;

    /**
     * 标签（JSON数组）
     */
    private String tags;

    /**
     * 封面图
     */
    private String coverImageUrl;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 正文内容
     */
    private String content;

    /**
     * 内容类型：1-Markdown，2-HTML
     */
    private Integer contentType;

    /**
     * 作者
     */
    private String author;

    /**
     * 来源
     */
    private String source;

    /**
     * 关联体质编码
     */
    private String relatedConstitutionCodes;

    /**
     * 关联季节：1-春，2-夏，3-秋，4-冬
     */
    private Integer relatedSeason;

    /**
     * 关联节气
     */
    private String relatedSolarTerm;

    /**
     * 阅读数
     */
    private Integer readCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 是否推荐：0-否，1-是
     */
    private Integer isRecommended;

    /**
     * 是否精选：0-否，1-是
     */
    private Integer isFeatured;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
