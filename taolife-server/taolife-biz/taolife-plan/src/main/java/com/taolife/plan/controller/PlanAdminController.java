package com.taolife.plan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.service.IPlanAdminService;
import com.taolife.plan.vo.UserPlanDetailAdminVO;
import com.taolife.plan.vo.UserPlanAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户方案管理控制器（管理后台）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/plan/admin/plan")
@RequiredArgsConstructor
public class PlanAdminController {

    private final IPlanAdminService planAdminService;

    /**
     * 分页查询用户方案
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param keyword  关键词（可选）
     * @param status   状态（可选）
     * @return 分页结果
     */
    @GetMapping("/getUserPlanPage")
    public ExceptionResult<PageResult<UserPlanAdminVO>> getUserPlanPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status) {
        log.info("分页查询用户方案，pageNo：{}，pageSize：{}，keyword：{}，status：{}", pageNo, pageSize, keyword, status);
        return ExceptionResult.success(planAdminService.getUserPlanPage(pageNo, pageSize, keyword, status));
    }

    /**
     * 获取用户方案详情
     *
     * @param id 方案ID
     * @return 方案详细信息
     */
    @GetMapping("/getUserPlanDetail")
    public ExceptionResult<UserPlanDetailAdminVO> getUserPlanDetail(@RequestParam("id") String id) {
        log.info("获取用户方案详情，id：{}", id);
        return ExceptionResult.success(planAdminService.getUserPlanDetail(id));
    }

    /**
     * 修改用户方案状态
     *
     * @param id     方案ID
     * @param status 状态值
     * @return 操作结果
     */
    @PostMapping("/modifyUserPlanStatus")
    public ExceptionResult<Void> modifyUserPlanStatus(@RequestParam("id") String id,
                                                       @RequestParam("status") Integer status) {
        log.info("修改用户方案状态，id：{}，status：{}", id, status);
        planAdminService.modifyUserPlanStatus(id, status);
        return ExceptionResult.success();
    }
}
