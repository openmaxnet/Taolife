package com.taolife.identity.controller;

import com.taolife.identity.param.SysRolePermissionParam;
import com.taolife.identity.param.SysRoleSaveParam;
import com.taolife.identity.service.ISysRoleService;
import com.taolife.identity.vo.SysRoleDetailVO;
import com.taolife.identity.vo.SysRoleVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统角色管理控制器
 * 负责处理系统角色相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-11
 */
@Slf4j
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
@RestController
@RequestMapping("/api/identity/admin/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final ISysRoleService sysRoleService;

    /**
     * 获取角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/getSysRoleList")
    public ExceptionResult<List<SysRoleVO>> getSysRoleList() {
        log.info("获取角色列表");
        return ExceptionResult.success(sysRoleService.getSysRoleList());
    }

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色详细信息
     */
    @GetMapping("/getSysRoleDetail")
    public ExceptionResult<SysRoleDetailVO> getSysRoleDetail(@RequestParam("id") String id) {
        log.info("获取角色详情，id：{}", id);
        return ExceptionResult.success(sysRoleService.getSysRoleDetail(id));
    }

    /**
     * 创建角色
     *
     * @param param 角色创建参数
     * @return 操作结果
     */
    @PostMapping("/createSysRole")
    public ExceptionResult<Void> createSysRole(@Valid @RequestBody SysRoleSaveParam param) {
        log.info("创建角色，参数：{}", param);
        sysRoleService.createSysRole(param);
        return ExceptionResult.success();
    }

    /**
     * 修改角色信息
     *
     * @param id    角色ID
     * @param param 角色修改参数
     * @return 操作结果
     */
    @PostMapping("/modifySysRoleInfo")
    public ExceptionResult<Void> modifySysRoleInfo(@RequestParam("id") String id,
                                                    @Valid @RequestBody SysRoleSaveParam param) {
        log.info("修改角色信息，id：{}，参数：{}", id, param);
        sysRoleService.modifySysRoleInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 操作结果
     */
    @PostMapping("/removeSysRole")
    public ExceptionResult<Void> removeSysRole(@RequestParam("id") String id) {
        log.info("删除角色，id：{}", id);
        sysRoleService.removeSysRole(id);
        return ExceptionResult.success();
    }

    /**
     * 修改角色状态
     *
     * @param id         角色ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     * @return 操作结果
     */
    @PostMapping("/modifySysRoleStatus")
    public ExceptionResult<Void> modifySysRoleStatus(@RequestParam("id") String id,
                                                      @RequestParam("isDisabled") Integer isDisabled) {
        log.info("修改角色状态，id：{}，isDisabled：{}", id, isDisabled);
        sysRoleService.modifySysRoleStatus(id, isDisabled);
        return ExceptionResult.success();
    }

    /**
     * 修改角色权限
     *
     * @param id    角色ID
     * @param param 权限修改参数
     * @return 操作结果
     */
    @PostMapping("/modifySysRolePermissions")
    public ExceptionResult<Void> modifySysRolePermissions(@RequestParam("id") String id,
                                                            @RequestBody SysRolePermissionParam param) {
        log.info("修改角色权限，id：{}，permissionIds：{}", id, param.getPermissionIds());
        sysRoleService.modifySysRolePermissions(id, param.getPermissionIds());
        return ExceptionResult.success();
    }
}
