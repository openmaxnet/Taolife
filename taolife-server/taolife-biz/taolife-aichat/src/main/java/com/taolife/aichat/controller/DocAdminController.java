package com.taolife.aichat.controller;

import com.taolife.aichat.param.DocumentDetailAdminParam;
import com.taolife.aichat.param.DocumentPageAdminParam;
import com.taolife.aichat.param.DocSaveAdminParam;
import com.taolife.aichat.param.RemoveDocumentAdminParam;
import com.taolife.aichat.service.IDocAdminService;
import com.taolife.aichat.vo.DocAdminVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员文档管理控制器
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/ai/admin/doc")
@RequiredArgsConstructor
public class DocAdminController {

    private final IDocAdminService adminDocService;

    /**
     * 分页查询文档列表
     *
     * @param param 分页查询参数
     * @return 文档分页结果
     */
    @GetMapping("/getDocumentPage")
    public ExceptionResult<PageResult<DocAdminVO>> getDocumentPage(DocumentPageAdminParam param) {
        return ExceptionResult.success(adminDocService.getDocumentPage(param));
    }

    /**
     * 获取文档详情
     *
     * @param param 详情查询参数
     * @return 文档详情
     */
    @GetMapping("/getDocumentDetail")
    public ExceptionResult<DocAdminVO> getDocumentDetail(DocumentDetailAdminParam param) {
        return ExceptionResult.success(adminDocService.getDocumentDetail(param));
    }

    /**
     * 创建文档
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createDocument")
    public ExceptionResult<Void> createDocument(@RequestBody DocSaveAdminParam param) {
        adminDocService.createDocument(param);
        return ExceptionResult.success();
    }

    /**
     * 修改文档信息
     *
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyDocumentInfo")
    public ExceptionResult<Void> modifyDocumentInfo(@RequestBody DocSaveAdminParam param) {
        adminDocService.modifyDocumentInfo(param);
        return ExceptionResult.success();
    }

    /**
     * 删除文档（逻辑删除）
     *
     * @param param 删除参数
     * @return 操作结果
     */
    @PostMapping("/removeDocument")
    public ExceptionResult<Void> removeDocument(@RequestBody RemoveDocumentAdminParam param) {
        adminDocService.removeDocument(param);
        return ExceptionResult.success();
    }
}
