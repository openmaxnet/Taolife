package com.taolife.aicore.config;

import lombok.Data;

/**
 * Chat配置VO（厂商无关）
 * 由业务层从DB/缓存加载后传递给 AiClient
 */
@Data
public class ChatConfigVO {

    /**
     * 完整URL（已拼接 provider.apiEndpoint + endpoint.endpointUri）
     */
    private String fullUrl;

    /**
     * 模型编码
     */
    private String model;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 温度参数
     */
    private Double temperature;

    /**
     * 最大Token数
     */
    private Integer maxTokens;

    /**
     * Top-P参数
     */
    private Double topP;

    /**
     * 加密存储的API Key（由调用方解密后传给 AiClient）
     */
    private String encryptedApiKey;

    /**
     * 模型是否支持思考模式
     */
    private Boolean supportsThinking;

    /**
     * 模型是否支持图像输入
     */
    private Boolean supportsImage;

    /**
     * 厂商ID
     */
    private String providerId;

    /**
     * 厂商编码（用于选择 VendorAdapter）
     */
    private String vendorCode;

    /**
     * 模型ID（用于并发计数）
     */
    private String modelId;
}
