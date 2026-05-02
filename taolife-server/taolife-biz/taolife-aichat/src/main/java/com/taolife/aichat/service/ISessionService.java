package com.taolife.aichat.service;

import com.taolife.aichat.entity.ChatSession;
import com.taolife.aichat.param.CreateSessionParam;
import com.taolife.aichat.param.SessionPageParam;
import com.taolife.aichat.vo.SessionVO;
import com.taolife.common.utils.PageResult;

/**
 * 会话管理服务接口
 * 提供会话的创建、查询、删除等功能
 *
 * @author 文二
 * @date 2026-03-26
 */
public interface ISessionService {

    /**
     * 创建会话
     * 创建一个新的聊天会话
     *
     * @param accountId 账户ID
     * @param param 创建会话参数
     * @return 会话VO
     */
    SessionVO createSession(String accountId, CreateSessionParam param);

    /**
     * 分页获取会话列表
     * 根据账户ID分页查询会话列表
     *
     * @param accountId 账户ID
     * @param param 分页参数
     * @return 分页结果
     */
    PageResult<SessionVO> getSessionPage(String accountId, SessionPageParam param);

    /**
     * 删除会话
     * 逻辑删除指定会话
     *
     * @param accountId 账户ID
     * @param sessionId 会话ID
     */
    void removeSession(String accountId, String sessionId);

    /**
     * 消息发送后更新会话信息
     * 更新会话的最后一条消息、消息数等信息
     *
     * @param sessionId 会话ID
     * @param lastMessage 最后一条消息
     * @param messageIncrement 消息增量
     * @param isFirstMessage 是否是第一条消息
     * @param lastMessageStatus 最后AI回复状态
     */
    void updateSession(String sessionId, String lastMessage, int messageIncrement, boolean isFirstMessage, Integer lastMessageStatus);

    /**
     * 根据ID获取会话
     *
     * @param sessionId 会话ID
     * @return 会话实体
     */
    ChatSession getSessionById(String sessionId);

    /**
     * 验证会话访问权限
     * 验证会话是否存在以及用户是否有权访问
     *
     * @param session 会话实体
     * @param accountId 账户ID
     */
    void validateSessionAccess(ChatSession session, String accountId);

    /**
     * 更新会话消息计数
     * 更新会话的消息数、最后一条消息等信息
     *
     * @param session 会话实体
     */
    void updateSessionMessageCount(ChatSession session);
}
