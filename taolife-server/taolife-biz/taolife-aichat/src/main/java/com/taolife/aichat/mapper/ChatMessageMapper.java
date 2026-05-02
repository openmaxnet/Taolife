package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 问答消息Mapper接口
 * 提供消息数据的数据库操作
 *
 * @author 文二
 * @date 2026-03-26
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    /**
     * 根据会话ID查询消息列表
     * 查询指定会话的所有未删除消息，按创建时间正序排列
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    default List<ChatMessage> selectBySessionId(String sessionId) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(ChatMessage::getSessionId).eq(sessionId)
                .and(ChatMessage::getIsDeleted).eq(0)
                .orderBy(ChatMessage::getCreateTime, true)
        );
    }

    /**
     * 根据会话ID查询最近的消息
     * 查询指定会话的最近N条未删除消息，按创建时间倒序排列
     *
     * @param sessionId 会话ID
     * @param limit 数量限制
     * @return 消息列表
     */
    default List<ChatMessage> selectRecentBySessionId(String sessionId, int limit) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(ChatMessage::getSessionId).eq(sessionId)
                .and(ChatMessage::getIsDeleted).eq(0)
                .orderBy(ChatMessage::getCreateTime, false)
                .limit(limit)
        );
    }

    /**
     * 根据账号ID查询消息列表
     * 查询指定用户的所有未删除消息，按创建时间倒序排列
     *
     * @param accountId 账号ID
     * @return 消息列表
     */
    default List<ChatMessage> selectByAccountId(String accountId) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(ChatMessage::getAccountId).eq(accountId)
                .and(ChatMessage::getIsDeleted).eq(0)
                .orderBy(ChatMessage::getCreateTime, false)
        );
    }

    /**
     * 分页查询消息列表
     * 查询指定会话的未删除消息，按创建时间正序排列
     * 只查询需要的字段：id, sessionId, role, content, createTime
     *
     * @param sessionId 会话ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    default Page<ChatMessage> paginateBySessionId(String sessionId, int page, int pageSize) {
        return paginate(
            page,
            pageSize,
            QueryWrapper.create()
                .select("id", "session_id", "role", "content", "reasoning_content", "create_time", "status")
                .where(ChatMessage::getSessionId).eq(sessionId)
                .and(ChatMessage::getIsDeleted).eq(0)
                .orderBy(ChatMessage::getCreateTime, true)
        );
    }
}
