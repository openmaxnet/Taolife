package com.taolife.identity.controller;

import com.taolife.common.annotation.AuthSkip;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.identity.service.ISysAgreementService;
import com.taolife.identity.vo.SysAgreementVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统协议与声明控制器（用户端）
 *
 * @author 文二
 * @date 2026-04-28
 */
@RestController
@RequestMapping("/api/identity/agreement")
@RequiredArgsConstructor
public class SysAgreementController {

    private final ISysAgreementService agreementService;

    /**
     * 根据编码获取协议
     * 通过协议编码获取公开的协议内容（无需认证）
     *
     * @param code 协议编码
     * @return 协议内容
     */
    @GetMapping("/getByCode")
    @AuthSkip(reason = "协议文档公开访问")
    public ExceptionResult<SysAgreementVO> getByCode(@RequestParam("code") String code) {
        return ExceptionResult.success(agreementService.getByCode(code));
    }
}
