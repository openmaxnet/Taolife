/// <reference path="../global.d.ts"/>

/** 菜单/权限数据库表字段（对齐后端 SysPermissionVO） */
namespace Entity {
  interface Menu {
    /** 权限ID */
    id?: string
    /** 父权限ID */
    parentId?: string | null
    /** 权限编码（路由标识） */
    permissionCode?: string
    /** 路由路径 */
    path?: string
    /** 权限名称（菜单标题） */
    permissionName?: string
    /** 图标 */
    icon?: string
    /** 组件路径 */
    component?: string
    /** 排序序号 */
    sortOrder?: number
    /** 权限类型（1：菜单，2：按钮） */
    permissionType?: 1 | 2
    /** 禁用标记（0：启用，1：禁用） */
    isDisabled?: 0 | 1
    /** 子权限列表 */
    children?: Menu[]
    /** 创建时间 */
    createTime?: string
  }

  interface MenuSaveParam {
    /** 父权限ID */
    parentId?: string | null
    /** 权限编码（路由标识） */
    permissionCode: string
    /** 权限名称（菜单标题） */
    permissionName?: string
    /** 路由路径 */
    path?: string
    /** 组件路径 */
    component?: string
    /** 图标 */
    icon?: string
    /** 排序序号 */
    sortOrder?: number
    /** 权限类型（1：菜单，2：按钮） */
    permissionType: 1 | 2
  }
}
