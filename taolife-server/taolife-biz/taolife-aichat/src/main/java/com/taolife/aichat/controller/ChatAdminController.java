package com.taolife.aichat.controller;

import com.taolife.aichat.param.RemoveSessionAdminParam;
import com.taolife.aichat.param.SessionDetailAdminParam;
import com.taolife.aichat.param.SessionPageAdminParam;
import com.taolife.aichat.service.IChatAdminService;
import com.taolife.aichat.vo.SessionAdminVO;
import com.taolife.aichat.vo.SessionDetailAdminVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员AI会话管理控制器
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/ai/admin/chat")
@RequiredArgsConstructor
public class ChatAdminController {

    private final IChatAdminService chatAdminService;

    /**
     * 分页查询会话列表
     *
     * @param param 分页查询参数
     * @return 会话分页结果
     */
    @GetMapping("/getSessionPage")
    public ExceptionResult<PageResult<SessionAdminVO>> getSessionPage(SessionPageAdminParam param) {
        return ExceptionResult.success(chatAdminService.getSessionPage(param));
    }

    /**
     * 获取会话详情
     *
     * @param param 详情查询参数
     * @return 会话详情
     */
    @GetMapping("/getSessionDetail")
    public ExceptionResult<SessionDetailAdminVO> getSessionDetail(SessionDetailAdminParam param) {
        return ExceptionResult.success(chatAdminService.getSessionDetail(param));
    }

    /**
     * 删除会话
     *
     * @param param 删除参数
     * @return 操作结果
     */
    @PostMapping("/removeSession")
    public ExceptionResult<Void> removeSession(RemoveSessionAdminParam param) {
        chatAdminService.removeSession(param);
        return ExceptionResult.success();
    }
}