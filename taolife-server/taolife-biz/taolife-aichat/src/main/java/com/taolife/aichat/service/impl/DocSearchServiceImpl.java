package com.taolife.aichat.service.impl;

import com.taolife.aichat.entity.HealthDocument;
import com.taolife.aichat.repository.MilvusDocRepo;
import com.taolife.aichat.service.IDocSearchService;
import com.taolife.aichat.service.IEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文档检索服务实现
 * 基于Milvus向量检索
 *
 * @author 文二
 * @date 2026-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocSearchServiceImpl implements IDocSearchService {

    private final IEmbeddingService embeddingService;
    private final MilvusDocRepo milvusDocRepo;

    /**
     * 向量检索文档（无分类过滤）
     * 将查询文本向量化后，在Milvus中进行相似度检索，返回最相关的文档
     *
     * @param query 查询文本
     * @param topK 返回的最相似文档数量
     * @return 匹配的健康文档列表
     */
    @Override
    public List<HealthDocument> retrieve(String query, int topK) {
        return retrieve(query, topK, null);
    }

    /**
     * 向量检索文档（按分类过滤）
     * 将查询文本向量化后，在Milvus中按指定分类进行相似度检索
     *
     * @param query 查询文本
     * @param topK 返回的最相似文档数量
     * @param category 文档分类（可为null，表示不限制分类）
     * @return 匹配的健康文档列表
     */
    @Override
    public List<HealthDocument> retrieve(String query, int topK, String category) {
        log.info("开始向量检索，查询: {}, topK: {}, category: {}", query, topK, category);
        try {
            float[] queryVector = embeddingService.embed(query);
            List<HealthDocument> results = milvusDocRepo.vectorSearch(queryVector, topK, category);
            log.info("向量检索完成，结果数: {}", results.size());
            return results;
        } catch (Exception e) {
            log.error("向量检索失败", e);
            return List.of();
        }
    }
}
