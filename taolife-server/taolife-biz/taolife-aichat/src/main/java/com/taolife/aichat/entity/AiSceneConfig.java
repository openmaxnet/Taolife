package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI场景配置实体
 * 对应数据库表 tf_ai_scene_config
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
@Table("tl_ai_scene_config")
public class AiSceneConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 场景编码：ai_chat/constitution_assessment/plan_generation
     */
    private String sceneCode;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 使用的模型实例ID
     */
    private String modelInstanceId;

    /**
     * 使用的提示词模板ID
     */
    private String promptTemplateId;

    /**
     * 该场景覆盖的模型参数
     */
    private String parametersJson;

    /**
     * 额外配置（如相似度阈值等）
     */
    private String extraConfigJson;

    /**
     * 知识检索相似度阈值
     */
    private Double similarityThreshold;

    /**
     * 历史消息数量限制
     */
    private Integer historyLimit;

    /**
     * 知识检索TopK
     */
    private Integer knowledgeTopK;

    /**
     * 并发满载策略: REJECT/QUEUE/FALLBACK
     */
    private String concurrencyStrategy;

    /**
     * 是否启用：0-否，1-是
     */
    private Integer isEnabled;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
