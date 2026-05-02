package com.taolife.identity.controller;

import com.taolife.identity.param.SysUserPageParam;
import com.taolife.identity.param.SysUserSaveParam;
import com.taolife.identity.service.ISysUserService;
import com.taolife.identity.vo.SysUserDetailVO;
import com.taolife.identity.vo.SysUserVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统用户管理控制器
 * 负责处理系统用户相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-11
 */
@Slf4j
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
@RestController
@RequestMapping("/api/identity/admin/user")
@RequiredArgsConstructor
public class SysUserController {

    private final ISysUserService sysUserService;

    /**
     * 分页获取用户列表
     * 根据条件分页获取用户信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getSysUserPage")
    public ExceptionResult<PageResult<SysUserVO>> getSysUserPage(SysUserPageParam param) {
        log.info("分页获取用户列表，参数：{}", param);
        return ExceptionResult.success(sysUserService.getSysUserPage(param));
    }

    /**
     * 获取用户详情
     * 根据用户ID获取用户的详细信息
     *
     * @param id 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/getSysUserDetail")
    public ExceptionResult<SysUserDetailVO> getSysUserDetail(@RequestParam("id") String id) {
        log.info("获取用户详情，id：{}", id);
        return ExceptionResult.success(sysUserService.getSysUserDetail(id));
    }

    /**
     * 创建用户
     * 新增一个用户记录
     *
     * @param param 用户创建参数
     * @return 操作结果
     */
    @PostMapping("/createSysUser")
    public ExceptionResult<Void> createSysUser(@Valid @RequestBody SysUserSaveParam param) {
        log.info("创建用户，参数：{}", param);
        sysUserService.createSysUser(param);
        return ExceptionResult.success();
    }

    /**
     * 修改用户信息
     * 根据用户ID修改用户信息
     *
     * @param id   用户ID
     * @param param 用户修改参数
     * @return 操作结果
     */
    @PostMapping("/modifySysUserInfo")
    public ExceptionResult<Void> modifySysUserInfo(@RequestParam("id") String id,
                                               @Valid @RequestBody SysUserSaveParam param) {
        log.info("修改用户信息，id：{}，参数：{}", id, param);
        sysUserService.modifySysUserInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除用户
     * 根据用户ID删除用户（逻辑删除）
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PostMapping("/removeSysUser")
    public ExceptionResult<Void> removeSysUser(@RequestParam("id") String id) {
        log.info("删除用户，id：{}", id);
        sysUserService.removeSysUser(id);
        return ExceptionResult.success();
    }

    /**
     * 修改用户状态
     * 启用或禁用用户账号
     *
     * @param id         用户ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     * @return 操作结果
     */
    @PostMapping("/modifySysUserStatus")
    public ExceptionResult<Void> modifySysUserStatus(@RequestParam("id") String id,
                                                  @RequestParam("isDisabled") Integer isDisabled) {
        log.info("修改用户状态，id：{}，isDisabled：{}", id, isDisabled);
        sysUserService.modifySysUserStatus(id, isDisabled);
        return ExceptionResult.success();
    }
}