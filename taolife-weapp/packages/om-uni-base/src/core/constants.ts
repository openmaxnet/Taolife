/**
 * 通用错误码（跨项目复用）
 * 成功 S，业务失败 F，系统错误 E
 */
export const BaseExceptionCode = {
  SUCCESS: 'S00001',
  CREATED: 'S00002',
  UPDATED: 'S00003',
  DELETED: 'S00004',
  QUERY_SUCCESS: 'S00005',

  BUSINESS_ERROR: 'F10001',
  PARAM_ERROR: 'F10002',
  DATA_NOT_FOUND: 'F10003',
  DATA_ALREADY_EXISTS: 'F10004',
  OPERATION_NOT_ALLOWED: 'F10006',

  USER_NOT_FOUND: 'F11001',
  USER_ALREADY_EXISTS: 'F11002',
  USER_DISABLED: 'F11003',
  USER_LOCKED: 'F11004',
  PASSWORD_ERROR: 'F11005',
  OLD_PASSWORD_ERROR: 'F11006',
  PASSWORD_NOT_MATCH: 'F11007',

  UNAUTHORIZED: 'F12000',
  LOGIN_FAILED: 'F12001',
  TOKEN_INVALID: 'F12002',
  TOKEN_EXPIRED: 'F12003',
  TOKEN_NOT_FOUND: 'F12004',
  REFRESH_TOKEN_INVALID: 'F12005',
  REFRESH_TOKEN_EXPIRED: 'F12006',
  PERMISSION_DENIED: 'F12007',
  ACCESS_DENIED: 'F12008',
  REFRESH_TOKEN_REQUIRED: 'F12009',
  NOT_LOGGED_IN: 'F12010',

  SYSTEM_ERROR: 'E20001',
  DATABASE_ERROR: 'E20002',
  NETWORK_ERROR: 'E20003',
  CONFIG_ERROR: 'E20004',
  EXTERNAL_SERVICE_ERROR: 'E21001',
  THIRD_PARTY_API_ERROR: 'E21002',
  VALIDATION_ERROR: 'E23001',
  REQUIRED_PARAM_MISSING: 'E23002',
  INVALID_PARAM_FORMAT: 'E23003',
  PARAM_OUT_OF_RANGE: 'E23004',
  UNKNOWN_ERROR: 'E99999',
} as const

export const BaseExceptionCodeMsg: Record<string, string> = {
  [BaseExceptionCode.SUCCESS]: '请求成功',
  [BaseExceptionCode.CREATED]: '创建成功',
  [BaseExceptionCode.UPDATED]: '更新成功',
  [BaseExceptionCode.DELETED]: '删除成功',
  [BaseExceptionCode.QUERY_SUCCESS]: '查询成功',
  [BaseExceptionCode.BUSINESS_ERROR]: '业务处理失败',
  [BaseExceptionCode.PARAM_ERROR]: '参数错误',
  [BaseExceptionCode.DATA_NOT_FOUND]: '数据不存在',
  [BaseExceptionCode.DATA_ALREADY_EXISTS]: '数据已存在',
  [BaseExceptionCode.OPERATION_NOT_ALLOWED]: '操作不允许',
  [BaseExceptionCode.USER_NOT_FOUND]: '用户名或密码错误',
  [BaseExceptionCode.USER_ALREADY_EXISTS]: '注册失败，请重试',
  [BaseExceptionCode.USER_DISABLED]: '账号已禁用',
  [BaseExceptionCode.USER_LOCKED]: '账号已锁定',
  [BaseExceptionCode.PASSWORD_ERROR]: '用户名或密码错误',
  [BaseExceptionCode.OLD_PASSWORD_ERROR]: '原密码错误',
  [BaseExceptionCode.PASSWORD_NOT_MATCH]: '两次密码不一致',
  [BaseExceptionCode.UNAUTHORIZED]: '请先登录',
  [BaseExceptionCode.LOGIN_FAILED]: '用户名或密码错误',
  [BaseExceptionCode.TOKEN_INVALID]: '登录已失效，请重新登录',
  [BaseExceptionCode.TOKEN_EXPIRED]: '登录已过期，请重新登录',
  [BaseExceptionCode.TOKEN_NOT_FOUND]: '请先登录',
  [BaseExceptionCode.REFRESH_TOKEN_INVALID]: '登录已失效，请重新登录',
  [BaseExceptionCode.REFRESH_TOKEN_EXPIRED]: '登录已过期，请重新登录',
  [BaseExceptionCode.PERMISSION_DENIED]: '权限不足',
  [BaseExceptionCode.ACCESS_DENIED]: '访问被拒绝',
  [BaseExceptionCode.REFRESH_TOKEN_REQUIRED]: '刷新令牌不能为空',
  [BaseExceptionCode.NOT_LOGGED_IN]: '未登录，请先登录',
  [BaseExceptionCode.SYSTEM_ERROR]: '系统错误',
  [BaseExceptionCode.DATABASE_ERROR]: '数据库错误',
  [BaseExceptionCode.NETWORK_ERROR]: '网络错误',
  [BaseExceptionCode.CONFIG_ERROR]: '配置错误',
  [BaseExceptionCode.EXTERNAL_SERVICE_ERROR]: '外部服务错误',
  [BaseExceptionCode.THIRD_PARTY_API_ERROR]: '第三方API错误',
  [BaseExceptionCode.VALIDATION_ERROR]: '参数验证失败',
  [BaseExceptionCode.REQUIRED_PARAM_MISSING]: '缺少必需参数',
  [BaseExceptionCode.INVALID_PARAM_FORMAT]: '参数格式无效',
  [BaseExceptionCode.PARAM_OUT_OF_RANGE]: '参数超出范围',
  [BaseExceptionCode.UNKNOWN_ERROR]: '未知错误',
}

export const isSuccessCode = (code: string): boolean => code.startsWith('S')
export const isBusinessError = (code: string): boolean => code.startsWith('F')
export const isSystemError = (code: string): boolean => code.startsWith('E')

export const isAuthError = (code: string): boolean => {
  const authCodes = [
    BaseExceptionCode.UNAUTHORIZED,
    BaseExceptionCode.TOKEN_INVALID,
    BaseExceptionCode.TOKEN_EXPIRED,
    BaseExceptionCode.TOKEN_NOT_FOUND,
    BaseExceptionCode.REFRESH_TOKEN_INVALID,
    BaseExceptionCode.REFRESH_TOKEN_EXPIRED,
    BaseExceptionCode.ACCESS_DENIED,
    BaseExceptionCode.NOT_LOGGED_IN,
  ]
  return (authCodes as readonly string[]).includes(code)
}
