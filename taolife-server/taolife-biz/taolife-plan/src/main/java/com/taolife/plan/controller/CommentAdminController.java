package com.taolife.plan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanComment;
import com.taolife.plan.service.ICommentAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 方案评论管理控制器（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/plan/admin/comment")
@RequiredArgsConstructor
public class CommentAdminController {

    private final ICommentAdminService adminCommentService;

    /**
     * 分页查询方案评论
     *
     * @param pageNo       页码
     * @param pageSize     每页条数
     * @param planSquareId 方案广场ID（可选，按方案筛选）
     * @return 分页结果
     */
    @GetMapping("/getCommentPage")
    public ExceptionResult<PageResult<PlanComment>> getCommentPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String planSquareId) {
        log.info("分页查询方案评论，pageNo：{}，pageSize：{}，planSquareId：{}", pageNo, pageSize, planSquareId);
        return ExceptionResult.success(adminCommentService.getCommentPage(pageNo, pageSize, planSquareId));
    }

    /**
     * 删除方案评论
     *
     * @param id 评论ID
     * @return 操作结果
     */
    @PostMapping("/removeComment")
    public ExceptionResult<Void> removeComment(@RequestParam("id") String id) {
        log.info("删除方案评论，id：{}", id);
        adminCommentService.removeComment(id);
        return ExceptionResult.success();
    }

    /**
     * 修改评论状态
     * 审核或管理评论的显示状态
     *
     * @param id     评论ID
     * @param status 状态值
     * @return 操作结果
     */
    @PostMapping("/modifyCommentStatus")
    public ExceptionResult<Void> modifyCommentStatus(@RequestParam("id") String id,
                                                      @RequestParam("status") Integer status) {
        log.info("修改方案评论状态，id：{}，status：{}", id, status);
        adminCommentService.modifyCommentStatus(id, status);
        return ExceptionResult.success();
    }
}
