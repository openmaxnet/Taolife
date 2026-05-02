package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 场景-模型关联实体（负载均衡）
 */
@Data
@Table("tl_ai_scene_model")
public class AiSceneModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    private String sceneConfigId;

    private String modelId;

    private Integer priority;

    private Integer maxConcurrency;

    private Integer weight;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
