package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI提示词模板实体
 * 对应数据库表 tf_ai_prompt_template
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
@Table("tl_ai_prompt_template")
public class AiPromptTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 模板编码：chat_system/constitution_assessment/health_advice
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
     * 模板内容（支持变量占位符 ${var}）
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
