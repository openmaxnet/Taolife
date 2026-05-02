package com.taolife.aichat.service;

/**
 * 流式消息缓存服务
 * 用于在流式输出过程中缓存消息内容，避免频繁操作数据库
 *
 * @author 文二
 * @date 2026-03-27
 */
public interface IStreamCacheService {

    /**
     * 缓存流式消息
     * 在流式输出开始时调用，初始化缓存
     *
     * @param sessionId 会话ID
     * @param accountId 账号ID
     * @param messageId 消息ID
     * @param initialContent 初始内容（通常为空字符串）
     */
    void cacheStreamMessage(String sessionId, String accountId, String messageId, String initialContent);

    /**
     * 追加流式内容
     * 在流式输出过程中调用，追加新的内容片段
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     * @param chunk 内容片段
     * @return 追加后的完整内容
     */
    String appendStreamContent(String sessionId, String messageId, String chunk);

    /**
     * 获取流式消息内容
     * 获取当前已缓存的消息内容
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     * @return 消息内容，如果不存在返回null
     */
    String getStreamContent(String sessionId, String messageId);

    /**
     * 删除流式消息缓存
     * 在消息成功落库后调用，清理缓存
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     */
    void removeStreamCache(String sessionId, String messageId);

    /**
     * 追加思考内容到缓存
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     * @param chunk 思考内容片段
     * @return 当前完整的思考内容
     */
    String appendThinkingContent(String sessionId, String messageId, String chunk);

    /**
     * 获取缓存的思考内容
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     * @return 思考内容
     */
    String getThinkingContent(String sessionId, String messageId);

    /**
     * 移除思考内容缓存
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     */
    void removeThinkingCache(String sessionId, String messageId);
}
