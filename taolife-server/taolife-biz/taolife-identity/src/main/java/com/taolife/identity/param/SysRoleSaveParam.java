package com.taolife.identity.param;

import lombok.Data;

import java.util.List;

/**
 * 系统角色保存参数
 * 用于创建和修改角色
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class SysRoleSaveParam {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 权限ID列表
     */
    private List<String> permissionIds;
}
