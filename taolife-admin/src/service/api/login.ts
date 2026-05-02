import { request } from '../http'

interface ILogin {
  username: string
  password: string
}

/** 管理员登录 */
export function fetchLogin(data: ILogin) {
  const methodInstance = request.Post<Service.ResponseResult<Api.Login.LoginResult>>('/api/identity/admin/auth/adminLogin', data)
  methodInstance.meta = {
    authRole: null,
  }
  return methodInstance
}

/** 刷新令牌 */
export function fetchUpdateToken(refreshToken: string) {
  const method = request.Post<Service.ResponseResult<Api.Login.LoginResult>>('/api/identity/admin/auth/refreshToken', { refreshToken })
  method.meta = {
    authRole: null,
  }
  return method
}

/** 管理员登出 */
export function fetchLogout() {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/auth/adminLogout')
}

/** 获取当前管理员信息 */
export function fetchUserInfo() {
  return request.Get<Service.ResponseResult<Api.Login.Info>>('/api/identity/admin/auth/getAdminUserInfo')
}

/** 获取权限树 */
export function fetchPermissionTree() {
  return request.Get<Service.ResponseResult<any>>('/api/identity/admin/auth/getAdminPermissionTree')
}

/** 获取用户路由（动态路由模式使用） */
export function fetchUserRoutes() {
  return request.Get<Service.ResponseResult<Entity.Menu[]>>('/api/identity/admin/auth/getAdminRoutes')
}
