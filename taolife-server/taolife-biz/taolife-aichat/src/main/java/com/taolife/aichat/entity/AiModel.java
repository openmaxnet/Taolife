package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI模型配置实体
 * 对应数据库表 tf_ai_model_instance
 *
 * @author 文二
 * @date 2026-04-14
 */
@Data
@Table("tl_ai_model")
public class AiModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模型ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 厂商ID
     */
    private String providerId;

    /**
     * 模型编码：glm-4-flash/gpt-4/qwen-turbo
     */
    private String modelCode;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型类型：chat/embedding
     */
    private String modelType;

    /**
     * 温度参数(0-2)
     */
    private Double temperature;

    /**
     * 最大输出Token数
     */
    private Integer maxTokens;

    /**
     * Top-P参数
     */
    private Double topP;

    /**
     * 支持思考模式:0-否,1-是
     */
    private Integer supportsThinking;

    /**
     * 支持图像输入:0-否,1-是
     */
    private Integer supportsImage;

    /**
     * 模型全局最大并发数(0=不限)
     */
    private Integer maxConcurrency;

    /**
     * 额外参数JSON(厂商特定)
     */
    private String extraParamsJson;

    /**
     * 模型能力JSON（如是否支持function call）
     */
    private String capabilitiesJson;

    /**
     * 是否默认模型：0-否，1-是
     */
    private Integer isDefault;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}