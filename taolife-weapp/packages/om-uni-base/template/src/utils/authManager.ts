/**
 * 认证管理器
 * 使用 @om/uni-base 的 createAuthManager + 微信小程序策略
 */

import { createAuthManager, createWechatAuthStrategy, configureRequest } from '@om/uni-base'
import type { AuthTokens } from '@om/uni-base'
import { getToken } from '@om/uni-base'
import { BASE_URL } from './config'

// ──── 内部 API 请求（uni.request 直调，避免循环依赖） ────

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
          const rd = res.data as { code: string; data: T }
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

// ──── 微信小程序认证策略 ────

const strategy = createWechatAuthStrategy({
  loginFn: async (): Promise<AuthTokens> => {
    const loginRes = await uni.login({ provider: 'weixin' })
    if (!loginRes?.code) throw new Error('wx.login failed: no code returned')
    return apiRequest<AuthTokens>('/api/identity/account/wxLogin', 'POST', { code: loginRes.code })
  },
  refreshFn: (refreshToken: string) =>
    apiRequest<AuthTokens | null>('/api/identity/account/refreshToken', 'POST', { refreshToken }).catch(() => null),
})

const defaultManager = createAuthManager(strategy)

// ──── 配置 request 模块 ────

configureRequest({
  getBaseUrl: () => BASE_URL,
  handleAuthError: defaultManager.handleAuthError,
})

// 导出
export const isAuth = defaultManager.isAuth
export const enAuth = defaultManager.enAuth
export const handleAuthError = defaultManager.handleAuthError
export const launchSilentLogin = defaultManager.launchSilentLogin
export const checkLocalAuthState = defaultManager.checkLocalAuthState
export const onLoginSuccess = defaultManager.onLoginSuccess
