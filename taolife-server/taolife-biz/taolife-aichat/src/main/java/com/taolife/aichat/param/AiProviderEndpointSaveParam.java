package com.taolife.aichat.param;

import lombok.Data;

/**
 * AI厂商端点配置保存参数
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
public class AiProviderEndpointSaveParam {

    /**
     * ID（修改时传入）
     */
    private String id;

    /**
     * 厂商ID
     */
    private String providerId;

    /**
     * 端点类型：model_api/tool_api/agent_api/file_api/batch_api/knowledge_api/realtime_api
     */
    private String endpointType;

    /**
     * 端点URI
     */
    private String endpointUri;

    /**
     * 请求类型：GET/POST/PUT/DELETE/WSS
     */
    private String requestType;

    /**
     * 超时时间(ms)
     */
    private Integer timeoutMs;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 是否启用：0-否，1-是
     */
    private Integer isEnabled;

    /**
     * 描述
     */
    private String description;

    /**
     * 额外配置JSON
     */
    private String configJson;
}