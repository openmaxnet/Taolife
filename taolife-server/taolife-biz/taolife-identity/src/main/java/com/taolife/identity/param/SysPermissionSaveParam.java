package com.taolife.identity.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 系统权限保存参数
 * 用于创建和修改权限（菜单/按钮）
 *
 * @author 文二
 * @date 2026-04-11
 */
@Data
public class SysPermissionSaveParam {

    /**
     * 父权限ID
     */
    private String parentId;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限类型（1：菜单，2：按钮）
     */
    @NotNull(message = "权限类型不能为空")
    private Integer permissionType;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序序号
     */
    private Integer sortOrder;
}