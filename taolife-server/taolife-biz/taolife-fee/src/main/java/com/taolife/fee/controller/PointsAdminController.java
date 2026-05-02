package com.taolife.fee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.service.IPointsAdminService;
import com.taolife.fee.vo.PointsRecordAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分管理控制器（管理员）
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/fee/admin/points")
@RequiredArgsConstructor
public class PointsAdminController {

    private final IPointsAdminService adminPointsService;

    /**
     * 分页查询积分变动记录
     *
     * @param pageNo    页码
     * @param pageSize  每页条数
     * @param accountId 用户账号ID（可选过滤条件）
     * @return 积分记录分页结果
     */
    @GetMapping("/getPointsRecordPage")
    public ExceptionResult<PageResult<PointsRecordAdminVO>> getPointsRecordPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "accountId", required = false) String accountId) {
        log.info("分页查询积分记录，pageNo：{}，pageSize：{}，accountId：{}", pageNo, pageSize, accountId);
        return ExceptionResult.success(adminPointsService.getPointsRecordPage(pageNo, pageSize, accountId));
    }
}
