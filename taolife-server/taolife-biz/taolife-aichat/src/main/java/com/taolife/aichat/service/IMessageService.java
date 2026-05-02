package com.taolife.aichat.service;

import com.taolife.aichat.entity.ChatMessage;
import com.taolife.aichat.param.RegenerateMessageParam;
import com.taolife.aichat.param.SendMessageParam;
import com.taolife.aichat.param.SessionPageParam;
import com.taolife.aichat.vo.MessageVO;
import com.taolife.common.utils.PageResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 消息管理服务接口
 * 提供消息的发送、查询、删除等功能
 *
 * @author 文二
 * @date 2026-03-26
 */
public interface IMessageService {

    /**
     * 发送消息
     * 处理用户消息并通过SSE流式返回AI回答
     *
     * @param accountId 账户ID
     * @param param 发送消息参数
     * @return SSE发射器
     */
    SseEmitter sendMessage(String accountId, SendMessageParam param);

    /**
     * 分页获取会话消息列表
     * 根据会话ID分页查询消息列表
     *
     * @param accountId 账户ID
     * @param sessionId 会话ID
     * @param param 分页参数
     * @return 分页结果
     */
    PageResult<MessageVO> getSessionDetail(String accountId, String sessionId, SessionPageParam param);

    /**
     * 保存用户消息
     * 保存用户发送的消息到数据库
     *
     * @param sessionId 会话ID
     * @param accountId 账户ID
     * @param content 消息内容
     * @return 消息实体
     */
    ChatMessage saveUserMessage(String sessionId, String accountId, String content);

    /**
     * 更新用户消息的安全状态
     * 更新用户的问题类型、是否安全、风险原因等信息
     *
     * @param userMessage 用户消息实体
     * @param questionType 问题类型
     * @param isSafe 是否安全
     * @param riskReason 风险原因
     */
    void updateUserMessageSafety(ChatMessage userMessage, Integer questionType, boolean isSafe, String riskReason);

    /**
     * 保存AI回答消息（带ID）
     * 保存AI生成的回答到数据库（使用指定ID）
     *
     * @param messageId 消息ID
     * @param sessionId 会话ID
     * @param accountId 账户ID
     * @param content 回答内容
     * @param reasoningContent 思考过程内容（可为null）
     * @param questionType 问题类型
     * @param modelUsed 使用的模型
     * @param tokensUsed 消耗的Token数
     * @param responseTime 响应时间
     * @param status 消息状态
     * @return 消息实体
     */
    ChatMessage saveAiMessageWithId(String messageId, String sessionId, String accountId, String content,
                                    String reasoningContent, Integer questionType, String modelUsed, int tokensUsed, int responseTime, Integer status);

    /**
     * 重新生成AI回答
     * 软删除指定的AI回答，基于前序用户消息重新调用LLM生成
     *
     * @param accountId 账户ID
     * @param param 重新生成参数
     * @return SSE发射器
     */
    SseEmitter regenerateMessage(String accountId, RegenerateMessageParam param);

    /**
     * 删除消息
     * 软删除指定会话中的单条消息，并更新会话最后消息信息
     *
     * @param accountId 账户ID
     * @param sessionId 会话ID
     * @param messageId 消息ID
     */
    void deleteMessage(String accountId, String sessionId, String messageId);
}
