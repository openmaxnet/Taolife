package com.taolife.aichat.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.aichat.entity.ChatMessage;
import com.taolife.aichat.entity.ChatSession;
import com.taolife.aichat.enums.MessageStatusEnum;
import com.taolife.aichat.enums.RoleEnum;
import com.taolife.aichat.mapper.ChatMessageMapper;
import com.taolife.aichat.param.RegenerateMessageParam;
import com.taolife.aichat.param.SendMessageParam;
import com.taolife.aichat.param.SessionPageParam;
import com.taolife.aichat.service.*;
import com.taolife.aichat.service.ISensitiveWordService;
import com.taolife.aichat.vo.MessageVO;
import com.taolife.common.security.UserContext;
import com.taolife.common.utils.UUIDKeyGeneratorUtil;
import com.taolife.common.utils.PageResult;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息管理服务实现
 * 提供消息的发送、查询等功能的具体实现
 *
 * @author 文二
 * @date 2026-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private final ChatMessageMapper chatMessageMapper;
    private final ISensitiveWordService sensitiveWordService;
    private final IQuestionClassificationService questionClassificationService;
    private final IContextBuilderService contextBuilderService;
    private final IAnswerGenerationService answerGenerationService;
    private final IStreamCacheService streamCacheService;
    private final ISessionService sessionService;
    private final UUIDKeyGeneratorUtil uuidKeyGeneratorUtil = new UUIDKeyGeneratorUtil();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 流式发送消息
     * 在虚拟线程中执行安全检测、问题分类、上下文构建和LLM流式生成，
     * 通过SSE将思考过程和回答内容实时推送给客户端。
     * 支持客户端断连后后台继续完成生成并落库
     *
     * @param accountId 用户账户ID
     * @param param 发送消息参数（会话ID、消息内容、思考模式等）
     * @return SSE发射器，用于推送流式响应
     */
    @Override
    public SseEmitter sendMessage(String accountId, SendMessageParam param) {
        long startTime = System.currentTimeMillis();
        log.info("开始处理流式发送消息请求:用户ID: {}, 会话ID: {}, 思考模式: {}",
                 accountId, param.getSessionId(), param.getEnableThinking());

        UserContext currentUserContext = UserContext.get();
        SecurityContext securityContext = SecurityContextHolder.getContext();

        SseEmitter emitter = new SseEmitter(300000L);

        // 追踪 SSE 客户端连接状态：断开后仍继续接收 LLM 流，只跳过 SSE 推送
        final boolean[] clientConnected = {true};
        emitter.onCompletion(() -> clientConnected[0] = false);
        emitter.onTimeout(() -> clientConnected[0] = false);
        emitter.onError(e -> clientConnected[0] = false);

        Thread.ofVirtual().start(() -> {
            UserContext.set(currentUserContext);
            SecurityContextHolder.setContext(securityContext);
            try {
                ChatSession session = sessionService.getSessionById(param.getSessionId());
                if (session == null) {
                    log.error("会话不存在，会话ID: {}", param.getSessionId());
                    sendError(emitter, "会话不存在");
                    return;
                }

                sessionService.validateSessionAccess(session, accountId);

                ChatMessage userMessage = saveUserMessage(param.getSessionId(), accountId, param.getContent());

                ISensitiveWordService.SensitiveWordResult sensitiveResult =
                    sensitiveWordService.detectWithContext(param.getContent(), null);

                IQuestionClassificationService.ClassificationResult classificationResult =
                    questionClassificationService.classify(param.getContent());

                userMessage.setQuestionType(classificationResult.getQuestionType());

                boolean isSafe = true;
                String riskReason = null;

                if (sensitiveResult.hasSensitiveWord()) {
                    isSafe = false;
                    riskReason = "包含敏感词：" + sensitiveResult.getMatchedWords().stream()
                        .map(ISensitiveWordService.MatchedWord::getWord)
                        .collect(Collectors.joining(","));
                }

                if (!classificationResult.isAllowed()) {
                    isSafe = false;
                    riskReason = classificationResult.getRejectReason();
                }

                updateUserMessageSafety(userMessage, classificationResult.getQuestionType(), isSafe, riskReason);

                if (!isSafe) {
                    // 安全拒绝：落库错误AI消息 + 更新会话
                    String aiMessageId = (String) uuidKeyGeneratorUtil.generate(null, "id");
                    saveAiMessageWithId(aiMessageId, param.getSessionId(), accountId,
                        riskReason, null, classificationResult.getQuestionType(),
                        session.getChatModel(), 0, 0, MessageStatusEnum.ERROR_SAFETY.getValue());
                    sessionService.updateSession(param.getSessionId(), param.getContent(), 2,
                        session.getMessageCount() <= 0, MessageStatusEnum.ERROR_SAFETY.getValue());
                    sendError(emitter, riskReason);
                    return;
                }

                IContextBuilderService.PromptContext context =
                    contextBuilderService.buildContext(param.getSessionId(), param.getContent());

                final long[] tokenCount = {0};
                final long[] responseTime = {0};

                String aiMessageId = (String) uuidKeyGeneratorUtil.generate(null, "id");

                streamCacheService.cacheStreamMessage(
                    param.getSessionId(), accountId, aiMessageId, "");

                boolean enableThinking = Boolean.TRUE.equals(param.getEnableThinking());

                // 标记 AI 消息是否已保存（防止客户端断开时重复保存）
                final boolean[] messageSaved = {false};

                answerGenerationService.generateAnswerStream(
                    param.getSessionId(),
                    param.getContent(),
                    context,
                    enableThinking,
                    new IAnswerGenerationService.StreamCallback() {
                        @Override
                        public void onThinkingChunk(String content, boolean done) {
                            if (done) {
                                return;
                            }
                            if (content != null && !content.isEmpty()) {
                                // 始终累积思考内容到缓存（无论客户端是否在线）
                                streamCacheService.appendThinkingContent(
                                    param.getSessionId(), aiMessageId, content);

                                // 仅在客户端在线时推送 SSE
                                if (clientConnected[0]) {
                                    try {
                                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                            .name("thinking")
                                            .data(objectMapper.writeValueAsString(Map.of("content", content, "done", false)));
                                        emitter.send(event);
                                    } catch (IOException e) {
                                        clientConnected[0] = false;
                                        log.info("思考内容推送时客户端已断开，LLM 流继续在后台运行");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onChunk(String content, boolean done) {
                            if (done) {
                                // LLM 流结束 — 始终保存完整响应（无论客户端是否在线）
                                try {
                                    responseTime[0] = System.currentTimeMillis() - startTime;

                                    String fullContent = streamCacheService.getStreamContent(
                                        param.getSessionId(), aiMessageId);

                                    if (fullContent == null || fullContent.isEmpty()) {
                                        log.error("从缓存获取内容失败（LLM流异常），sessionId: {}, messageId: {}",
                                                 param.getSessionId(), aiMessageId);
                                        // 清理缓存
                                        streamCacheService.removeStreamCache(param.getSessionId(), aiMessageId);
                                        streamCacheService.removeThinkingCache(param.getSessionId(), aiMessageId);
                                        // 落库错误AI消息
                                        saveAiMessageWithId(aiMessageId, param.getSessionId(), accountId,
                                            "AI回答生成失败，请稍后重试", null,
                                            classificationResult.getQuestionType(), session.getChatModel(),
                                            0, (int) responseTime[0], MessageStatusEnum.ERROR_LLM.getValue());
                                        boolean isFirstMessage = session.getMessageCount() <= 0;
                                        sessionService.updateSession(param.getSessionId(), param.getContent(), 2,
                                            isFirstMessage, MessageStatusEnum.ERROR_LLM.getValue());
                                        // 通知前端生成失败
                                        if (clientConnected[0]) {
                                            sendError(emitter, "AI回答生成失败，请稍后重试");
                                        } else {
                                            try { emitter.complete(); } catch (Exception ignored) {}
                                        }
                                        return;
                                    }

                                    String reasoningContent = null;
                                    if (enableThinking) {
                                        reasoningContent = streamCacheService.getThinkingContent(
                                            param.getSessionId(), aiMessageId);
                                    }

                                    saveAiMessageWithId(
                                        aiMessageId,
                                        param.getSessionId(),
                                        accountId,
                                        fullContent,
                                        reasoningContent,
                                        classificationResult.getQuestionType(),
                                        session.getChatModel(),
                                        (int) tokenCount[0],
                                        (int) responseTime[0],
                                        MessageStatusEnum.COMPLETE.getValue()
                                    );
                                    messageSaved[0] = true;

                                    // 清理缓存
                                    streamCacheService.removeStreamCache(param.getSessionId(), aiMessageId);
                                    streamCacheService.removeThinkingCache(param.getSessionId(), aiMessageId);

                                    boolean isFirstMessage = session.getMessageCount() <= 0;
                                    sessionService.updateSession(param.getSessionId(), param.getContent(), 2, isFirstMessage, MessageStatusEnum.COMPLETE.getValue());

                                    log.info("流式输出完成（客户端{}），总耗时: {}ms, 内容长度: {}, 思考内容长度: {}",
                                             clientConnected[0] ? "在线" : "已断开",
                                             responseTime[0], fullContent.length(),
                                             reasoningContent != null ? reasoningContent.length() : 0);

                                    // 尝试推送 done 事件
                                    if (clientConnected[0]) {
                                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                            .name("done")
                                            .data(objectMapper.writeValueAsString(Map.of("content", "", "done", true)));
                                        emitter.send(event);
                                        emitter.complete();
                                    } else {
                                        try { emitter.complete(); } catch (Exception ignored) {}
                                    }
                                } catch (Exception e) {
                                    log.error("保存AI消息失败", e);
                                    try { emitter.completeWithError(e); } catch (Exception ignored) {}
                                } finally {
                                    UserContext.clear();
                                }
                            } else {
                                // 始终累积内容到缓存（无论客户端是否在线）
                                streamCacheService.appendStreamContent(
                                    param.getSessionId(), aiMessageId, content);
                                tokenCount[0]++;

                                // 仅在客户端在线时推送 SSE
                                if (clientConnected[0]) {
                                    try {
                                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                            .name("message")
                                            .data(objectMapper.writeValueAsString(Map.of("content", content, "done", false)));
                                        emitter.send(event);
                                    } catch (IOException e) {
                                        clientConnected[0] = false;
                                        log.info("内容推送时客户端已断开，LLM 流继续在后台运行");
                                    }
                                }
                            }
                        }
                    }
                );

            } catch (Exception e) {
                log.error("流式发送消息失败", e);
                try {
                    sendError(emitter, "生成回答时出现错误：" + e.getMessage());
                } catch (IOException ioException) {
                    log.error("发送错误消息失败", ioException);
                    emitter.completeWithError(ioException);
                }
            }
        });

        return emitter;
    }

    /**
     * 重新生成AI回答
     * 软删除原有AI消息后，在虚拟线程中重新执行安全检测、上下文构建和LLM流式生成，
     * 通过SSE推送新的回答内容。支持客户端断连后后台继续完成
     *
     * @param accountId 用户账户ID
     * @param param 重新生成参数（会话ID、消息ID、思考模式等）
     * @return SSE发射器，用于推送流式响应
     */
    @Override
    public SseEmitter regenerateMessage(String accountId, RegenerateMessageParam param) {
        long startTime = System.currentTimeMillis();
        log.info("开始处理重新生成请求: 用户ID: {}, 会话ID: {}, 消息ID: {}, 思考模式: {}",
                 accountId, param.getSessionId(), param.getMessageId(), param.getEnableThinking());

        UserContext currentUserContext = UserContext.get();
        SecurityContext securityContext = SecurityContextHolder.getContext();

        SseEmitter emitter = new SseEmitter(300000L);

        final boolean[] clientConnected = {true};
        emitter.onCompletion(() -> clientConnected[0] = false);
        emitter.onTimeout(() -> clientConnected[0] = false);
        emitter.onError(e -> clientConnected[0] = false);

        Thread.ofVirtual().start(() -> {
            UserContext.set(currentUserContext);
            SecurityContextHolder.setContext(securityContext);
            try {
                // 阶段1: 验证会话
                ChatSession session = sessionService.getSessionById(param.getSessionId());
                if (session == null) {
                    log.error("会话不存在，会话ID: {}", param.getSessionId());
                    sendError(emitter, "会话不存在");
                    return;
                }

                sessionService.validateSessionAccess(session, accountId);

                // 阶段2: 获取并验证 AI 消息
                ChatMessage aiMessage = chatMessageMapper.selectOneById(param.getMessageId());
                if (aiMessage == null) {
                    sendError(emitter, "消息不存在");
                    return;
                }
                if (!aiMessage.getSessionId().equals(param.getSessionId())) {
                    sendError(emitter, "消息不属于该会话");
                    return;
                }
                if (!RoleEnum.ASSISTANT.getValue().equals(aiMessage.getRole())) {
                    sendError(emitter, "只能重新生成AI回答");
                    return;
                }

                // 阶段3: 获取前序用户消息（软删除前查询，保证列表完整）
                List<ChatMessage> messages = chatMessageMapper.selectBySessionId(param.getSessionId());
                String userContent = null;
                ChatMessage userMessage = null;

                for (int i = 0; i < messages.size(); i++) {
                    if (messages.get(i).getId().equals(param.getMessageId())) {
                        // 找到 AI 消息位置，向前找最近的用户消息
                        for (int j = i - 1; j >= 0; j--) {
                            if (RoleEnum.USER.getValue().equals(messages.get(j).getRole())) {
                                userMessage = messages.get(j);
                                userContent = userMessage.getContent();
                                break;
                            }
                        }
                        break;
                    }
                }

                if (userContent == null || userContent.isEmpty()) {
                    sendError(emitter, "找不到原始问题");
                    return;
                }

                // 阶段4: 检查用户消息安全性（快速拒绝）
                if (userMessage != null && Integer.valueOf(0).equals(userMessage.getIsSafe())) {
                    log.warn("重新生成被拒绝: 用户消息不安全，消息ID: {}", userMessage.getId());
                    // 落库错误AI消息（软删除旧的，保存新的错误消息）
                    chatMessageMapper.deleteById(param.getMessageId());
                    String errAiMsgId = (String) uuidKeyGeneratorUtil.generate(null, "id");
                    saveAiMessageWithId(errAiMsgId, param.getSessionId(), accountId,
                        "原始问题不合规，无法重新生成", null,
                        userMessage.getQuestionType(), session.getChatModel(),
                        0, 0, MessageStatusEnum.ERROR_SAFETY.getValue());
                    sessionService.updateSession(param.getSessionId(), "原始问题不合规", 0,
                        false, MessageStatusEnum.ERROR_SAFETY.getValue());
                    sendError(emitter, "原始问题不合规，无法重新生成");
                    return;
                }

                // 阶段5: 重新运行安全检测（防御性检查，规则可能已变更）
                ISensitiveWordService.SensitiveWordResult sensitiveResult =
                    sensitiveWordService.detectWithContext(userContent, null);

                IQuestionClassificationService.ClassificationResult classificationResult =
                    questionClassificationService.classify(userContent);

                boolean isSafe = true;
                String riskReason = null;

                if (sensitiveResult.hasSensitiveWord()) {
                    isSafe = false;
                    riskReason = "包含敏感词：" + sensitiveResult.getMatchedWords().stream()
                        .map(ISensitiveWordService.MatchedWord::getWord)
                        .collect(Collectors.joining(","));
                }

                if (!classificationResult.isAllowed()) {
                    isSafe = false;
                    riskReason = classificationResult.getRejectReason();
                }

                if (!isSafe) {
                    log.warn("重新生成被安全检测拒绝: {}", riskReason);
                    // 落库错误AI消息
                    chatMessageMapper.deleteById(param.getMessageId());
                    String errAiMsgId = (String) uuidKeyGeneratorUtil.generate(null, "id");
                    saveAiMessageWithId(errAiMsgId, param.getSessionId(), accountId,
                        riskReason, null, classificationResult.getQuestionType(),
                        session.getChatModel(), 0, 0, MessageStatusEnum.ERROR_SAFETY.getValue());
                    sessionService.updateSession(param.getSessionId(), riskReason, 0,
                        false, MessageStatusEnum.ERROR_SAFETY.getValue());
                    sendError(emitter, riskReason);
                    return;
                }

                // 阶段6: 软删除旧 AI 消息
                chatMessageMapper.deleteById(param.getMessageId());
                log.info("已软删除旧AI消息: {}", param.getMessageId());

                // 阶段7: 构建 LLM 上下文（自动排除已删除消息）
                IContextBuilderService.PromptContext context =
                    contextBuilderService.buildContext(param.getSessionId(), userContent);

                final long[] tokenCount = {0};
                final long[] responseTime = {0};

                String newAiMessageId = (String) uuidKeyGeneratorUtil.generate(null, "id");

                streamCacheService.cacheStreamMessage(
                    param.getSessionId(), accountId, newAiMessageId, "");

                boolean enableThinking = Boolean.TRUE.equals(param.getEnableThinking());

                // 阶段8: 生成流式回答
                answerGenerationService.generateAnswerStream(
                    param.getSessionId(),
                    userContent,
                    context,
                    enableThinking,
                    new IAnswerGenerationService.StreamCallback() {
                        @Override
                        public void onThinkingChunk(String content, boolean done) {
                            if (done) return;
                            if (content != null && !content.isEmpty()) {
                                streamCacheService.appendThinkingContent(
                                    param.getSessionId(), newAiMessageId, content);

                                if (clientConnected[0]) {
                                    try {
                                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                            .name("thinking")
                                            .data(objectMapper.writeValueAsString(Map.of("content", content, "done", false)));
                                        emitter.send(event);
                                    } catch (IOException e) {
                                        clientConnected[0] = false;
                                        log.info("思考内容推送时客户端已断开，LLM 流继续在后台运行");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onChunk(String content, boolean done) {
                            if (done) {
                                try {
                                    responseTime[0] = System.currentTimeMillis() - startTime;

                                    String fullContent = streamCacheService.getStreamContent(
                                        param.getSessionId(), newAiMessageId);

                                    if (fullContent == null || fullContent.isEmpty()) {
                                        log.error("从缓存获取内容失败（LLM流异常），sessionId: {}, messageId: {}",
                                                 param.getSessionId(), newAiMessageId);
                                        streamCacheService.removeStreamCache(param.getSessionId(), newAiMessageId);
                                        streamCacheService.removeThinkingCache(param.getSessionId(), newAiMessageId);
                                        // 落库错误AI消息
                                        saveAiMessageWithId(newAiMessageId, param.getSessionId(), accountId,
                                            "AI回答生成失败，请稍后重试", null,
                                            classificationResult.getQuestionType(), session.getChatModel(),
                                            0, (int) responseTime[0], MessageStatusEnum.ERROR_LLM.getValue());
                                        sessionService.updateSession(param.getSessionId(), "AI回答生成失败", 0,
                                            false, MessageStatusEnum.ERROR_LLM.getValue());
                                        if (clientConnected[0]) {
                                            sendError(emitter, "AI回答生成失败，请稍后重试");
                                        } else {
                                            try { emitter.complete(); } catch (Exception ignored) {}
                                        }
                                        return;
                                    }

                                    String reasoningContent = null;
                                    if (enableThinking) {
                                        reasoningContent = streamCacheService.getThinkingContent(
                                            param.getSessionId(), newAiMessageId);
                                    }

                                    saveAiMessageWithId(
                                        newAiMessageId,
                                        param.getSessionId(),
                                        accountId,
                                        fullContent,
                                        reasoningContent,
                                        classificationResult.getQuestionType(),
                                        session.getChatModel(),
                                        (int) tokenCount[0],
                                        (int) responseTime[0],
                                        MessageStatusEnum.COMPLETE.getValue()
                                    );

                                    streamCacheService.removeStreamCache(param.getSessionId(), newAiMessageId);
                                    streamCacheService.removeThinkingCache(param.getSessionId(), newAiMessageId);

                                    // increment=0: -1(删除旧AI) + 1(新增AI) = 0
                                    sessionService.updateSession(param.getSessionId(), fullContent, 0, false, MessageStatusEnum.COMPLETE.getValue());

                                    log.info("重新生成完成（客户端{}），总耗时: {}ms, 内容长度: {}",
                                             clientConnected[0] ? "在线" : "已断开",
                                             responseTime[0], fullContent.length());

                                    if (clientConnected[0]) {
                                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                            .name("done")
                                            .data(objectMapper.writeValueAsString(Map.of("content", "", "done", true)));
                                        emitter.send(event);
                                        emitter.complete();
                                    } else {
                                        try { emitter.complete(); } catch (Exception ignored) {}
                                    }
                                } catch (Exception e) {
                                    log.error("保存重新生成的AI消息失败", e);
                                    try { emitter.completeWithError(e); } catch (Exception ignored) {}
                                } finally {
                                    UserContext.clear();
                                }
                            } else {
                                streamCacheService.appendStreamContent(
                                    param.getSessionId(), newAiMessageId, content);
                                tokenCount[0]++;

                                if (clientConnected[0]) {
                                    try {
                                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                            .name("message")
                                            .data(objectMapper.writeValueAsString(Map.of("content", content, "done", false)));
                                        emitter.send(event);
                                    } catch (IOException e) {
                                        clientConnected[0] = false;
                                        log.info("内容推送时客户端已断开，LLM 流继续在后台运行");
                                    }
                                }
                            }
                        }
                    }
                );

            } catch (Exception e) {
                log.error("重新生成消息失败", e);
                try {
                    sendError(emitter, "重新生成回答时出现错误：" + e.getMessage());
                } catch (IOException ioException) {
                    log.error("发送错误消息失败", ioException);
                    emitter.completeWithError(ioException);
                }
            }
        });

        return emitter;
    }

    /**
     * 删除消息
     * 验证会话和消息归属后，软删除指定消息，并更新会话的最后一条消息记录
     *
     * @param accountId 用户账户ID
     * @param sessionId 会话ID
     * @param messageId 消息ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(String accountId, String sessionId, String messageId) {
        log.info("删除消息请求: 用户ID: {}, 会话ID: {}, 消息ID: {}", accountId, sessionId, messageId);

        // 验证会话
        ChatSession session = sessionService.getSessionById(sessionId);
        if (session == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "会话不存在");
        }
        sessionService.validateSessionAccess(session, accountId);

        // 验证消息
        ChatMessage message = chatMessageMapper.selectOneById(messageId);
        if (message == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "消息不存在或已删除");
        }
        if (!message.getSessionId().equals(sessionId)) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "消息不属于该会话");
        }

        // 软删除消息（deleteById 配合 @Column(isLogicDelete=true) 自动软删除）
        chatMessageMapper.deleteById(messageId);
        log.info("已软删除消息: {}", messageId);

        // 查询剩余最后一条消息，更新会话
        List<ChatMessage> remaining = chatMessageMapper.selectRecentBySessionId(sessionId, 1);
        if (remaining.isEmpty()) {
            // 无剩余消息，lastMessage 设为空，messageCount -1
            sessionService.updateSession(sessionId, "", -1, false, null);
        } else {
            ChatMessage lastMsg = remaining.get(0);
            String lastContent = lastMsg.getContent();
            // 截断过长内容
            if (lastContent != null && lastContent.length() > 50) {
                lastContent = lastContent.substring(0, 50) + "...";
            }
            Integer lastStatus = lastMsg.getStatus() != null ? lastMsg.getStatus() : MessageStatusEnum.COMPLETE.getValue();
            sessionService.updateSession(sessionId, lastContent, -1, false, lastStatus);
        }
    }

    /**
     * 获取会话详情（消息列表）
     * 验证会话归属后，按创建时间正序分页查询消息列表，转换为视图对象返回
     *
     * @param accountId 用户账户ID
     * @param sessionId 会话ID
     * @param param 分页参数
     * @return 分页消息结果
     */
    @Override
    public PageResult<MessageVO> getSessionDetail(String accountId, String sessionId, SessionPageParam param) {
        // 验证会话是否存在
        ChatSession session = sessionService.getSessionById(sessionId);
        if (session == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "会话不存在");
        }

        // 验证会话所属用户
        sessionService.validateSessionAccess(session, accountId);

        // 查询消息列表，按创建时间正序排列
        Page<ChatMessage> page = chatMessageMapper.paginateBySessionId(
            sessionId,
            param.getPageNo(),
            param.getPageSize()
        );

        // 使用PageResult的of方法转换
        PageResult<ChatMessage> pageResult = PageResult.of(page);

        // 转换为VO列表
        List<MessageVO> list = pageResult.getList().stream()
            .map(this::convertToMessageVO)
            .collect(Collectors.toList());

        // 构造分页结果
        PageResult<MessageVO> result = new PageResult<>();
        result.setList(list);
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 保存用户消息
     * 创建用户消息实体并持久化到数据库
     *
     * @param sessionId 会话ID
     * @param accountId 用户账户ID
     * @param content 消息内容
     * @return 保存后的用户消息实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatMessage saveUserMessage(String sessionId, String accountId, String content) {
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setAccountId(accountId);
        userMessage.setRole(RoleEnum.USER.getValue());
        userMessage.setContent(content);
        userMessage.setIsDeleted(0);
        userMessage.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(userMessage);
        return userMessage;
    }

    /**
     * 更新用户消息安全状态
     * 设置消息的问题类型、安全标记和风险原因，并持久化到数据库
     *
     * @param userMessage 用户消息实体
     * @param questionType 问题类型
     * @param isSafe 是否安全
     * @param riskReason 风险原因（不安全时填写）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserMessageSafety(ChatMessage userMessage, Integer questionType, boolean isSafe, String riskReason) {
        userMessage.setQuestionType(questionType);
        userMessage.setIsSafe(isSafe ? 1 : 0);
        userMessage.setRiskReason(riskReason);
        chatMessageMapper.update(userMessage);
    }

    /**
     * 保存AI消息（指定ID）
     * 创建AI助手的回复消息实体，包含完整内容、思考内容、模型信息、耗时等，持久化到数据库
     *
     * @param messageId 消息ID（预生成）
     * @param sessionId 会话ID
     * @param accountId 用户账户ID
     * @param content AI回复内容
     * @param reasoningContent 思考链内容（可能为null）
     * @param questionType 问题类型
     * @param modelUsed 使用的模型名称
     * @param tokensUsed 消耗的Token数量
     * @param responseTime 响应耗时（毫秒）
     * @param status 消息状态
     * @return 保存后的AI消息实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatMessage saveAiMessageWithId(String messageId, String sessionId, String accountId, String content,
                                            String reasoningContent, Integer questionType, String modelUsed, int tokensUsed, int responseTime, Integer status) {
        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setId(messageId);
        aiMessage.setSessionId(sessionId);
        aiMessage.setAccountId(accountId);
        aiMessage.setRole(RoleEnum.ASSISTANT.getValue());
        aiMessage.setContent(content);
        aiMessage.setReasoningContent(reasoningContent);
        aiMessage.setQuestionType(questionType);
        aiMessage.setIsSafe(1);
        aiMessage.setModelUsed(modelUsed);
        aiMessage.setTokensUsed(tokensUsed);
        aiMessage.setResponseTime(responseTime);
        aiMessage.setStatus(status);
        aiMessage.setIsDeleted(0);
        aiMessage.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(aiMessage);
        return aiMessage;
    }

    /**
     * 发送错误消息
     *
     * @param emitter SSE发射器
     * @param errorMessage 错误消息
     */
    private void sendError(SseEmitter emitter, String errorMessage) throws IOException {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
            .name("error")
            .data(objectMapper.writeValueAsString(Map.of("error", errorMessage)));
        emitter.send(event);
        emitter.complete();
    }

    /**
     * 转换为消息VO
     * 将实体对象转换为视图对象
     *
     * @param message 消息实体
     * @return 消息VO
     */
    private MessageVO convertToMessageVO(ChatMessage message) {
        MessageVO vo = new MessageVO();
        vo.setMessageId(message.getId());
        vo.setSessionId(message.getSessionId());
        vo.setContent(message.getContent());
        vo.setReasoningContent(message.getReasoningContent());
        if (message.getCreateTime() != null) {
            vo.setCreateTime(message.getCreateTime().toString());
        }
        vo.setRole(RoleEnum.getByValue(message.getRole()).getCode());
        vo.setStatus(message.getStatus() != null ? message.getStatus() : MessageStatusEnum.COMPLETE.getValue());
        return vo;
    }
}
