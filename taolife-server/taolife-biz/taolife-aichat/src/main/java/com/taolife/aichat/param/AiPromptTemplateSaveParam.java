package com.taolife.aichat.param;

import lombok.Data;

/**
 * AI提示词模板保存参数
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiPromptTemplateSaveParam {

    /**
     * ID（修改时传入）
     */
    private String id;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板类型：system_prompt/user_prompt/assistant_prompt
     */
    private String templateType;

    /**
     * 模板内容
     */
    private String templateContent;

    /**
     * 变量定义JSON
     */
    private String variablesJson;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 是否启用：0-否，1-是
     */
    private Integer isEnabled;

    /**
     * 描述
     */
    private String description;
}
