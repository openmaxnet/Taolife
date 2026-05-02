/**
 * 请求封装
 * 封装 uni.request，统一处理响应、认证错误、Token 携带等
 */

import { getToken } from './storage'
import { BaseExceptionCodeMsg } from './constants'
import type { ExceptionResult } from './result'
import type { RequestOptions } from './types'

let _getBaseUrl: () => string = () => ''
let _handleAuthError: (() => Promise<boolean>) | null = null

/**
 * 配置请求模块（项目入口调用一次）
 */
export const configureRequest = (options: {
  getBaseUrl: () => string
  handleAuthError?: () => Promise<boolean>
}) => {
  _getBaseUrl = options.getBaseUrl
  _handleAuthError = options.handleAuthError ?? null
}

export const request = <T = any>(options: RequestOptions) => {
  const {
    url,
    method = 'GET',
    data,
    loading = false,
    showError = true,
    header = {},
    _isRetry = false,
  } = options as RequestOptions & { _isRetry?: boolean }

  const fullUrl = url.startsWith('http') ? url : _getBaseUrl() + url

  const requestHeader: Record<string, string> = {
    'Content-Type': 'application/json',
    ...header,
  }

  const token = getToken()
  if (token) {
    requestHeader['Authorization'] = `Bearer ${token}`
  }

  if (loading) {
    uni.showLoading({ title: '加载中...', mask: true })
  }

  return new Promise<T>((resolve, reject) => {
    uni.request({
      url: fullUrl,
      method,
      data,
      header: requestHeader,
      timeout: 15000,
      success: async (res) => {
        if (loading) uni.hideLoading()

        if (res.statusCode >= 200 && res.statusCode < 300) {
          const responseData = res.data as ExceptionResult<T>

          if (responseData.code && responseData.code.startsWith('S')) {
            resolve(responseData.data)
          } else if (responseData.code && responseData.code.startsWith('F')) {
            if (showError) {
              uni.showToast({
                title: responseData.msg || BaseExceptionCodeMsg[responseData.code] || '请求失败',
                icon: 'none',
              })
            }
            reject(responseData)
          } else if (responseData.code && responseData.code.startsWith('E')) {
            if (showError) {
              uni.showToast({
                title: responseData.msg || BaseExceptionCodeMsg[responseData.code] || '系统错误',
                icon: 'none',
              })
            }
            reject(responseData)
          } else {
            resolve(responseData as any)
          }
        } else if (res.statusCode === 401) {
          if (_isRetry) {
            uni.$emit('authExpired')
            reject({ code: 'F12000', msg: '请先登录' })
            return
          }

          if (_handleAuthError) {
            const retrySuccess = await _handleAuthError()
            if (retrySuccess) {
              try {
                const retryResult = await request<T>({
                  ...options,
                  _isRetry: true,
                  loading: false,
                } as any)
                resolve(retryResult)
              } catch (retryErr) {
                reject(retryErr)
              }
            } else {
              reject({ code: 'F12000', msg: '请先登录' })
            }
          } else {
            reject({ code: 'F12000', msg: '请先登录' })
          }
        } else if (res.statusCode === 403) {
          if (showError) uni.showToast({ title: '权限不足', icon: 'none' })
          reject({ code: 'F12007', msg: '权限不足' })
        } else if (res.statusCode >= 500) {
          if (showError) uni.showToast({ title: '服务器错误', icon: 'none' })
          reject({ code: 'E20001', msg: '系统错误' })
        } else {
          if (showError) uni.showToast({ title: '请求失败', icon: 'none' })
          reject(res.data)
        }
      },
      fail: (err) => {
        if (loading) uni.hideLoading()
        if (showError) uni.showToast({ title: '网络错误，请检查网络连接', icon: 'none' })
        reject(err)
      },
    })
  })
}

export const get = <T = any>(url: string, data?: any, options?: Partial<RequestOptions>) => {
  const filteredData = data ? Object.fromEntries(
    Object.entries(data).filter(([_, value]) => value !== undefined)
  ) : undefined

  return request<T>({
    url,
    method: 'GET',
    data: filteredData,
    ...options,
  })
}

export const post = <T = any>(url: string, data?: any, options?: Partial<RequestOptions>) => {
  return request<T>({
    url,
    method: 'POST',
    data,
    ...options,
  })
}
