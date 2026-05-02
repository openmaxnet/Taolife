package com.taolife.identity.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统权限实体类
 * 对应数据库表 tf_sys_permission，支持菜单和按钮两种权限类型
 *
 * @author 文二
 * @date 2026-04-10
 */
@Data
@Table("tl_sys_permission")
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID（主键）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
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
     * 权限编码（唯一标识）
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
     * 排序序号（升序排列）
     */
    private Integer sortOrder;

    /**
     * 禁用标记（0：启用，1：禁用）
     */
    private Integer isDisabled;

    /**
     * 逻辑删除标记（0：未删除，1：已删除）
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 子权限列表（非数据库字段）
     */
    @Column(ignore = true)
    private List<SysPermission> children;
}
