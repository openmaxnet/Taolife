package com.taolife.common.model;

import lombok.Data;

/**
 * 向量化API请求
 * 用于调用向量模型API的通用请求参数
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
public class EmbeddingApiRequest {

    /**
     * 模型名称
     * 如：qwen3-embedding-8b
     */
    private String model;

    /**
     * 输入文本
     * 需要向量化的文本内容
     */
    private String input;

    /**
     * 编码格式
     * 默认：float
     */
    private String encodingFormat;
}
