package com.taolife.identity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.identity.entity.SysAgreement;
import com.taolife.identity.param.SysAgreementSaveParam;
import com.taolife.identity.service.ISysAgreementAdminService;
import com.taolife.identity.vo.SysAgreementAdminVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 系统协议管理控制器（Admin）
 *
 * @author 文二
 * @date 2026-04-28
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/identity/admin/agreement")
@RequiredArgsConstructor
public class SysAgreementAdminController {

    private final ISysAgreementAdminService agreementAdminService;

    /**
     * 分页查询协议列表
     * 根据关键词分页查询系统协议
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param keyword  关键词（可选，按协议名称/编码模糊搜索）
     * @return 分页结果
     */
    @GetMapping("/getAgreementPage")
    public ExceptionResult<PageResult<SysAgreementAdminVO>> getAgreementPage(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("查询系统协议分页：pageNo={}，pageSize={}，keyword={}", pageNo, pageSize, keyword);
        return ExceptionResult.success(agreementAdminService.getAgreementPage(pageNo, pageSize, keyword));
    }

    /**
     * 获取协议详情
     * 根据ID获取系统协议详细信息
     *
     * @param id 协议ID
     * @return 协议详情
     */
    @GetMapping("/getAgreementDetail")
    public ExceptionResult<SysAgreement> getAgreementDetail(@RequestParam("id") String id) {
        log.info("查询系统协议详情：id={}", id);
        return ExceptionResult.success(agreementAdminService.getAgreementDetail(id));
    }

    /**
     * 创建协议
     * 新增一条系统协议记录
     *
     * @param param 协议创建参数
     * @return 操作结果
     */
    @PostMapping("/createAgreement")
    public ExceptionResult<Void> createAgreement(@Valid @RequestBody SysAgreementSaveParam param) {
        log.info("创建系统协议：code={}", param.getCode());
        agreementAdminService.createAgreement(param);
        return ExceptionResult.success();
    }

    /**
     * 修改协议信息
     * 根据ID修改系统协议的内容
     *
     * @param id    协议ID
     * @param param 协议修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyAgreementInfo")
    public ExceptionResult<Void> modifyAgreementInfo(
            @RequestParam("id") String id,
            @Valid @RequestBody SysAgreementSaveParam param) {
        log.info("修改系统协议：id={}，code={}", id, param.getCode());
        agreementAdminService.modifyAgreementInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除协议
     * 根据ID删除系统协议（逻辑删除）
     *
     * @param id 协议ID
     * @return 操作结果
     */
    @PostMapping("/removeAgreement")
    public ExceptionResult<Void> removeAgreement(@RequestParam("id") String id) {
        log.info("删除系统协议：id={}", id);
        agreementAdminService.removeAgreement(id);
        return ExceptionResult.success();
    }
}
