package com.taolife.aichat.param;

import lombok.Data;

/**
 * AI场景配置保存参数
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiSceneConfigSaveParam {

    /**
     * ID（修改时传入）
     */
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
     * 提示词模板ID
     */
    private String promptTemplateId;

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
}
