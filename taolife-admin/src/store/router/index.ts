import type { MenuOption } from 'naive-ui'
import { router } from '@/router'
import { fetchUserRoutes } from '@/service'
import { $t } from '@/utils'
import { createMenus, createRoutes, generateCacheRoutes } from './helper'

/** 将后端字段转换为前端 RowRoute 格式 */
/** 将后端字段转换为前端 RowRoute 格式 */
function transformApiRoutes(data: Entity.Menu[]): AppRoute.RowRoute[] {
  return data.map(item => ({
    id: item.id ?? '',
    pid: item.parentId ?? null,
    name: item.permissionCode ?? '',
    path: item.path ?? '',
    title: item.permissionName ?? '',
    icon: item.icon,
    componentPath: item.component || undefined,
    order: item.sortOrder,
    requiresAuth: true,
    menuType: item.component ? 'page' as const : 'dir' as const,
  }))
}

/** 内置隐藏路由（不受权限控制，如个人中心） */
const builtinRoutes: AppRoute.RowRoute[] = [
  {
    name: 'userCenter',
    path: '/user-center',
    title: '个人中心',
    requiresAuth: true,
    hide: true,
    icon: 'carbon:user-avatar-filled-alt',
    componentPath: '/build-in/user-center/index.vue',
    id: 'builtin-user-center',
    pid: null,
  },
]

interface RoutesStatus {
  isInitAuthRoute: boolean
  menus: MenuOption[]
  rowRoutes: AppRoute.RowRoute[]
  activeMenu: string | null
  cacheRoutes: string[]
}
export const useRouteStore = defineStore('route-store', {
  state: (): RoutesStatus => {
    return {
      isInitAuthRoute: false,
      activeMenu: null,
      menus: [],
      rowRoutes: [],
      cacheRoutes: [],
    }
  },
  actions: {
    resetRouteStore() {
      this.resetRoutes()
      this.$reset()
    },
    resetRoutes() {
      if (router.hasRoute('appRoot')) router.removeRoute('appRoot')
    },
    // set the currently highlighted menu key
    setActiveMenu(key: string) {
      this.activeMenu = key
    },

    async initRouteInfo() {
      const result = await fetchUserRoutes()

      if (!result.isSuccess || !result.data) {
        throw new Error('Failed to fetch user routes')
      }

      // 合并内置隐藏路由（不受权限控制）
      return [...transformApiRoutes(result.data), ...builtinRoutes]
    },
    async initAuthRoute() {
      this.isInitAuthRoute = false

      try {
        // Initialize route information
        const rowRoutes = await this.initRouteInfo()
        if (!rowRoutes) {
          const error = new Error('Failed to get route information')
          window.$message.error($t(`app.getRouteError`))
          throw error
        }
        this.rowRoutes = rowRoutes

        // Generate actual route and insert
        const routes = createRoutes(rowRoutes)
        router.addRoute(routes)

        // Generate side menu
        this.menus = createMenus(rowRoutes)

        // Generate the route cache
        this.cacheRoutes = generateCacheRoutes(rowRoutes)

        this.isInitAuthRoute = true
      } catch (error) {
        // 标记为已尝试，防止无限重试
        this.isInitAuthRoute = true
        throw error
      }
    },
  },
})
