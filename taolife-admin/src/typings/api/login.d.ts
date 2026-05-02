/// <reference path="../global.d.ts"/>

namespace Api {
  namespace Login {
    /** 登录返回的用户信息 */
    interface Info {
      /** 用户id */
      id: string
      /** 用户名 */
      username: string
      /** 真实姓名 */
      realName: string
      /** 头像URL */
      avatarUrl: string | null
      /** 角色编码列表 */
      roleCodes: string[]
      /** 角色名称列表 */
      roleNames: string[]
      /** 权限码列表（按钮/菜单/路由权限） */
      permissions: string[]
    }

    /** 登录接口返回的完整数据 */
    interface LoginResult {
      /** 访问令牌 */
      accessToken: string
      /** 刷新令牌 */
      refreshToken: string
      /** 用户信息 */
      userInfo: Info
    }
  }
}
