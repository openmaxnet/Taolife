/**
 * @om/uni-base 统一导出
 */

// Core
export { request, get, post, configureRequest } from './core/request'
export type { RequestOptions, PageParam, PageResult } from './core/types'
export type { ExceptionResult } from './core/result'
export { BaseExceptionCode, BaseExceptionCodeMsg, isSuccessCode, isBusinessError, isSystemError, isAuthError } from './core/constants'
export { setStorageKeyPrefix, setToken, getToken, removeToken, setRefreshToken, getRefreshToken, removeRefreshToken, clearUserInfo, setTokenExpiry, getTokenExpiry, removeTokenExpiry } from './core/storage'
export { formatCount, parseTagsJson, parseTagsCsv, formatDate } from './core/format'

// Auth
export { createAuthManager } from './auth/createAuthManager'
export type { AuthTokens, AuthManagerOptions, AuthManager } from './auth/createAuthManager'
export { createWechatAuthStrategy } from './auth/strategies/wechat'
export type { WechatAuthOptions } from './auth/strategies/wechat'

// Composables
export { usePageLayout } from './composables/usePageLayout'
export { useDetailLoader } from './composables/useDetailLoader'
export { useListLoader } from './composables/useListLoader'
export { useCachedResource } from './composables/useCachedResource'
export { useInteraction } from './composables/useInteraction'
export type { InteractionApi } from './composables/useInteraction'
export { useTheme } from './composables/useTheme'

// Platform
export { getCapsulePosition, getNavbarHeight, getNavbarTop, getRightContentStart, getContentPaddingTop, watchCapsulePosition } from './platform/capsule'
export type { CapsulePosition } from './platform/capsule'
