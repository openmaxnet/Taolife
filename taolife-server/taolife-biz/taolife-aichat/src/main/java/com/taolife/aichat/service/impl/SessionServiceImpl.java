package com.taolife.aichat.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.aichat.entity.ChatSession;
import com.taolife.aichat.mapper.ChatSessionMapper;
import com.taolife.aichat.param.CreateSessionParam;
import com.taolife.aichat.param.SessionPageParam;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aichat.service.ISessionService;
import com.taolife.aichat.vo.SessionVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话管理服务实现
 * 提供会话的创建、查询、删除等功能
 *
 * @author 文二
 * @date 2026-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements ISessionService {

    private final ChatSessionMapper chatSessionMapper;
    private final IAiConfigService aiConfigService;

    /**
     * 创建会话
     * 初始化新会话，设置用户ID、默认模型配置、会话标题等，并持久化到数据库
     *
     * @param accountId 用户账户ID
     * @param param 创建会话参数（含体质记录ID、聊天模型等）
     * @return 会话视图对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SessionVO createSession(String accountId, CreateSessionParam param) {
        // 创建会话实体
        ChatSession session = new ChatSession();
        session.setAccountId(accountId);
        session.setSessionTitle("新对话");
        session.setConstitutionId(param.getConstitutionId());

        // 使用配置的默认模型
        var chatConfig = aiConfigService.getChatConfig();
        var embeddingConfig = aiConfigService.getEmbeddingConfig();

        String chatModel = param.getChatModel() != null ? param.getChatModel() : chatConfig.getModel();
        String embeddingModel = param.getEmbeddingModel() != null ? param.getEmbeddingModel() : embeddingConfig.getModel();

        // 验证模型配置
        if (chatModel == null || chatModel.trim().isEmpty()) {
            log.error("默认聊天模型未配置");
            throw new BusinessException(ExceptionCode.CONFIG_ERROR, "配置异常");
        }
        if (embeddingModel == null || embeddingModel.trim().isEmpty()) {
            log.error("默认向量模型未配置");
            throw new BusinessException(ExceptionCode.CONFIG_ERROR, "配置异常");
        }

        session.setChatModel(chatModel);
        session.setEmbeddingModel(embeddingModel);
        session.setMessageCount(0);
        session.setStatus(1);
        session.setIsDeleted(0);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());

        // 保存会话到数据库
        chatSessionMapper.insert(session);

        // 转换为VO并返回
        return convertToSessionVO(session);
    }

    /**
     * 分页查询会话列表
     * 按创建时间倒序分页查询用户的会话列表，并转换为视图对象
     *
     * @param accountId 用户账户ID
     * @param param 分页参数
     * @return 分页的会话视图对象结果
     */
    @Override
    public PageResult<SessionVO> getSessionPage(String accountId, SessionPageParam param) {
        // 查询会话列表，只查询需要的字段，按创建时间倒序排列
        Page<ChatSession> page = chatSessionMapper.paginateByAccountId(
            accountId,
            param.getPageNo(),
            param.getPageSize()
        );

        // 使用PageResult的of方法转换
        PageResult<ChatSession> pageResult = PageResult.of(page);

        // 转换为VO列表
        List<SessionVO> list = pageResult.getList().stream()
            .map(this::convertToSessionVO)
            .collect(Collectors.toList());

        // 构造分页结果
        PageResult<SessionVO> result = new PageResult<>();
        result.setList(list);
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 删除会话
     * 验证会话归属后，逻辑删除指定会话（标记isDeleted为1）
     *
     * @param accountId 用户账户ID
     * @param sessionId 会话ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSession(String accountId, String sessionId) {
        // 验证会话是否存在
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "会话不存在");
        }

        // 验证会话所属用户
        if (!session.getAccountId().equals(accountId)) {
            throw new BusinessException(ExceptionCode.ACCESS_DENIED, "无权访问该会话");
        }

        // 逻辑删除会话
        session.setIsDeleted(1);
        chatSessionMapper.update(session);
    }

    /**
     * 更新会话信息
     * 更新会话的最后一条消息、消息计数、最后消息时间及状态。
     * 若是第一条消息，则自动生成会话标题
     *
     * @param sessionId 会话ID
     * @param lastMessage 最后一条消息内容
     * @param messageIncrement 消息数量的增量（可为负数）
     * @param isFirstMessage 是否为第一条消息
     * @param lastMessageStatus 最后一条消息的状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSession(String sessionId, String lastMessage, int messageIncrement, boolean isFirstMessage, Integer lastMessageStatus) {
        // 查询会话
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            log.error("会话不存在，会话ID: {}", sessionId);
            return;
        }

        // 更新会话信息
        session.setLastMessage(lastMessage);
        session.setLastMessageTime(LocalDateTime.now());
        session.setMessageCount(session.getMessageCount() + messageIncrement);
        if (lastMessageStatus != null) {
            session.setLastMessageStatus(lastMessageStatus);
        }

        // 如果是第一条消息，生成会话标题
        if (isFirstMessage) {
            String title = generateSessionTitle(lastMessage);
            session.setSessionTitle(title);
        }

        chatSessionMapper.update(session);
        log.info("会话信息更新成功");
    }

    /**
     * 根据会话ID获取会话
     *
     * @param sessionId 会话ID
     * @return 会话实体，不存在时返回null
     */
    @Override
    public ChatSession getSessionById(String sessionId) {
        return chatSessionMapper.selectById(sessionId);
    }

    /**
     * 验证会话访问权限
     * 检查会话是否存在以及当前用户是否有权访问该会话
     *
     * @param session 会话实体
     * @param accountId 用户账户ID
     * @throws BusinessException 当会话不存在或无权访问时抛出
     */
    @Override
    public void validateSessionAccess(ChatSession session, String accountId) {
        // 验证会话是否存在
        if (session == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "会话不存在");
        }

        // 验证会话所属用户
        if (!session.getAccountId().equals(accountId)) {
            throw new BusinessException(ExceptionCode.ACCESS_DENIED, "无权访问该会话");
        }
    }

    /**
     * 更新会话消息计数
     * 将会话实体的变更持久化到数据库
     *
     * @param session 待更新的会话实体（需包含已修改的字段）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSessionMessageCount(ChatSession session) {
        chatSessionMapper.update(session);
    }

    /**
     * 转换为会话VO
     * 将实体对象转换为视图对象
     *
     * @param session 会话实体
     * @return 会话VO
     */
    private SessionVO convertToSessionVO(ChatSession session) {
        SessionVO vo = new SessionVO();
        vo.setSessionId(session.getId());
        vo.setSessionTitle(session.getSessionTitle());
        vo.setCreateTime(session.getCreateTime());
        return vo;
    }

    /**
     * 生成会话标题
     * 根据第一条消息生成会话标题，截取前配置的最大长度字符
     *
     * @param firstMessage 第一条消息内容
     * @return 会话标题
     */
    private String generateSessionTitle(String firstMessage) {
        // 会话标题最大长度
        int maxLength = 20;

        // 如果消息长度不超过最大长度，直接使用
        if (firstMessage.length() <= maxLength) {
            return firstMessage;
        }
        // 否则截取前N个字符并添加省略号
        return firstMessage.substring(0, maxLength) + "...";
    }
}