package com.taolife.aichat.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI场景配置VO
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiSceneConfigVO {

    private String id;

    /**
     * 场景编码
     */
    private String sceneCode;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 模型实例ID
     */
    private String modelInstanceId;

    /**
     * 模型名称（冗余字段）
     */
    private String modelName;

    /**
     * 模型编码（冗余字段）
     */
    private String modelCode;

    /**
     * 提示词模板ID
     */
    private String promptTemplateId;

    /**
     * 提示词模板名称（冗余字段）
     */
    private String promptTemplateName;

    /**
     * 该场景覆盖的模型参数
     */
    private String parametersJson;

    /**
     * 额外配置JSON
     */
    private String extraConfigJson;

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
