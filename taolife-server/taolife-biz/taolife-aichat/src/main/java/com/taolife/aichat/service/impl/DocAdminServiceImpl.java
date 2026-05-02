package com.taolife.aichat.service.impl;

import com.taolife.aichat.entity.HealthDocument;
import com.taolife.aichat.param.DocSaveAdminParam;
import com.taolife.aichat.param.DocumentDetailAdminParam;
import com.taolife.aichat.param.DocumentPageAdminParam;
import com.taolife.aichat.param.RemoveDocumentAdminParam;
import com.taolife.aichat.repository.MilvusDocRepo;
import com.taolife.aichat.service.IDocAdminService;
import com.taolife.aichat.service.IEmbeddingService;
import com.taolife.aichat.vo.DocAdminVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 管理员文档管理服务实现
 * 基于Milvus向量数据库
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocAdminServiceImpl implements IDocAdminService {

    private final MilvusDocRepo milvusDocRepo;
    private final IEmbeddingService embeddingService;

    /**
     * 分页查询文档列表
     *
     * @param param 分页查询参数
     * @return 文档分页结果
     */
    @Override
    public PageResult<DocAdminVO> getDocumentPage(DocumentPageAdminParam param) {
        PageResult<HealthDocument> docPage = milvusDocRepo.pageQuery(
                param.getPageNo(), param.getPageSize(), param.getCategory(), param.getKeyword());

        PageResult<DocAdminVO> result = new PageResult<>();
        result.setList(docPage.getList().stream().map(this::convertToVO).toList());
        result.setPageNo(docPage.getPageNo());
        result.setPageSize(docPage.getPageSize());
        result.setTotalPage(docPage.getTotalPage());
        result.setTotalRow(docPage.getTotalRow());
        return result;
    }

    /**
     * 获取文档详情
     *
     * @param param 详情查询参数
     * @return 文档详情
     */
    @Override
    public DocAdminVO getDocumentDetail(DocumentDetailAdminParam param) {
        HealthDocument doc = milvusDocRepo.queryById(param.getId());
        if (doc == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "文档不存在");
        }
        return convertToVO(doc);
    }

    /**
     * 创建文档（含向量化存储）
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDocument(DocSaveAdminParam param) {
        HealthDocument doc = new HealthDocument();
        doc.setDocId(UUID.randomUUID().toString().replace("-", ""));
        doc.setTitle(param.getTitle());
        doc.setContent(param.getContent());
        doc.setCategory(param.getCategory());
        doc.setTags(param.getTags());
        doc.setSource(param.getSource());
        doc.setConstitutionType(param.getConstitutionType());
        doc.setSeason(param.getSeason());
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());

        // 向量化文档内容
        String docText = (doc.getTitle() != null ? doc.getTitle() : "") + " "
                + (doc.getContent() != null ? doc.getContent() : "");
        doc.setEmbedding(embeddingService.embed(docText));

        milvusDocRepo.insert(doc);
    }

    /**
     * 修改文档信息（含重新向量化）
     *
     * @param param 修改参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyDocumentInfo(DocSaveAdminParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        HealthDocument existing = milvusDocRepo.queryById(param.getId());
        if (existing == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "文档不存在");
        }

        existing.setTitle(param.getTitle());
        existing.setContent(param.getContent());
        existing.setCategory(param.getCategory());
        existing.setTags(param.getTags());
        existing.setSource(param.getSource());
        existing.setConstitutionType(param.getConstitutionType());
        existing.setSeason(param.getSeason());
        existing.setUpdatedAt(LocalDateTime.now());

        // 重新向量化
        String docText = (existing.getTitle() != null ? existing.getTitle() : "") + " "
                + (existing.getContent() != null ? existing.getContent() : "");
        existing.setEmbedding(embeddingService.embed(docText));

        milvusDocRepo.update(existing);
    }

    /**
     * 删除文档
     *
     * @param param 删除参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDocument(RemoveDocumentAdminParam param) {
        milvusDocRepo.deleteById(param.getId());
    }

    /**
     * 文档实体转VO
     *
     * @param doc 文档实体
     * @return 文档VO
     */
    private DocAdminVO convertToVO(HealthDocument doc) {
        DocAdminVO vo = new DocAdminVO();
        vo.setId(doc.getId());
        vo.setDocId(doc.getDocId());
        vo.setTitle(doc.getTitle());
        vo.setContent(doc.getContent());
        vo.setCategory(doc.getCategory());
        vo.setCategoryName(getCategoryName(doc.getCategory()));
        vo.setTags(doc.getTags());
        vo.setSource(doc.getSource());
        vo.setConstitutionType(doc.getConstitutionType());
        vo.setSeason(doc.getSeason());
        vo.setCreatedAt(doc.getCreatedAt());
        return vo;
    }

    /**
     * 获取分类名称
     *
     * @param category 分类编码
     * @return 分类名称
     */
    private String getCategoryName(String category) {
        if (category == null) return "未知";
        return switch (category) {
            case "constitution" -> "体质";
            case "food" -> "食疗";
            case "acupoint" -> "穴位";
            case "health" -> "养生";
            default -> category;
        };
    }
}
