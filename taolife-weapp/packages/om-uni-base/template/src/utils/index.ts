/**
 * Re-export @om/uni-base utilities
 * 页面中 import { xxx } from '@/utils' 即可使用
 */
export { request, get, post } from '@om/uni-base'
export { setStorageKeyPrefix, getToken, setToken, removeToken } from '@om/uni-base'
export { formatCount, parseTagsJson, parseTagsCsv, formatDate } from '@om/uni-base'
export { getCapsulePosition, getNavbarHeight, getNavbarTop } from '@om/uni-base'
export { BASE_URL } from './config'
export { isAuth, enAuth, handleAuthError, launchSilentLogin, checkLocalAuthState, onLoginSuccess } from './authManager'
