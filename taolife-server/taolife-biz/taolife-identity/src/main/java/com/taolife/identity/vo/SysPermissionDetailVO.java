package com.taolife.identity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统权限详情视图对象
 * 用于返回权限详细信息
 *
 * @author 文二
 * @date 2026-04-11
 */
@Data
@NoArgsConstructor
public class SysPermissionDetailVO {

    /**
     * 权限ID
     */
    private String id;

    /**
     * 父权限ID
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

    /**
     * 禁用标记（0：启用，1：禁用）
     */
    private Integer isDisabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}