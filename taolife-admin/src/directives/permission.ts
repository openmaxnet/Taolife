import type { App, Directive } from 'vue'
import { usePermission } from '@/hooks'

/** 权限指令绑定值类型 */
type PermissionValue = string | string[]
type PermissionType = 'role' | 'permission'

interface PermissionBinding {
  value: PermissionValue
  modifiers?: {
    role?: boolean
    permission?: boolean
  }
}

export function install(app: App) {
  const { hasPermission } = usePermission()

  function updatePermission(el: HTMLElement, binding: PermissionBinding) {
    const { value, modifiers } = binding
    if (!value) throw new Error('v-permission Directive requires a permission code')

    // 默认为权限码判断，可通过 v-permission.role 切换为角色判断
    const type: PermissionType = modifiers?.role ? 'role' : 'permission'
    const hasAuth = hasPermission(value as string | string[], type)

    if (!hasAuth) el.parentElement?.removeChild(el)
  }

  const permissionDirective: Directive<HTMLElement, PermissionValue> = {
    mounted(el, binding) {
      updatePermission(el, binding as PermissionBinding)
    },
    updated(el, binding) {
      updatePermission(el, binding as PermissionBinding)
    },
  }
  app.directive('permission', permissionDirective)
}
