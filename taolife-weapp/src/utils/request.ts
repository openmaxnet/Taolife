/**
 * 请求封装
 * 封装 uni.request，统一处理响应、认证错误、Token 携带等
 */

import { getToken } from './storage';
import { ExceptionCodeMsg } from './constants';
import { BASE_URL } from './config';
import type { ExceptionResult, RequestOptions } from '../types';
import { handleAuthError } from './authManager';
import { encryptRequest, decryptResponse, isEncryptionEnabled } from './crypto';
import { miniAppMonitor } from './monitor';

/**
 * 发送请求
 * @param options 请求配置
 * @returns Promise
 */
export const request = <T = any>(options: RequestOptions) => {
  const {
    url,
    method = 'GET',
    data,
    loading = false,
    showError = true,
    header = {},
    _isRetry = false,
  } = options as RequestOptions & { _isRetry?: boolean };

  // 构建完整URL
  const fullUrl = url.startsWith('http') ? url : BASE_URL + url;

  // 构建请求头
  const requestHeader: Record<string, string> = {
    'Content-Type': 'application/json',
    ...header,
  };

  // 添加 Token
  const token = getToken();
  if (token) {
    requestHeader['Authorization'] = `Bearer ${token}`;
  }

  // 显示加载中
  if (loading) {
    uni.showLoading({ title: '加载中...', mask: true });
  }

  return new Promise<T>(async (resolve, reject) => {
    const startTime = Date.now();
    // 加密请求体
    let encryptedBody: any = data
    let currentAesKey = ''
    if (isEncryptionEnabled() && method === 'POST' && data) {
      try {
        const bodyStr = typeof data === 'string' ? data : JSON.stringify(data)
        const urlPath = new URL(url, 'http://localhost').pathname
        const { headers: encHeaders, body: encBody, aesKey } = await encryptRequest(method, urlPath, bodyStr)
        Object.assign(requestHeader, encHeaders)
        encryptedBody = encBody
        currentAesKey = aesKey
      } catch (e) {
        console.warn('请求加密失败，使用明文发送', e)
      }
    }

    uni.request({
      url: fullUrl,
      method,
      data: encryptedBody,
      header: requestHeader,
      timeout: 15000, // 15秒超时
      success: async (res) => {
        miniAppMonitor.recordApiRequest(url, method, Date.now() - startTime, res.statusCode);
        if (loading) {
          uni.hideLoading();
        }

        // 检查 HTTP 状态码
        if (res.statusCode >= 200 && res.statusCode < 300) {
          let responseData = res.data as ExceptionResult<T>;

          // 解密加密响应
          if (responseData?.encrypted && currentAesKey) {
            try {
              responseData = decryptResponse(responseData, currentAesKey) as ExceptionResult<T>;
            } catch (e) {
              console.warn('响应解密失败', e);
            }
          }

          // 检查业务响应状态码
          if (responseData.code && responseData.code.startsWith('S')) {
            // 成功响应
            resolve(responseData.data);
          } else if (responseData.code && responseData.code.startsWith('F')) {
            // 业务失败
            if (showError) {
              uni.showToast({
                title: responseData.msg || ExceptionCodeMsg[responseData.code] || '请求失败',
                icon: 'none',
              });
            }
            reject(responseData);
          } else if (responseData.code && responseData.code.startsWith('E')) {
            // 系统错误
            if (showError) {
              uni.showToast({
                title: responseData.msg || ExceptionCodeMsg[responseData.code] || '系统错误',
                icon: 'none',
              });
            }
            reject(responseData);
          } else {
            // 未知响应格式
            resolve(responseData as any);
          }
        } else if (res.statusCode === 401) {
          // 已是重试请求，不再重试
          if (_isRetry) {
            uni.$emit('authExpired');
            reject({ code: 'F12000', msg: '请先登录' });
            return;
          }

          // 尝试静默重新登录
          const retrySuccess = await handleAuthError();
          if (retrySuccess) {
            // 重试原请求
            try {
              const retryResult = await request<T>({
                ...options,
                _isRetry: true,
                loading: false,
              } as any);
              resolve(retryResult);
            } catch (retryErr) {
              reject(retryErr);
            }
          } else {
            reject({ code: 'F12000', msg: '请先登录' });
          }
        } else if (res.statusCode === 403) {
          // 权限不足
          if (showError) {
            uni.showToast({
              title: '权限不足',
              icon: 'none',
            });
          }
          reject({ code: 'F12007', msg: '权限不足' });
        } else if (res.statusCode >= 500) {
          // 服务器错误
          if (showError) {
            uni.showToast({
              title: '服务器错误',
              icon: 'none',
            });
          }
          reject({ code: 'E20001', msg: '系统错误' });
        } else {
          // 其他错误
          if (showError) {
            uni.showToast({
              title: '请求失败',
              icon: 'none',
            });
          }
          reject(res.data);
        }
      },
      fail: (err) => {
        if (loading) {
          uni.hideLoading();
        }

        // 网络错误
        if (showError) {
          uni.showToast({
            title: '网络错误，请检查网络连接',
            icon: 'none',
          });
        }
        reject(err);
      },
    });
  });
}

// 快捷请求方法

/**
 * GET 请求
 */
export const get = <T = any>(url: string, data?: any, options?: Partial<RequestOptions>) => {
  // 过滤掉值为 undefined 的参数
  const filteredData = data ? Object.fromEntries(
    Object.entries(data).filter(([_, value]) => value !== undefined)
  ) : undefined;

  return request<T>({
    url,
    method: 'GET',
    data: filteredData,
    ...options,
  });
};

/**
 * POST 请求
 */
export const post = <T = any>(url: string, data?: any, options?: Partial<RequestOptions>) => {
  return request<T>({
    url,
    method: 'POST',
    data,
    ...options,
  });
};
