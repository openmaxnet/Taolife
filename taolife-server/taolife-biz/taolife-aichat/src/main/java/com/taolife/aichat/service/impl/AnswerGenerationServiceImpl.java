package com.taolife.aichat.service.impl;

import com.taolife.aicore.client.AiClient;
import com.taolife.aicore.config.ChatConfigVO;
import com.taolife.aicore.model.ChatCompletionRequest;
import com.taolife.aicore.model.Message;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aichat.service.IAnswerGenerationService;
import com.taolife.aichat.service.IContextBuilderService.PromptContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 回答生成服务实现
 * 基于 AiClient 流式调用大模型生成回答，支持思考模式和普通模式
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerGenerationServiceImpl implements IAnswerGenerationService {

    private final AiClient aiClient;
    private final IAiConfigService aiConfigService;

    /**
     * 流式生成AI回答
     * 通过AiClient流式调用大模型，将结果分块通过回调返回
     *
     * @param sessionId      会话ID
     * @param query          用户问题
     * @param context        上下文
     * @param enableThinking 是否启用思考模式
     * @param streamCallback 流式回调
     */
    @Override
    public void generateAnswerStream(String sessionId, String query, PromptContext context,
                                     boolean enableThinking, StreamCallback streamCallback) {
        String systemPrompt = context.getFullPrompt();
        String userPrompt = "用户问题：" + query;

        ChatConfigVO config = aiConfigService.getChatConfig();

        List<Message> messages = new ArrayList<>();
        messages.add(Message.system(systemPrompt));
        messages.add(Message.user(userPrompt));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(config.getModel())
                .messages(messages)
                .temperature(config.getTemperature())
                .maxTokens(config.getMaxTokens())
                .enableThinking(enableThinking)
                .build();

        try {
            aiClient.chatCompletionsStream(request, config)
                    .subscribe(
                            chunk -> {
                                if (chunk == null || chunk.isEmpty()) {
                                    return;
                                }
                                switch (chunk.type()) {
                                    case THINKING -> streamCallback.onThinkingChunk(chunk.content(), false);
                                    case ANSWER -> streamCallback.onChunk(chunk.content(), false);
                                    case EMPTY -> {}
                                }
                            },
                            error -> {
                                log.error("流式生成回答失败", error);
                                streamCallback.onChunk("生成回答时出现错误。", true);
                            },
                            () -> {
                                streamCallback.onThinkingChunk("", true);
                                streamCallback.onChunk("", true);
                            }
                    );
        } catch (Exception e) {
            log.error("流式生成回答失败", e);
            streamCallback.onChunk("生成回答时出现错误。", true);
        }
    }
}
