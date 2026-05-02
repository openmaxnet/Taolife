package com.taolife.plan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanTask;
import com.taolife.plan.service.ITaskAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 方案任务管理控制器（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/plan/admin/task")
@RequiredArgsConstructor
public class TaskAdminController {

    private final ITaskAdminService adminTaskService;

    /**
     * 分页查询方案任务
     *
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @param userPlanId 用户方案ID（可选，按方案筛选）
     * @return 分页结果
     */
    @GetMapping("/getTaskPage")
    public ExceptionResult<PageResult<PlanTask>> getTaskPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "userPlanId", required = false) String userPlanId) {
        log.info("分页查询方案任务，pageNo：{}，pageSize：{}，userPlanId：{}", pageNo, pageSize, userPlanId);
        return ExceptionResult.success(adminTaskService.getTaskPage(pageNo, pageSize, userPlanId));
    }
}
