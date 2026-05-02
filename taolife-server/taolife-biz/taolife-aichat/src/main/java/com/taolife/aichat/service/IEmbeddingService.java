package com.taolife.aichat.service;

/**
 * 向量化服务接口
 * 负责将文本转换为向量表示
 *
 * @author 文二
 * @date 2026-03-26
 */
public interface IEmbeddingService {

    /**
     * 将文本向量化
     *
     * @param text 文本内容
     * @return 向量数组
     */
    float[] embed(String text);

    /**
     * 将文本批量向量化
     *
     * @param texts 文本列表
     * @return 向量数组列表
     */
    float[][] embedBatch(String[] texts);

    /**
     * 计算向量相似度
     *
     * @param vec1 向量1
     * @param vec2 向量2
     * @return 相似度
     */
    double cosineSimilarity(float[] vec1, float[] vec2);
}