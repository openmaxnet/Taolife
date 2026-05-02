package com.taolife.identity.controller;

import com.taolife.identity.param.SysPermissionPageParam;
import com.taolife.identity.param.SysPermissionSaveParam;
import com.taolife.identity.service.ISysPermissionService;
import com.taolife.identity.vo.SysPermissionDetailVO;
import com.taolife.identity.vo.SysPermissionVO;
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

import java.util.List;

/**
 * 系统权限管理控制器
 * 负责处理系统权限（菜单/按钮）相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-11
 */
@Slf4j
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
@RestController
@RequestMapping("/api/identity/admin/permission")
@RequiredArgsConstructor
public class SysPermissionController {

    private final ISysPermissionService sysPermissionService;

    /**
     * 获取权限树
     * 获取权限列表并构建树形结构
     *
     * @param type 权限类型（1：菜单，2：按钮，可为空）
     * @return 权限树列表
     */
    @GetMapping("/getPermissionTree")
    public ExceptionResult<List<SysPermissionVO>> getPermissionTree(@RequestParam(value = "type", required = false) Integer type) {
        log.info("获取权限树，type：{}", type);
        return ExceptionResult.success(sysPermissionService.getPermissionTree(type));
    }

    /**
     * 分页获取权限列表
     * 根据条件分页获取权限信息
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getPermissionPage")
    public ExceptionResult<PageResult<SysPermissionVO>> getPermissionPage(SysPermissionPageParam param) {
        log.info("分页获取权限列表，参数：{}", param);
        return ExceptionResult.success(sysPermissionService.getPermissionPage(param));
    }

    /**
     * 获取权限详情
     * 根据权限ID获取权限的详细信息
     *
     * @param id 权限ID
     * @return 权限详细信息
     */
    @GetMapping("/getPermissionDetail")
    public ExceptionResult<SysPermissionDetailVO> getPermissionDetail(@RequestParam("id") String id) {
        log.info("获取权限详情，id：{}", id);
        return ExceptionResult.success(sysPermissionService.getPermissionDetail(id));
    }

    /**
     * 创建权限
     * 新增一个权限记录
     *
     * @param param 权限创建参数
     * @return 操作结果
     */
    @PostMapping("/createPermission")
    public ExceptionResult<Void> createPermission(@Valid @RequestBody SysPermissionSaveParam param) {
        log.info("创建权限，参数：{}", param);
        sysPermissionService.createPermission(param);
        return ExceptionResult.success();
    }

    /**
     * 修改权限信息
     * 根据权限ID修改权限信息
     *
     * @param id   权限ID
     * @param param 权限修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyPermissionInfo")
    public ExceptionResult<Void> modifyPermissionInfo(@RequestParam("id") String id,
                                                      @Valid @RequestBody SysPermissionSaveParam param) {
        log.info("修改权限信息，id：{}，参数：{}", id, param);
        sysPermissionService.modifyPermissionInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除权限
     * 根据权限ID删除权限（逻辑删除）
     *
     * @param id 权限ID
     * @return 操作结果
     */
    @PostMapping("/removePermission")
    public ExceptionResult<Void> removePermission(@RequestParam("id") String id) {
        log.info("删除权限，id：{}", id);
        sysPermissionService.removePermission(id);
        return ExceptionResult.success();
    }

    /**
     * 批量删除权限
     * 根据权限ID列表批量删除权限
     *
     * @param ids 权限ID列表
     * @return 操作结果
     */
    @PostMapping("/removePermissionBatch")
    public ExceptionResult<Void> removePermissionBatch(@RequestBody List<String> ids) {
        log.info("批量删除权限，ids：{}", ids);
        sysPermissionService.removePermissionBatch(ids);
        return ExceptionResult.success();
    }
}