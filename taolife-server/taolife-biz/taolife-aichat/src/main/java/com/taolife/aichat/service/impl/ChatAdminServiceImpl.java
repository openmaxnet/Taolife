package com.taolife.aichat.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.aichat.entity.ChatMessage;
import com.taolife.aichat.entity.ChatSession;
import com.taolife.wisdom.entity.FitnessRecord;
import com.taolife.aichat.mapper.ChatMessageMapper;
import com.taolife.aichat.mapper.ChatSessionMapper;
import com.taolife.wisdom.mapper.FitnessRecordMapper;
import com.taolife.aichat.param.RemoveSessionAdminParam;
import com.taolife.aichat.param.SessionDetailAdminParam;
import com.taolife.aichat.param.SessionPageAdminParam;
import com.taolife.aichat.service.IChatAdminService;
import com.taolife.aichat.vo.MessageAdminVO;
import com.taolife.aichat.vo.SessionDetailAdminVO;
import com.taolife.aichat.vo.SessionAdminVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理员AI会话服务实现
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAdminServiceImpl implements IChatAdminService {

    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    @Lazy
    private final FitnessRecordMapper constitutionRecordMapper;

    /**
     * 分页查询会话列表（管理后台）
     *
     * @param param 分页查询参数
     * @return 会话分页结果
     */
    @Override
    public PageResult<SessionAdminVO> getSessionPage(SessionPageAdminParam param) {
        Page<ChatSession> page;
        if (param.getAccountId() != null && !param.getAccountId().trim().isEmpty()) {
            page = chatSessionMapper.paginateByAccountIdForAdmin(param.getAccountId(), param.getPageNo(), param.getPageSize());
        } else if (param.getKeyword() != null && !param.getKeyword().trim().isEmpty()) {
            page = chatSessionMapper.searchByKeywordForAdmin(param.getKeyword(), param.getPageNo(), param.getPageSize());
        } else {
            page = chatSessionMapper.paginateForAdmin(param.getPageNo(), param.getPageSize());
        }

        PageResult<ChatSession> pageResult = PageResult.of(page);
        List<SessionAdminVO> list = pageResult.getList().stream()
                .map(this::convertToSessionAdminVO)
                .toList();

        PageResult<SessionAdminVO> result = new PageResult<>();
        result.setList(list);
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 获取会话详情（含消息列表）
     *
     * @param param 详情查询参数
     * @return 会话详情VO
     */
    @Override
    public SessionDetailAdminVO getSessionDetail(SessionDetailAdminParam param) {
        ChatSession session = chatSessionMapper.selectByIdIncludeDeleted(param.getSessionId());
        if (session == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "会话不存在");
        }

        SessionAdminVO sessionVO = convertToSessionAdminVO(session);

        List<ChatMessage> messages = chatMessageMapper.selectBySessionId(param.getSessionId());
        List<MessageAdminVO> messageVOs = messages.stream()
                .map(this::convertToMessageAdminVO)
                .toList();

        SessionDetailAdminVO detailVO = new SessionDetailAdminVO();
        detailVO.setSession(sessionVO);
        detailVO.setMessages(messageVOs);
        return detailVO;
    }

    /**
     * 删除会话（逻辑删除）
     *
     * @param param 删除参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSession(RemoveSessionAdminParam param) {
        ChatSession session = chatSessionMapper.selectByIdIncludeDeleted(param.getSessionId());
        if (session == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "会话不存在");
        }

        session.setIsDeleted(1);
        chatSessionMapper.update(session);
    }

    /**
     * 会话实体转管理端VO
     *
     * @param session 会话实体
     * @return 管理端会话VO
     */
    private SessionAdminVO convertToSessionAdminVO(ChatSession session) {
        SessionAdminVO vo = new SessionAdminVO();
        vo.setSessionId(session.getId());
        vo.setAccountId(session.getAccountId());
        vo.setSessionTitle(session.getSessionTitle());
        vo.setConstitutionId(session.getConstitutionId());
        vo.setMessageCount(session.getMessageCount());
        vo.setLastMessage(session.getLastMessage());
        vo.setLastMessageTime(session.getLastMessageTime());
        vo.setChatModel(session.getChatModel());
        vo.setEmbeddingModel(session.getEmbeddingModel());
        vo.setStatus(session.getStatus());
        vo.setCreateTime(session.getCreateTime());

        // 查询体质名称
        if (session.getConstitutionId() != null) {
            FitnessRecord record = constitutionRecordMapper.selectById(session.getConstitutionId());
            if (record != null && record.getConstitutionName() != null) {
                vo.setConstitutionName(record.getConstitutionName());
            }
        }
        return vo;
    }

    /**
     * 消息实体转管理端VO
     *
     * @param message 消息实体
     * @return 管理端消息VO
     */
    private MessageAdminVO convertToMessageAdminVO(ChatMessage message) {
        MessageAdminVO vo = new MessageAdminVO();
        vo.setMessageId(message.getId());
        vo.setSessionId(message.getSessionId());
        vo.setAccountId(message.getAccountId());
        vo.setRole(message.getRole());
        vo.setRoleName(getRoleName(message.getRole()));
        vo.setContent(message.getContent());
        vo.setQuestionType(message.getQuestionType());
        vo.setQuestionTypeName(getQuestionTypeName(message.getQuestionType()));
        vo.setIsSafe(message.getIsSafe());
        vo.setRiskReason(message.getRiskReason());
        vo.setKnowledgeRefs(message.getKnowledgeRefs());
        vo.setModelUsed(message.getModelUsed());
        vo.setTokensUsed(message.getTokensUsed());
        vo.setResponseTime(message.getResponseTime());
        vo.setCreateTime(message.getCreateTime());
        return vo;
    }

    /**
     * 获取角色名称
     *
     * @param role 角色编码
     * @return 角色名称
     */
    private String getRoleName(Integer role) {
        if (role == null) return "未知";
        return switch (role) {
            case 1 -> "用户";
            case 2 -> "AI助手";
            case 3 -> "系统";
            case 4 -> "工具";
            default -> "未知";
        };
    }

    /**
     * 获取问题类型名称
     *
     * @param questionType 问题类型编码
     * @return 问题类型名称
     */
    private String getQuestionTypeName(Integer questionType) {
        if (questionType == null) return "其他";
        return switch (questionType) {
            case 1 -> "体质类";
            case 2 -> "食疗类";
            case 3 -> "穴位类";
            case 4 -> "养生类";
            default -> "其他";
        };
    }
}