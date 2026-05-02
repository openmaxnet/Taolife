package com.taolife.aichat.service;

import com.taolife.aichat.entity.HealthDocument;

import java.util.List;

/**
 * 文档存储服务接口
 *
 * @author 文二
 * @date 2026-03-26
 */
public interface IDocStoreService {

    /**
     * 向量化并添加文档
     *
     * @param document 文档实体
     */
    void addDocumentWithEmbedding(HealthDocument document);

    /**
     * 批量向量化并添加文档
     *
     * @param documents 文档实体列表
     */
    void addDocumentsWithEmbedding(List<HealthDocument> documents);

    /**
     * 删除文档
     *
     * @param docId 文档ID
     */
    void deleteDocument(String docId);

    /**
     * 更新文档（如内容变更则重新向量化）
     *
     * @param document 文档实体
     */
    void updateDocument(HealthDocument document);

    /**
     * 根据分类获取文档
     *
     * @param category 分类编码
     * @return 文档列表
     */
    List<HealthDocument> getDocumentsByCategory(String category);
}
