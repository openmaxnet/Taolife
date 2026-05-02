import type { Router } from 'vue-router'
import { useAppStore, useAuthStore, useRouteStore, useTabStore } from '@/store'
import { local } from '@/utils'

const title = import.meta.env.VITE_APP_NAME

export function setupRouterGuard(router: Router) {
  router.beforeEach(async (to, _from) => {
    // 在回调内部获取 store，确保此时 Pinia 已初始化
    const appStore = useAppStore()
    const routeStore = useRouteStore()

    // 判断是否是外链，如果是直接打开网页并拦截跳转
    if (to.meta.href) {
      window.open(to.meta.href)
      return false // 取消当前导航
    }
    // 开始 loadingBar
    if (appStore.showProgress) window.$loadingBar?.start()

    // 判断有无TOKEN,登录鉴权
    const isLogin = Boolean(local.get('tf_access_token'))

    // 处理根路由重定向
    if (to.name === 'root') {
      if (isLogin) {
        // 已登录，重定向到首页
        return { path: import.meta.env.VITE_HOME_PATH, replace: true }
      } else {
        // 未登录，重定向到登录页
        return { path: '/login', replace: true }
      }
    }

    // 如果是login路由，直接放行
    if (to.name === 'login') {
      // login页面不需要任何认证检查，直接放行
      // 继续执行后面的逻辑
    }
    // 如果路由明确设置了requiresAuth为false，直接放行
    else if (to.meta.requiresAuth === false) {
      // 明确设置为false的路由直接放行
      // 继续执行后面的逻辑
    }
    // 如果路由设置了requiresAuth为true，且用户未登录，重定向到登录页
    else if (to.meta.requiresAuth === true && !isLogin) {
      const redirect = to.name === 'not-found' ? undefined : to.fullPath
      return { path: '/login', query: { redirect } }
    }

    // 判断路由有无进行初始化
    if (!routeStore.isInitAuthRoute && to.name !== 'login') {
      try {
        await routeStore.initAuthRoute()
        // 动态路由加载完回到根路由
        if (to.name === 'not-found') {
          // 等待权限路由加载好了，回到之前的路由,否则404
          return {
            path: to.fullPath,
            replace: true,
            query: to.query,
            hash: to.hash,
          }
        }
      } catch {
        // 路由初始化失败，清除认证状态防止无限重定向循环
        const authStore = useAuthStore()
        authStore.clearAuthStorage()
        authStore.$reset()
        window.$message?.error('路由加载失败，请重新登录')
        return { path: '/login' }
      }
    }

    // 如果用户已登录且访问login页面，重定向到首页
    if (to.name === 'login' && isLogin) {
      return { path: '/' }
    }
  })
  router.beforeResolve((to) => {
    // 在回调内部获取 store
    const routeStore = useRouteStore()
    const tabStore = useTabStore()

    // 设置菜单高亮
    routeStore.setActiveMenu(to.meta.activeMenu ?? to.fullPath)
    // 添加tabs
    tabStore.addTab(to)
    // 设置高亮标签;
    tabStore.setCurrentTab(to.fullPath as string)
  })

  router.afterEach((to) => {
    // 在回调内部获取 store
    const appStore = useAppStore()

    // 修改网页标题
    document.title = `${to.meta.title} - ${title}`
    // 结束 loadingBar
    if (appStore.showProgress) window.$loadingBar?.finish()
  })
}
