package com.taolife.aichat.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI模型厂商VO
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiProviderVO {

    private String id;

    /**
     * 厂商编码：glm/openai/claude/qwen
     */
    private String providerCode;

    /**
     * 厂商名称：智谱AI/OpenAI/Claude/通义千问
     */
    private String providerName;

    /**
     * 厂商类型：chat/embedding/image
     */
    private String providerType;

    /**
     * API Endpoint
     */
    private String apiEndpoint;

    /**
     * API Key（脱敏显示）
     */
    private String apiKeyMasked;

    /**
     * API Key是否加密：0-否，1-是
     */
    private Integer isEncrypted;

    /**
     * 是否默认厂商：0-否，1-是
     */
    private Integer isDefault;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 厂商特定配置JSON
     */
    private String configJson;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
