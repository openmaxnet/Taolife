package com.taolife.plan.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanComment;
import com.taolife.plan.mapper.PlanCommentMapper;
import com.taolife.plan.service.ICommentAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 方案评论管理服务实现类（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentAdminServiceImpl implements ICommentAdminService {

    private final PlanCommentMapper planCommentMapper;

    /**
     * 分页查询方案评论
     *
     * @param pageNo       页码
     * @param pageSize     每页条数
     * @param planSquareId 方案广场ID（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<PlanComment> getCommentPage(Integer pageNo, Integer pageSize, String planSquareId) {
        Page<PlanComment> page = planCommentMapper.selectAdminPage(new Page<>(pageNo, pageSize), planSquareId);
        return new PageResult<>(page.getRecords(), pageNo, pageSize, page.getTotalRow());
    }

    /**
     * 删除方案评论
     * 根据ID逻辑删除评论
     *
     * @param id 评论ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeComment(String id) {
        PlanComment comment = planCommentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "评论不存在");
        }
        comment.setIsDeleted(1);
        comment.setUpdateTime(LocalDateTime.now());
        planCommentMapper.update(comment);
        log.info("删除方案评论：id={}", id);
    }

    /**
     * 修改评论状态
     * 更新评论的审核或显示状态
     *
     * @param id     评论ID
     * @param status 状态值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyCommentStatus(String id, Integer status) {
        PlanComment comment = planCommentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "评论不存在");
        }
        comment.setStatus(status);
        comment.setUpdateTime(LocalDateTime.now());
        planCommentMapper.update(comment);
        log.info("修改方案评论状态：id={}, status={}", id, status);
    }
}
