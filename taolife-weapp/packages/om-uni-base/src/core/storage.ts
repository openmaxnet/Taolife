/**
 * Token Storage 工具
 * Key 前缀可配置，默认 om_
 */

const DEFAULT_PREFIX = 'om_'
let keyPrefix = DEFAULT_PREFIX

const TOKEN_KEY = () => `${keyPrefix}access_token`
const REFRESH_TOKEN_KEY = () => `${keyPrefix}refresh_token`
const TOKEN_EXPIRY_KEY = () => `${keyPrefix}access_token_expiry`

export const setStorageKeyPrefix = (prefix: string) => {
  keyPrefix = prefix
}

export const setToken = (token: string): void => {
  uni.setStorageSync(TOKEN_KEY(), token)
}

export const getToken = (): string | undefined => {
  return uni.getStorageSync(TOKEN_KEY())
}

export const removeToken = (): void => {
  uni.removeStorageSync(TOKEN_KEY())
}

export const setRefreshToken = (token: string): void => {
  uni.setStorageSync(REFRESH_TOKEN_KEY(), token)
}

export const getRefreshToken = (): string | undefined => {
  return uni.getStorageSync(REFRESH_TOKEN_KEY())
}

export const removeRefreshToken = (): void => {
  uni.removeStorageSync(REFRESH_TOKEN_KEY())
}

export const clearUserInfo = (): void => {
  removeToken()
  removeRefreshToken()
  removeTokenExpiry()
}

export const setTokenExpiry = (expiryMs: number): void => {
  uni.setStorageSync(TOKEN_EXPIRY_KEY(), String(expiryMs))
}

export const getTokenExpiry = (): number | undefined => {
  const val = uni.getStorageSync(TOKEN_EXPIRY_KEY())
  return val ? Number(val) : undefined
}

export const removeTokenExpiry = (): void => {
  uni.removeStorageSync(TOKEN_EXPIRY_KEY())
}
