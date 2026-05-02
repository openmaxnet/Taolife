import type { MenuOption } from 'naive-ui'
import type { RouteRecordRaw } from 'vue-router'
import { usePermission } from '@/hooks'
import Layout from '@/layouts/index.vue'
import { arrayToTree, renderIcon } from '@/utils'
import { clone, omit, pick } from 'radash'
import { RouterLink } from 'vue-router'

const metaFields: AppRoute.MetaKeys[] = [
  'title',
  'icon',
  'requiresAuth',
  'roles',
  'keepAlive',
  'hide',
  'order',
  'href',
  'activeMenu',
  'withoutTab',
  'pinTab',
  'menuType',
  'permission',
]

function standardizedRoutes(route: AppRoute.RowRoute[]) {
  return clone(route).map((i) => {
    const route = omit(i, metaFields)

    Reflect.set(route, 'meta', pick(i, metaFields))
    return route
  }) as AppRoute.Route[]
}

export function createRoutes(routes: AppRoute.RowRoute[]) {
  const { hasRoutePermission } = usePermission()

  // Structure the meta field
  let resultRouter = standardizedRoutes(routes)

  // Route permission filtering (基于权限码过滤)
  resultRouter = resultRouter.filter((i) => hasRoutePermission(i.meta.permission || i.permission))

  // 只保留页面路由，目录节点不进入 Vue Router（仅用于菜单分组）
  const pageRoutes = resultRouter.filter((i) => i.meta.menuType !== 'dir')

  // Resolve page components
  const modules = import.meta.glob('@/views/**/*.vue')
  pageRoutes.forEach((item: AppRoute.Route) => {
    if (item.componentPath) {
      const componentPath = item.componentPath.startsWith('/') ? item.componentPath : `/${item.componentPath}`
      const modulePath = `/src/views${componentPath}`
      if (modules[modulePath]) {
        item.component = modules[modulePath]
      } else {
        console.error(`[Router] ✗ 组件未找到: ${modulePath}`)
      }
    }
  })

  // 页面路由直接作为 appRoot 的平铺子路由（所有 path 都是绝对路径，不需要嵌套）
  const appRootRoute: RouteRecordRaw = {
    path: '/appRoot',
    name: 'appRoot',
    redirect: import.meta.env.VITE_HOME_PATH,
    component: Layout,
    meta: {
      title: '',
      icon: 'icon-park-outline:home',
    },
    children: pageRoutes as unknown as RouteRecordRaw[],
  }
  return appRootRoute
}

// Generate an array of route names that need to be kept alive
export function generateCacheRoutes(routes: AppRoute.RowRoute[]) {
  return routes
    .filter((i) => {
      // 只缓存有效的路由：有名称、启用keepAlive、不是重定向路由
      return i.name && i.keepAlive && i.menuType === 'page'
    })
    .map((i) => String(i.name))
}

/* 生成侧边菜单的数据 */
export function createMenus(userRoutes: AppRoute.RowRoute[]) {
  const resultMenus = standardizedRoutes(userRoutes)

  // filter menus that do not need to be displayed
  const visibleMenus = resultMenus.filter((route) => !route.meta.hide)

  // generate side menu
  return arrayToTree(transformAuthRoutesToMenus(visibleMenus))
}

// render the returned routing table as a sidebar
function transformAuthRoutesToMenus(userRoutes: AppRoute.Route[]) {
  const { hasRoutePermission } = usePermission()
  return (
    userRoutes
      // Filter out side menus without permission (基于权限码过滤)
      .filter((i) => hasRoutePermission(i.meta.permission || i.permission))
      //  Sort the menu according to the order size
      .sort((a, b) => {
        if (a.meta && a.meta.order && b.meta && b.meta.order) return a.meta.order - b.meta.order
        else if (a.meta && a.meta.order) return -1
        else if (b.meta && b.meta.order) return 1
        else return 0
      })
      // Convert to side menu data structure
      .map((item) => {
        const target: MenuOption = {
          id: item.id,
          pid: item.pid,
          label:
            !item.meta.menuType || item.meta.menuType === 'page'
              ? () =>
                  h(
                    RouterLink,
                    {
                      to: {
                        path: item.path,
                      },
                    },
                    { default: () => item.meta.title },
                  )
              : () => item.meta.title,
          key: item.path,
          icon: item.meta.icon ? renderIcon(item.meta.icon) : undefined,
        }
        return target
      })
  )
}
