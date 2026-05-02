package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI厂商端点配置实体
 * 对应数据库表 tf_ai_provider_endpoint
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
@Table("tl_ai_provider_endpoint")
public class AiProviderEndpoint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 端点ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
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
     * 端点URI，如: /chat/completions
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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}