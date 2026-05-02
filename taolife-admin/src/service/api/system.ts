import { request } from '../http'

// ==================== 字典管理 ====================

/**
 * 请求获取字典列表
 *
 * @param code - 字典编码，用于筛选特定的字典列表
 * @returns 返回的字典列表数据
 */
export function fetchDictList(code?: string) {
  const params = { code }
  return request.Get<Service.ResponseResult<Entity.Dict[]>>('/dict/list', { params })
}

// ==================== 路由管理 ====================

/**
 * 获取当前用户的路由信息
 * 用于登录后获取用户的权限路由
 */
export function fetchAllRoutes() {
  return request.Get<Service.ResponseResult<AppRoute.RowRoute[]>>('/getUserRoutes')
}

// ==================== 用户管理 ====================

/**
 * 分页获取用户列表
 * @param params - 查询参数 { pageNo, pageSize, realName }
 */
export function getSysUserPage(params: Entity.UserPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.User>>>('/api/identity/admin/user/getSysUserPage', { params })
}

/**
 * 获取用户详情
 * @param id - 用户ID
 */
export function getSysUserDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.User>>('/api/identity/admin/user/getSysUserDetail', { params: { id } })
}

/**
 * 创建用户
 * @param data - 用户信息
 */
export function createSysUser(data: Entity.UserSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/user/createSysUser', data)
}

/**
 * 修改用户信息
 * @param id - 用户ID
 * @param data - 用户信息
 */
export function modifySysUserInfo(id: string, data: Entity.UserSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/user/modifySysUserInfo', data, { params: { id } })
}

/**
 * 删除用户
 * @param id - 用户ID
 */
export function removeSysUser(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/user/removeSysUser', undefined, { params: { id } })
}

/**
 * 修改用户状态
 * @param id - 用户ID
 * @param isDisabled - 禁用标记（0：启用，1：禁用）
 */
export function modifySysUserStatus(id: string, isDisabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/user/modifySysUserStatus', undefined, { params: { id, isDisabled } })
}

// ==================== 角色管理 ====================

/**
 * 获取角色列表
 */
export function getSysRoleList() {
  return request.Get<Service.ResponseResult<Entity.Role[]>>('/api/identity/admin/role/getSysRoleList')
}

/**
 * 获取角色详情
 * @param id - 角色ID
 */
export function getSysRoleDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.Role>>('/api/identity/admin/role/getSysRoleDetail', { params: { id } })
}

/**
 * 创建角色
 * @param data - 角色信息
 */
export function createSysRole(data: Entity.RoleSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/role/createSysRole', data)
}

/**
 * 修改角色信息
 * @param id - 角色ID
 * @param data - 角色信息
 */
export function modifySysRoleInfo(id: string, data: Entity.RoleSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/role/modifySysRoleInfo', data, { params: { id } })
}

/**
 * 删除角色
 * @param id - 角色ID
 */
export function removeSysRole(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/role/removeSysRole', undefined, { params: { id } })
}

/**
 * 修改角色状态
 * @param id - 角色ID
 * @param isDisabled - 禁用标记（0：启用，1：禁用）
 */
export function modifySysRoleStatus(id: string, isDisabled: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/role/modifySysRoleStatus', undefined, { params: { id, isDisabled } })
}

/**
 * 修改角色权限
 * @param id - 角色ID
 * @param permissionIds - 权限ID列表
 */
export function modifySysRolePermissions(id: string, permissionIds: string[]) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/role/modifySysRolePermissions', { permissionIds }, { params: { id } })
}

// ==================== 菜单/权限管理 ====================

/**
 * 获取权限树形列表
 * @param type - 权限类型（1=菜单, 2=按钮）
 */
export function getPermissionTree(type?: 1 | 2) {
  return request.Get<Service.ResponseResult<Entity.Menu[]>>('/api/identity/admin/permission/getPermissionTree', { params: { type } })
}

/**
 * 获取权限详情
 * @param id - 权限ID
 */
export function getPermissionDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.Menu>>('/api/identity/admin/permission/getPermissionDetail', { params: { id } })
}

/**
 * 创建权限
 * @param data - 权限信息
 */
export function createPermission(data: Entity.MenuSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/permission/createPermission', data)
}

/**
 * 修改权限信息
 * @param id - 权限ID
 * @param data - 权限信息
 */
export function modifyPermissionInfo(id: string, data: Entity.MenuSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/permission/modifyPermissionInfo', data, { params: { id } })
}

/**
 * 删除权限
 * @param id - 权限ID
 */
export function removePermission(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/permission/removePermission', undefined, { params: { id } })
}

/**
 * 批量删除权限
 * @param ids - 权限ID列表
 */
export function removePermissionBatch(ids: string[]) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/permission/removePermissionBatch', ids)
}
