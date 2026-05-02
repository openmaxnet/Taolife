/**
 * 认证管理器工厂
 * 支持可插拔登录策略，通过 createAuthManager 创建实例
 */

import { ref, type Ref } from 'vue'
import {
  getToken, setToken, getRefreshToken, setRefreshToken,
  clearUserInfo, setTokenExpiry, getTokenExpiry,
} from '../core/storage'

export interface AuthTokens {
  accessToken: string
  refreshToken: string
}

export interface AuthManagerOptions {
  loginFn: () => Promise<AuthTokens>
  refreshFn: (refreshToken: string) => Promise<AuthTokens | null>
  onLoginExpired?: () => void
}

export interface AuthManager {
  isAuth: Ref<boolean>
  enAuth: () => Promise<boolean>
  handleAuthError: () => Promise<boolean>
  launchSilentLogin: () => Promise<boolean>
  checkLocalAuthState: () => void
  onLoginSuccess: (callback?: () => void) => void
}

const TOKEN_LIFETIME_MS = 7 * 24 * 60 * 60 * 1000
const EXPIRY_BUFFER_MS = 5 * 60 * 1000

export const createAuthManager = (options: AuthManagerOptions): AuthManager => {
  const isAuth = ref(false)
  let loginPromise: Promise<boolean> | null = null
  let launchLoginPromise: Promise<boolean> | null = null

  const isTokenNearExpiry = (): boolean => {
    const expiry = getTokenExpiry()
    if (!expiry) return true
    return Date.now() > expiry - EXPIRY_BUFFER_MS
  }

  const handleAuthError = async (): Promise<boolean> => {
    isAuth.value = false
    clearUserInfo()
    const success = await doLogin()
    if (!success) {
      options.onLoginExpired?.()
      uni.$emit('authExpired')
    }
    return success
  }

  const doLogin = async (): Promise<boolean> => {
    const currentRefreshToken = getRefreshToken()

    if (currentRefreshToken) {
      try {
        const result = await options.refreshFn(currentRefreshToken)
        if (result) {
          setToken(result.accessToken)
          setRefreshToken(result.refreshToken)
          setTokenExpiry(Date.now() + TOKEN_LIFETIME_MS)
          isAuth.value = true
          uni.$emit('authLoginSuccess')
          return true
        }
      } catch (e) {
        console.warn('Token refresh failed, falling back to login:', e)
        clearUserInfo()
      }
    }

    return doFullLogin()
  }

  const doFullLogin = async (): Promise<boolean> => {
    try {
      uni.showToast({ title: '登录已失效，正在重新登录', icon: 'none', duration: 1500 })
      const result = await options.loginFn()
      setToken(result.accessToken)
      setRefreshToken(result.refreshToken)
      setTokenExpiry(Date.now() + TOKEN_LIFETIME_MS)
      isAuth.value = true
      uni.showToast({ title: '登录成功', icon: 'success', duration: 1500 })
      uni.$emit('authLoginSuccess')
      return true
    } catch (e) {
      console.error('Login failed:', e)
      isAuth.value = false
      return false
    }
  }

  const ensureLogin = async (): Promise<boolean> => {
    if (isAuth.value && !isTokenNearExpiry()) return true
    if (loginPromise) return loginPromise

    loginPromise = doLogin()
    try {
      return await loginPromise
    } finally {
      loginPromise = null
    }
  }

  const checkLocalAuthState = (): void => {
    const token = getToken()
    isAuth.value = !!(token && !isTokenNearExpiry())
  }

  const enAuth = async (): Promise<boolean> => {
    if (launchLoginPromise) return launchLoginPromise
    if (isAuth.value && !isTokenNearExpiry()) return true
    return ensureLogin()
  }

  const launchSilentLogin = (): Promise<boolean> => {
    if (launchLoginPromise) return launchLoginPromise

    if (loginPromise) {
      launchLoginPromise = loginPromise
    } else {
      loginPromise = doLogin()
      launchLoginPromise = loginPromise
      loginPromise.finally(() => { loginPromise = null })
    }
    launchLoginPromise.finally(() => { launchLoginPromise = null })
    return launchLoginPromise
  }

  const onLoginSuccess = (callback?: () => void) => {
    isAuth.value = true
    callback?.()
  }

  uni.$on('authExpired', () => { isAuth.value = false })
  uni.$on('authLoginSuccess', () => { isAuth.value = true })

  return { isAuth, enAuth, handleAuthError, launchSilentLogin, checkLocalAuthState, onLoginSuccess }
}
