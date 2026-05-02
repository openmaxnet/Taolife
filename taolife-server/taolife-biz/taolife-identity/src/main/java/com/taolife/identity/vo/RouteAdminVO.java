package com.taolife.identity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员路由视图对象
 * 用于返回当前管理员可访问的扁平路由列表
 *
 * @author 文二
 * @date 2026-04-11
 */
@Data
@NoArgsConstructor
public class RouteAdminVO {

    /**
     * 路由ID（权限ID）
     */
    private String id;

    /**
     * 父级路由ID（顶级为null）
     */
    private String parentId;

    /**
     * 权限编码（路由标识）
     */
    private String permissionCode;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 权限名称（菜单标题）
     */
    private String permissionName;

    /**
     * 图标
     */
    private String icon;

    /**
     * 组件路径（目录类型为null）
     */
    private String component;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 权限类型（1：菜单）
     */
    private Integer permissionType;
}
