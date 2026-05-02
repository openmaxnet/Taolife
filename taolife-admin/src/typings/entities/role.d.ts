/// <reference path="../global.d.ts"/>

/** 角色数据库表字段 */
namespace Entity {
  interface Role {
    /** 角色ID (binary(16) UUID) */
    id?: string
    /** 角色名称 */
    roleName?: string
    /** 角色编码 */
    roleCode?: string
    /** 角色描述 */
    description?: string
    /** 禁用标记（0：启用，1：禁用） */
    isDisabled?: 0 | 1
    /** 创建时间 */
    createTime?: string
  }

  interface RoleSaveParam {
    /** 角色名称 */
    roleName: string
    /** 角色编码 */
    roleCode: string
    /** 角色描述 */
    description?: string
    /** 权限ID列表 */
    permissionIds?: string[]
  }
}
