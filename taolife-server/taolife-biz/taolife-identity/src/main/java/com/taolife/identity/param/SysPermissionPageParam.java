package com.taolife.identity.param;

import lombok.Data;

/**
 * 系统权限分页查询参数
 *
 * @author 文二
 * @date 2026-04-11
 */
@Data
public class SysPermissionPageParam {

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 权限类型（1：菜单，2：按钮）
     */
    private Integer type;

    /**
     * 权限名称（模糊查询）
     */
    private String permissionName;
}