package com.taolife.aichat.service.impl;

import com.taolife.aichat.entity.HealthDocument;
import com.taolife.aichat.repository.MilvusDocRepo;
import com.taolife.aichat.service.IDocStoreService;
import com.taolife.aichat.service.IEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档存储服务实现
 * 基于Milvus向量数据库
 *
 * @author 文二
 * @date 2026-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocStoreServiceImpl implements IDocStoreService {

    private final MilvusDocRepo milvusDocRepo;
    private final IEmbeddingService embeddingService;

    /**
     * 添加文档并生成向量
     * 将文档标题和内容拼接后调用向量化服务生成向量，存入Milvus向量数据库
     *
     * @param document 健康文档实体（需包含标题和内容）
     */
    @Override
    public void addDocumentWithEmbedding(HealthDocument document) {
        // 向量化文档内容
        String docText = (document.getTitle() != null ? document.getTitle() : "") + " "
                + (document.getContent() != null ? document.getContent() : "");
        float[] embedding = embeddingService.embed(docText);
        document.setEmbedding(embedding);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        milvusDocRepo.insert(document);
        log.info("向量化并添加文档成功，docId: {}", document.getDocId());
    }

    /**
     * 批量添加文档并生成向量
     * 遍历文档列表，逐条调用添加文档方法
     *
     * @param documents 健康文档实体列表
     */
    @Override
    public void addDocumentsWithEmbedding(List<HealthDocument> documents) {
        documents.forEach(this::addDocumentWithEmbedding);
        log.info("批量向量化并添加文档成功，数量: {}", documents.size());
    }

    /**
     * 删除文档
     * 根据文档ID从Milvus向量数据库中删除指定的文档记录
     *
     * @param docId 文档ID
     */
    @Override
    public void deleteDocument(String docId) {
        milvusDocRepo.deleteByDocId(docId);
        log.info("删除文档成功，docId: {}", docId);
    }

    /**
     * 更新文档
     * 内容变更时重新生成向量，并更新Milvus中的文档记录
     *
     * @param document 待更新的健康文档实体
     */
    @Override
    public void updateDocument(HealthDocument document) {
        // 内容变更时重新向量化
        String docText = (document.getTitle() != null ? document.getTitle() : "") + " "
                + (document.getContent() != null ? document.getContent() : "");
        float[] embedding = embeddingService.embed(docText);
        document.setEmbedding(embedding);
        document.setUpdatedAt(LocalDateTime.now());
        milvusDocRepo.update(document);
        log.info("更新文档成功，docId: {}", document.getDocId());
    }

    /**
     * 按分类查询文档
     * 根据指定的分类从Milvus中查询文档列表
     *
     * @param category 文档分类名称
     * @return 属于该分类的健康文档列表
     */
    @Override
    public List<HealthDocument> getDocumentsByCategory(String category) {
        return milvusDocRepo.queryByCategory(category, 100);
    }
}
