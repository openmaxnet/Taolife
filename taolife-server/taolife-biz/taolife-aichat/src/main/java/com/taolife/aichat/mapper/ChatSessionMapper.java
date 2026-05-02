package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 问答会话Mapper接口
 * 提供会话数据的数据库操作
 *
 * @author 文二
 * @date 2026-03-26
 */
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {

    /**
     * 根据账号ID查询会话列表
     * 查询指定用户的所有未删除会话，按创建时间倒序排列
     *
     * @param accountId 账号ID
     * @return 会话列表
     */
    default List<ChatSession> selectByAccountId(String accountId) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(ChatSession::getAccountId).eq(accountId)
                .and(ChatSession::getIsDeleted).eq(0)
                .orderBy(ChatSession::getCreateTime, false)
        );
    }

    /**
     * 根据ID查询会话
     * 查询指定ID的未删除会话
     *
     * @param id 会话ID
     * @return 会话信息，如果不存在返回null
     */
    default ChatSession selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(ChatSession::getId).eq(id)
                .and(ChatSession::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询会话（包含已删除的）
     * 查询指定ID的会话，不限制删除状态
     *
     * @param id 会话ID
     * @return 会话信息，如果不存在返回null
     */
    default ChatSession selectByIdIncludeDeleted(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(ChatSession::getId).eq(id)
                .limit(1)
        );
    }

    /**
     * 分页查询会话列表
     * 查询指定用户的未删除会话，只查询必要字段，按创建时间倒序排列
     *
     * @param accountId 账号ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    default Page<ChatSession> paginateByAccountId(String accountId, int page, int pageSize) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 使用字符串字段名避免泛型类型安全警告
        queryWrapper.select("id", "session_title", "create_time");
        queryWrapper.where(ChatSession::getAccountId).eq(accountId)
                  .and(ChatSession::getIsDeleted).eq(0)
                  .orderBy(ChatSession::getCreateTime, false);

        return paginate(page, pageSize, queryWrapper);
    }

    /**
     * 管理员分页查询会话列表（不限制账号）
     * 查询所有未删除会话，按创建时间倒序排列
     *
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    default Page<ChatSession> paginateForAdmin(int page, int pageSize) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.where(ChatSession::getIsDeleted).eq(0)
                  .orderBy(ChatSession::getCreateTime, false);

        return paginate(page, pageSize, queryWrapper);
    }

    /**
     * 管理员根据账号ID精确搜索会话列表
     *
     * @param accountId 账号ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    default Page<ChatSession> paginateByAccountIdForAdmin(String accountId, int page, int pageSize) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.where(ChatSession::getAccountId).eq(accountId)
                  .and(ChatSession::getIsDeleted).eq(0)
                  .orderBy(ChatSession::getCreateTime, false);

        return paginate(page, pageSize, queryWrapper);
    }

    /**
     * 管理员关键词搜索会话（标题或最后消息含关键词）
     *
     * @param keyword 关键词
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    default Page<ChatSession> searchByKeywordForAdmin(String keyword, int page, int pageSize) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.where(ChatSession::getIsDeleted).eq(0)
                  .and(ChatSession::getSessionTitle).like(keyword)
                  .or(ChatSession::getLastMessage).like(keyword)
                  .orderBy(ChatSession::getCreateTime, false);

        return paginate(page, pageSize, queryWrapper);
    }
}
