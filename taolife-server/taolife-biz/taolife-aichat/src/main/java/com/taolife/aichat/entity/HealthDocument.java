package com.taolife.aichat.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识库文档实体
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
public class HealthDocument {

    /**
     * 主键ID（Milvus中的ID）
     */
    private Long id;

    /**
     * 文档唯一ID
     */
    private String docId;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档内容
     */
    private String content;

    /**
     * 分类：constitution/food/acupoint/health
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 来源
     */
    private String source;

    /**
     * 向量表示（1024维）
     */
    private float[] embedding;

    /**
     * 相似度得分
     */
    private Double similarity;

    /**
     * 体质类型
     */
    private String constitutionType;

    /**
     * 季节
     */
    private String season;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
