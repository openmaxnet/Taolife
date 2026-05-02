/**
 * TaoLife 认证管理器
 * 使用 @om/uni-base 的 createAuthManager + 微信小程序策略
 */

import { createAuthManager } from '@om/uni-base'
import type { AuthTokens } from '@om/uni-base'
import { getToken } from '@/utils/storage'
import { BASE_URL } from '@/utils/config'
import type { ExceptionResult } from '@/types'

// ──── 内部 API 请求（uni.request 直调，避免依赖 request.ts） ────

const apiRequest = <T>(url: string, method: 'GET' | 'POST', data?: any): Promise<T> => {
  const header: Record<string, string> = { 'Content-Type': 'application/json' }
  const token = getToken()
  if (token) header['Authorization'] = `Bearer ${token}`
  return new Promise((resolve, reject) => {
    uni.request({
      url: url.startsWith('http') ? url : BASE_URL + url,
      method, data, header,
      timeout: 15000,
      success: (res) => {
        if (res.statusCode === 200) {
          const rd = res.data as ExceptionResult<T>
          if (rd.code?.startsWith('S')) resolve(rd.data)
          else reject(rd)
        } else {
          reject(res.data)
        }
      },
      fail: reject,
    })
  })
}

// ──── 默认实例（微信小程序策略） ────

const defaultManager = createAuthManager({
  loginFn: async (): Promise<AuthTokens> => {
    const loginRes = await uni.login({ provider: 'weixin' })
    if (!loginRes?.code) throw new Error('wx.login failed: no code returned')
    return apiRequest<AuthTokens>('/api/identity/account/wxLogin', 'POST', { code: loginRes.code })
  },
  refreshFn: (refreshToken: string) =>
    apiRequest<AuthTokens | null>('/api/identity/account/refreshToken', 'POST', { refreshToken }).catch(() => null),
})

// 导出默认实例（保持向后兼容）
export const isAuth = defaultManager.isAuth
export const enAuth = defaultManager.enAuth
export const wxLogin = defaultManager.enAuth
export const handleAuthError = defaultManager.handleAuthError
export const launchSilentLogin = defaultManager.launchSilentLogin
export const checkLocalAuthState = defaultManager.checkLocalAuthState
export const onLoginSuccess = defaultManager.onLoginSuccess
