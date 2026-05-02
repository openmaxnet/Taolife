package com.taolife.plan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanAdjustRecord;
import com.taolife.plan.service.IAdjustAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 方案调整记录管理控制器（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/plan/admin/adjustment")
@RequiredArgsConstructor
public class AdjustAdminController {

    private final IAdjustAdminService adminAdjustService;

    /**
     * 分页查询方案调整记录
     *
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @param userPlanId 用户方案ID（可选，按方案筛选）
     * @return 分页结果
     */
    @GetMapping("/getAdjustmentPage")
    public ExceptionResult<PageResult<PlanAdjustRecord>> getAdjustmentPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "userPlanId", required = false) String userPlanId) {
        log.info("分页查询方案调整记录，pageNo：{}，pageSize：{}，userPlanId：{}", pageNo, pageSize, userPlanId);
        return ExceptionResult.success(adminAdjustService.getAdjustmentPage(pageNo, pageSize, userPlanId));
    }
}
