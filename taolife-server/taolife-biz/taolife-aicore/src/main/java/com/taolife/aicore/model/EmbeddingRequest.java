package com.taolife.aicore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Embedding 请求模型（OpenAI API 兼容）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingRequest {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 输入文本或文本列表
     */
    private Object input;

    /**
     * 编码格式
     */
    private String encodingFormat;

    /**
     * 向量维度
     */
    private Integer dimensions;
}
