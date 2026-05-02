package com.taolife.common.model;

import lombok.Data;

import java.util.List;

/**
 * 向量化API响应
 * 用于接收向量模型API的响应数据
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
public class EmbeddingApiResponse {

    /**
     * 对象类型
     */
    private String object;

    /**
     * 向量化数据列表
     */
    private List<EmbeddingData> data;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 使用情况
     */
    private Usage usage;

    /**
     * 向量化数据
     */
    @Data
    public static class EmbeddingData {
        /**
         * 对象类型
         */
        private String object;

        /**
         * 向量数组
         */
        private float[] embedding;

        /**
         * 索引
         */
        private int index;
    }

    /**
     * 使用情况
     */
    @Data
    public static class Usage {
        /**
         * 提示词Token数
         */
        private int promptTokens;

        /**
         * 总Token数
         */
        private int totalTokens;
    }
}
