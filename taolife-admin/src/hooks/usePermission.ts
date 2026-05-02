import { useAuthStore } from '@/store'
import { isArray, isString } from 'radash'

/** 权限判断类型 */
type PermissionType = 'role' | 'permission'

/** 权限判断 */
export function usePermission() {
  const authStore = useAuthStore()

  /**
   * 检查用户是否有指定权限
   * @param permission 权限码或角色码
   * @param type 判断类型：'role' 基于角色判断，'permission' 基于权限码判断
   */
  function hasPermission(permission?: string | string[], type: PermissionType = 'role') {
    if (!permission) return true

    if (!authStore.userInfo) return false
    const { roleCodes, permissions } = authStore.userInfo

    // 角色为SUPER_ADMIN可直接通过所有权限检查
    if (roleCodes.includes('SUPER_ADMIN')) return true

    if (type === 'permission') {
      // 基于权限码判断
      if (!permissions || permissions.length === 0) return false
      if (isArray(permission))
        return permission.some(p => permissions.includes(p))
      return permissions.includes(permission)
    }

    // 基于角色判断（默认）
    if (isArray(permission))
      return permission.some(r => roleCodes.includes(r))
    return roleCodes.includes(permission)
  }

  /**
   * 检查用户是否有指定路由/菜单的访问权限
   * @param permission 路由权限码
   */
  function hasRoutePermission(permission?: string): boolean {
    if (!permission) return true
    if (!authStore.userInfo) return false

    const { roleCodes, permissions } = authStore.userInfo

    // 超级管理员拥有所有权限
    if (roleCodes.includes('SUPER_ADMIN')) return true

    // 检查权限码列表
    if (permissions?.includes(permission)) return true

    // 路由权限码格式通常为 "xxx:view"，也需要检查对应的编辑/删除等权限
    const basePermission = permission.replace(/:(view|add|edit|delete|import|export)$/, '')
    return permissions?.some(p => p.startsWith(basePermission)) ?? false
  }

  return {
    hasPermission,
    hasRoutePermission,
  }
}
