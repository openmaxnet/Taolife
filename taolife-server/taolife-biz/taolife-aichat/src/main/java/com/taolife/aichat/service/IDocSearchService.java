package com.taolife.aichat.service;

import com.taolife.aichat.entity.HealthDocument;

import java.util.List;

/**
 * 文档检索服务接口
 * 基于Milvus向量检索
 *
 * @author 文二
 * @date 2026-03-26
 */
public interface IDocSearchService {

    /**
     * 向量检索
     *
     * @param query 用户问题
     * @param topK 返回数量
     * @return 检索结果
     */
    List<HealthDocument> retrieve(String query, int topK);

    /**
     * 带分类过滤的向量检索
     *
     * @param query 用户问题
     * @param topK 返回数量
     * @param category 分类过滤
     * @return 检索结果
     */
    List<HealthDocument> retrieve(String query, int topK, String category);
}
