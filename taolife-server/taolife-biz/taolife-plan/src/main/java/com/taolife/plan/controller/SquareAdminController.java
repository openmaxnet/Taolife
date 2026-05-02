package com.taolife.plan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.plan.entity.PlanSquare;
import com.taolife.plan.service.ISquareAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 方案广场管理控制器（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/plan/admin/square")
@RequiredArgsConstructor
public class SquareAdminController {

    private final ISquareAdminService adminSquareService;

    /**
     * 分页查询方案广场
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param status   状态（可选）
     * @return 分页结果
     */
    @GetMapping("/getSquarePage")
    public ExceptionResult<PageResult<PlanSquare>> getSquarePage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status) {
        log.info("分页查询方案广场，pageNo：{}，pageSize：{}，status：{}", pageNo, pageSize, status);
        return ExceptionResult.success(adminSquareService.getSquarePage(pageNo, pageSize, status));
    }

    /**
     * 修改方案广场状态
     * 审核或管理方案在广场的展示状态
     *
     * @param id     方案ID
     * @param status 状态值
     * @return 操作结果
     */
    @PostMapping("/modifySquareStatus")
    public ExceptionResult<Void> modifySquareStatus(@RequestParam("id") String id,
                                                     @RequestParam("status") Integer status) {
        log.info("修改方案广场状态，id：{}，status：{}", id, status);
        adminSquareService.modifySquareStatus(id, status);
        return ExceptionResult.success();
    }
}
