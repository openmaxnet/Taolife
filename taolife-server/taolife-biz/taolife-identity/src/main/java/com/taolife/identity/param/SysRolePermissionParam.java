package com.taolife.identity.param;

import lombok.Data;

import java.util.List;

/**
 * 系统角色权限保存参数
 *
 * @author 文二
 * @date 2026-04-13
 */
@Data
public class SysRolePermissionParam {

    /**
     * 权限ID列表
     */
    private List<String> permissionIds;
}
