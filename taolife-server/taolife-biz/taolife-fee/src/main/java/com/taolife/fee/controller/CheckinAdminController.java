package com.taolife.fee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.CheckinRecord;
import com.taolife.fee.service.ICheckinAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 签到管理控制器（后台管理）
 * 查看所有用户的签到记录
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/fee/admin/checkin")
@RequiredArgsConstructor
public class CheckinAdminController {

    private final ICheckinAdminService adminCheckinService;

    /**
     * 分页查询签到记录
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 签到记录分页结果
     */
    @GetMapping("/getCheckinRecordPage")
    public ExceptionResult<PageResult<CheckinRecord>> getCheckinRecordPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        log.info("分页查询签到记录，pageNo：{}，pageSize：{}", pageNo, pageSize);
        return ExceptionResult.success(adminCheckinService.getCheckinRecordPage(pageNo, pageSize));
    }
}
