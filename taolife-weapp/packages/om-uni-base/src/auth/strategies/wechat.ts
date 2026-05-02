/**
 * 微信小程序登录策略
 */

import type { AuthTokens } from '../createAuthManager'
import type { ExceptionResult } from '../../core/result'

const apiRequest = <T>(baseUrl: string, url: string, method: 'GET' | 'POST', data?: any, getToken?: () => string | undefined): Promise<T> => {
  const header: Record<string, string> = { 'Content-Type': 'application/json' }
  const token = getToken?.()
  if (token) header['Authorization'] = `Bearer ${token}`
  return new Promise((resolve, reject) => {
    uni.request({
      url: url.startsWith('http') ? url : baseUrl + url,
      method,
      data,
      header,
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

export interface WechatAuthOptions {
  baseUrl: string
  getToken: () => string | undefined
}

export const createWechatAuthStrategy = (options: WechatAuthOptions) => ({
  loginFn: async (): Promise<AuthTokens> => {
    const loginRes = await uni.login({ provider: 'weixin' })
    if (!loginRes?.code) throw new Error('wx.login failed: no code returned')
    return apiRequest<AuthTokens>(options.baseUrl, '/api/identity/account/wxLogin', 'POST', { code: loginRes.code })
  },
  refreshFn: (refreshToken: string): Promise<AuthTokens | null> =>
    apiRequest<AuthTokens>(options.baseUrl, '/api/identity/account/refreshToken', 'POST', { refreshToken }, options.getToken).catch(() => null),
})
