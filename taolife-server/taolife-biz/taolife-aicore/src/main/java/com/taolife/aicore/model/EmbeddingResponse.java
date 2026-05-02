package com.taolife.aicore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Embedding 响应模型（OpenAI API 兼容）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingResponse {

    /**
     * 对象类型
     */
    private String object;

    /**
     * 向量数据列表
     */
    private List<EmbeddingData> data;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * Token用量
     */
    private Usage usage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmbeddingData {
        /**
         * 对象类型
         */
        private String object;

        /**
         * 索引
         */
        private Integer index;

        /**
         * 向量数组
         */
        private float[] embedding;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        /**
         * 提示token数
         */
        private Integer promptTokens;

        /**
         * 总token数
         */
        private Integer totalTokens;
    }
}
