package com.taolife.identity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 权限树视图对象
 * 用于返回菜单和按钮权限的树形结构给前端
 *
 * @author 文二
 * @date 2026-04-10
 */
@Data
@NoArgsConstructor
public class PermissionTreeVO {

    /**
     * 权限ID
     */
    private String id;

    /**
     * 父权限ID（空表示顶级节点）
     */
    private String parentId;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限类型（1：菜单，2：按钮）
     */
    private Integer permissionType;

    /**
     * 前端路由路径
     */
    private String path;

    /**
     * 前端组件路径
     */
    private String component;

    /**
     * 图标名称
     */
    private String icon;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 子权限列表
     */
    private List<PermissionTreeVO> children;
}
