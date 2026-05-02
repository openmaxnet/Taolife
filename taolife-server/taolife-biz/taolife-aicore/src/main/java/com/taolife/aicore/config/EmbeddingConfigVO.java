package com.taolife.aicore.config;

import lombok.Data;

/**
 * Embedding配置VO（厂商无关）
 */
@Data
public class EmbeddingConfigVO {

    /**
     * 完整API地址
     */
    private String fullUrl;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 模型显示名称
     */
    private String modelName;

    /**
     * 向量维度
     */
    private Integer dimensions;

    /**
     * 加密后的API密钥
     */
    private String encryptedApiKey;

    /**
     * 供应商ID
     */
    private String providerId;

    /**
     * 厂商编码
     */
    private String vendorCode;
}
