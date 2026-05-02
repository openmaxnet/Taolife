package com.taolife.aichat.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI提示词模板VO
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiPromptTemplateVO {

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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
