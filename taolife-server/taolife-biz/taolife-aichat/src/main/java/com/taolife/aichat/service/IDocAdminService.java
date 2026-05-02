package com.taolife.aichat.service;

import com.taolife.aichat.param.DocSaveAdminParam;
import com.taolife.aichat.param.DocumentDetailAdminParam;
import com.taolife.aichat.param.DocumentPageAdminParam;
import com.taolife.aichat.param.RemoveDocumentAdminParam;
import com.taolife.aichat.vo.DocAdminVO;
import com.taolife.common.utils.PageResult;

/**
 * 管理员文档管理服务接口
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IDocAdminService {

    /**
     * 分页查询文档列表
     *
     * @param param 分页查询参数
     * @return 文档分页结果
     */
    PageResult<DocAdminVO> getDocumentPage(DocumentPageAdminParam param);

    /**
     * 获取文档详情
     *
     * @param param 详情查询参数
     * @return 文档详情
     */
    DocAdminVO getDocumentDetail(DocumentDetailAdminParam param);

    /**
     * 创建文档
     *
     * @param param 创建参数
     */
    void createDocument(DocSaveAdminParam param);

    /**
     * 修改文档信息
     *
     * @param param 修改参数
     */
    void modifyDocumentInfo(DocSaveAdminParam param);

    /**
     * 删除文档（逻辑删除）
     *
     * @param param 删除参数
     */
    void removeDocument(RemoveDocumentAdminParam param);
}
