package com.taolife.identity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.identity.entity.UserPreference;
import com.taolife.identity.service.IUserPreferenceAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户偏好管理控制器（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/identity/admin/userPreference")
@RequiredArgsConstructor
public class UserPreferenceAdminController {

    private final IUserPreferenceAdminService adminUserPreferenceService;

    /**
     * 分页查询用户偏好
     * 分页获取所有用户的偏好设置列表
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @GetMapping("/getUserPreferencePage")
    public ExceptionResult<PageResult<UserPreference>> getUserPreferencePage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        log.info("分页查询用户偏好，pageNo：{}，pageSize：{}", pageNo, pageSize);
        return ExceptionResult.success(adminUserPreferenceService.getUserPreferencePage(pageNo, pageSize));
    }
}
