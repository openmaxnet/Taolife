package com.taolife.plan.service;

import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanComment;

/**
 * 方案评论管理服务接口（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface ICommentAdminService {

    /**
     * 分页查询方案评论列表
     *
     * @param pageNo       页码
     * @param pageSize     每页大小
     * @param planSquareId 方案广场ID（可选）
     * @return 分页结果
     */
    PageResult<PlanComment> getCommentPage(Integer pageNo, Integer pageSize, String planSquareId);

    /**
     * 删除方案评论（逻辑删除）
     *
     * @param id 评论ID
     */
    void removeComment(String id);

    /**
     * 修改方案评论状态
     *
     * @param id     评论ID
     * @param status 状态
     */
    void modifyCommentStatus(String id, Integer status);
}
