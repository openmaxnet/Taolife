/// <reference path="../global.d.ts"/>

/** 用户数据库表字段 */
namespace Entity {
  interface User {
    /** 用户id (binary(16) UUID) */
    id?: string
    /** 用户名 */
    username?: string
    /** 真实姓名 */
    realName?: string
    /** 用户头像 */
    avatarUrl?: string
    /** 手机号 */
    phone?: string
    /** 邮箱 */
    email?: string
    /** 角色ID */
    roleId?: string
    /** 角色名称 */
    roleName?: string
    /** 禁用标记（0：启用，1：禁用） */
    isDisabled?: 0 | 1
    /** 最后登录IP */
    lastLoginIp?: string
    /** 最后登录时间 */
    lastLoginTime?: string
    /** 创建时间 */
    createTime?: string
  }

  interface UserPageParam {
    /** 页码 */
    pageNo?: number
    /** 每页大小 */
    pageSize?: number
    /** 真实姓名（模糊查询） */
    realName?: string
  }

  interface UserSaveParam {
    /** 用户名 */
    username: string
    /** 密码 */
    password?: string
    /** 真实姓名 */
    realName?: string
    /** 手机号 */
    phone?: string
    /** 邮箱 */
    email?: string
    /** 头像URL */
    avatarUrl?: string
    /** 角色ID */
    roleId: string
  }

  // ==================== 小程序账号管理 ====================
  interface AccountPageParam {
    pageNo?: number
    pageSize?: number
    keyword?: string
    memberLevel?: number
    startDate?: string
    endDate?: string
  }

  interface AccountVO {
    id: string
    nickname?: string
    avatarUrl?: string
    phone?: string
    gender?: number
    memberLevel?: number
    memberExpireTime?: string
    isDisabled?: number
    createTime?: string
  }

  interface AccountDetailVO extends AccountVO {
    realName?: string
    idCard?: string
    email?: string
    province?: string
    city?: string
    district?: string
    address?: string
    emergencyContact?: string
    emergencyPhone?: string
    height?: number
    weight?: number
    bloodType?: number
    allergyHistory?: string
    medicalHistory?: string
  }

  interface ModifyAccountStatusParam {
    id: string
    isDisabled: 0 | 1
  }

  interface ModifyMemberLevelParam {
    id: string
    memberLevel: number
    memberExpireTime?: string
  }

  // ==================== 系统协议管理 ====================
  interface SysAgreement {
    id?: string
    code?: string
    title?: string
    content?: string
    version?: number
    updateTime?: string
    createTime?: string
  }

  interface SysAgreementSaveParam {
    code: string
    title: string
    content?: string
  }
}
