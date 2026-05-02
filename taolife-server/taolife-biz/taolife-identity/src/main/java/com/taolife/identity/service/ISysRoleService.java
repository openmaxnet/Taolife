package com.taolife.identity.service;

import com.taolife.identity.param.SysRoleSaveParam;
import com.taolife.identity.vo.SysRoleDetailVO;
import com.taolife.identity.vo.SysRoleVO;

import java.util.List;

/**
 * 系统角色服务接口
 *
 * @author 文二
 * @date 2026-04-11
 */
public interface ISysRoleService {

    /**
     * 获取角色列表
     *
     * @return 角色列表
     */
    List<SysRoleVO> getSysRoleList();

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色详细信息
     */
    SysRoleDetailVO getSysRoleDetail(String id);

    /**
     * 创建角色
     *
     * @param param 角色保存参数
     */
    void createSysRole(SysRoleSaveParam param);

    /**
     * 修改角色信息
     *
     * @param id    角色ID
     * @param param 角色保存参数
     */
    void modifySysRoleInfo(String id, SysRoleSaveParam param);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void removeSysRole(String id);

    /**
     * 修改角色状态
     *
     * @param id         角色ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    void modifySysRoleStatus(String id, Integer isDisabled);

    /**
     * 修改角色权限
     *
     * @param id            角色ID
     * @param permissionIds 权限ID列表
     */
    void modifySysRolePermissions(String id, List<String> permissionIds);
}
