package com.taolife.aichat.controller;

import com.taolife.aichat.param.CreateSessionParam;
import com.taolife.aichat.param.DeleteMessageParam;
import com.taolife.aichat.param.RegenerateMessageParam;
import com.taolife.aichat.param.RemoveSessionParam;
import com.taolife.aichat.param.SendMessageParam;
import com.taolife.aichat.param.SessionPageParam;
import com.taolife.aichat.service.IMessageService;
import com.taolife.aichat.service.ISessionService;
import com.taolife.aichat.vo.MessageVO;
import com.taolife.aichat.vo.SessionVO;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.service.IQuotaService;
import com.taolife.fee.vo.QuotaStatusVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * AI问答控制器
 * 负责处理AI智能问答相关的HTTP请求
 *
 * @author 文二
 * @date 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ISessionService sessionService;
    private final IMessageService messageService;
    private final IQuotaService quotaService;

    /**
     * 创建会话
     * 创建新的AI问答会话，支持指定体质记录和模型配置
     *
     * @param param 创建会话参数，包含体质ID、聊天模型、向量模型等信息
     * @return 会话信息，包含会话ID、标题、创建时间等
     */
    @PostMapping("/createSession")
    public ExceptionResult<SessionVO> createSession(@Valid @RequestBody CreateSessionParam param) {
        String accountId = UserContext.getAccountId();
        SessionVO session = sessionService.createSession(accountId, param);
        return ExceptionResult.success(session);
    }

    /**
     * 流式发送消息
     * 发送用户问题，通过SSE流式返回AI回答
     *
     * @param param 发送消息参数，包含会话ID、消息内容等信息
     * @param response HTTP响应对象
     * @return SSE流式响应
     */
    @PostMapping(value = "/sendMessage", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessage(@Valid @RequestBody SendMessageParam param,
                               HttpServletResponse response) {
        // 设置字符编码为UTF-8，解决中文乱码问题
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/event-stream; charset=UTF-8");
        
        String accountId = UserContext.getAccountId();
        quotaService.checkAndDecrementAiQuota(accountId);
        return messageService.sendMessage(accountId, param);
    }

    /**
     * 重新生成AI回答
     * 软删除指定的AI回答，基于前序用户消息重新调用LLM生成
     *
     * @param param 重新生成参数，包含会话ID和要重新生成的消息ID
     * @param response HTTP响应对象
     * @return SSE流式响应
     */
    @PostMapping(value = "/regenerateMessage", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter regenerateMessage(@Valid @RequestBody RegenerateMessageParam param,
                                        HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/event-stream; charset=UTF-8");

        String accountId = UserContext.getAccountId();
        quotaService.checkAndDecrementAiQuota(accountId);
        return messageService.regenerateMessage(accountId, param);
    }

    /**
     * 获取会话列表
     * 分页获取用户的会话列表，按创建时间倒序排列
     *
     * @param param 查询参数，包含页码和每页数量
     * @return 分页会话列表，包含会话基本信息和最后一条消息
     */
    @GetMapping("/getSessionPage")
    public ExceptionResult<PageResult<SessionVO>> getSessionPage(SessionPageParam param) {
        String accountId = UserContext.getAccountId();
        var result = sessionService.getSessionPage(accountId, param);
        return ExceptionResult.success(result);
    }

    /**
     * 获取会话详情
     * 获取指定会话的消息列表，支持分页查询
     *
     * @param param 查询参数，包含会话ID、页码和每页数量
     * @return 分页消息列表，包含用户问题和AI回答
     */
    @GetMapping("/getSessionDetail")
    public ExceptionResult<PageResult<MessageVO>> getSessionDetail(@Valid @ModelAttribute SessionPageParam param) {
        String accountId = UserContext.getAccountId();
        var result = messageService.getSessionDetail(accountId, param.getSessionId(), param);
        return ExceptionResult.success(result);
    }

    /**
     * 删除会话
     * 逻辑删除指定的会话及其关联的所有消息
     *
     * @param param 删除会话参数，包含会话ID
     * @return 操作结果
     */
    @PostMapping("/removeSession")
    public ExceptionResult<Void> removeSession(@Valid @ModelAttribute RemoveSessionParam param) {
        String accountId = UserContext.getAccountId();
        sessionService.removeSession(accountId, param.getSessionId());
        return ExceptionResult.success();
    }

    /**
     * 删除消息
     * 逻辑删除指定会话中的单条消息
     *
     * @param param 删除消息参数，包含会话ID和消息ID
     * @return 操作结果
     */
    @PostMapping("/deleteMessage")
    public ExceptionResult<Void> deleteMessage(@Valid @RequestBody DeleteMessageParam param) {
        messageService.deleteMessage(UserContext.getAccountId(), param.getSessionId(), param.getMessageId());
        return ExceptionResult.success();
    }

    /**
     * 获取AI问答配额状态
     */
    @GetMapping("/getQuotaStatus")
    public ExceptionResult<QuotaStatusVO> getQuotaStatus() {
        String accountId = UserContext.getAccountId();
        QuotaStatusVO result = quotaService.getAiQuotaStatus(accountId);
        return ExceptionResult.success(result);
    }

}
