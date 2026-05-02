/**
 * 路由工具函数
 */

/** 获取当前页面参数 */
export const getPageOptions = (): Record<string, string> => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  return currentPage?.options || {}
}
