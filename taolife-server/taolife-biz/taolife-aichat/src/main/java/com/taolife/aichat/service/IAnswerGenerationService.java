package com.taolife.aichat.service;

import com.taolife.aichat.service.IContextBuilderService.PromptContext;

/**
 * 答案生成服务接口
 * 基于检索结果和上下文生成AI回答
 *
 * @author 文二
 * @date 2026-03-26
 */
public interface IAnswerGenerationService {

    /**
     * 流式生成回答
     *
     * @param sessionId 会话ID
     * @param query 用户问题
     * @param context 上下文信息
     * @param enableThinking 是否启用思考模式
     * @param streamCallback 流式回调
     */
    void generateAnswerStream(String sessionId, String query, PromptContext context, boolean enableThinking, StreamCallback streamCallback);

    /**
     * 流式回调接口
     */
    interface StreamCallback {
        /**
         * 接收到回答内容片段
         *
         * @param content 内容片段
         * @param done 是否完成
         */
        void onChunk(String content, boolean done);

        /**
         * 接收到思考内容片段
         *
         * @param content 思考内容片段
         * @param done 思考是否完成
         */
        default void onThinkingChunk(String content, boolean done) {}
    }
}