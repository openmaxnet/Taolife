package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI模型厂商配置实体
 * 对应数据库表 tl_ai_provider
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
@Table("tl_ai_provider")
public class AiProvider implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 厂商ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
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
     * API Key（加密存储）
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
     * 优先级（负载均衡用，数字越大越优先）
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
