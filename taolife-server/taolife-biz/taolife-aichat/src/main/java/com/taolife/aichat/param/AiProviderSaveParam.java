package com.taolife.aichat.param;

import lombok.Data;

/**
 * AI模型厂商保存参数
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiProviderSaveParam {

    /**
     * ID（修改时传入）
     */
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
     * API Key
     */
    private String apiKey;

    /**
     * API Key是否加密：0-否，1-是
     */
    private Integer isEncrypted;

    /**
     * 是否默认厂商：0-否，1-是
     */
    private Integer isDefault;

    /**
     * 优先级（负载均衡用）
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
}
