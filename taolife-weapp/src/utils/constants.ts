/**
 * 统一响应状态码常量
 * 与后端 ExceptionCode 枚举对应
 * 成功状态码以S开头，失败响应使用F开头，错误响应以E开头
 */

/**
 * 通用错误码（跨项目复用）
 * 各项目可在此基础上扩展业务特有错误码
 */
export const BaseExceptionCode = {
  // 成功响应 (S开头)
  SUCCESS: 'S00001',
  CREATED: 'S00002',
  UPDATED: 'S00003',
  DELETED: 'S00004',
  QUERY_SUCCESS: 'S00005',

  // 通用业务失败 (F 1xxxx)
  BUSINESS_ERROR: 'F10001',
  PARAM_ERROR: 'F10002',
  DATA_NOT_FOUND: 'F10003',
  DATA_ALREADY_EXISTS: 'F10004',
  OPERATION_NOT_ALLOWED: 'F10006',

  // 用户相关 (F 11xxx)
  USER_NOT_FOUND: 'F11001',
  USER_ALREADY_EXISTS: 'F11002',
  USER_DISABLED: 'F11003',
  USER_LOCKED: 'F11004',
  PASSWORD_ERROR: 'F11005',
  OLD_PASSWORD_ERROR: 'F11006',
  PASSWORD_NOT_MATCH: 'F11007',

  // 认证相关 (F 12xxx)
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

  // 系统错误 (E 2xxxx)
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

/**
 * TaoLife 业务错误码（在通用码基础上扩展）
 */
export const ExceptionCode = {
  ...BaseExceptionCode,

  // ──── TaoLife 业务特有错误码 ────

  // 验证码相关
  CAPTCHA_CREATE_ERROR: 'F400100',
  CAPTCHA_VALIDATE_ERROR: 'F400101',
  SMS_CAPTCHA_ERROR: 'F400102',
  RATE_LIMIT_EXCEEDED: 'F400103',
  REQUEST_TOO_FREQUENT: 'F400105',

  // 文件上传
  FILE_UPLOAD_SIZE_EXCEEDED: 'F400400',

  // Token
  TOKEN_VALID: 'F400302',
  TOKEN_MISMATCH: 'F400303',
  TOKEN_PROCESS_ERROR: 'E500300',

  // 邀请码
  INVITE_CODE_EXISTS: 'F400200',
  INVALID_INVITE_CODE: 'F400201',

  // 安全相关
  SECURITY_ERROR: 'E22001',
  AUTHENTICATION_ERROR: 'E22002',
  AUTHORIZATION_ERROR: 'E22003',
  ENCRYPTION_ERROR: 'E22004',

  // 文件相关
  FILE_UPLOAD_ERROR: 'E24001',
  FILE_DOWNLOAD_ERROR: 'E24002',
  FILE_NOT_FOUND: 'E24003',
  FILE_SIZE_EXCEEDED: 'E24004',
  FILE_TYPE_NOT_SUPPORTED: 'E24005',

  // 业务规则
  BUSINESS_RULE_VIOLATION: 'E25001',
  STATE_CONFLICT: 'E25002',
  CONCURRENT_MODIFICATION: 'E25003',
  RESOURCE_CONFLICT: 'E25004',
} as const

export type ExceptionCodeType = string;

/**
 * 状态码消息映射
 */
export const ExceptionCodeMsg: Record<string, string> = {
  // 成功响应
  [ExceptionCode.SUCCESS]: '请求成功',
  [ExceptionCode.CREATED]: '创建成功',
  [ExceptionCode.UPDATED]: '更新成功',
  [ExceptionCode.DELETED]: '删除成功',
  [ExceptionCode.QUERY_SUCCESS]: '查询成功',

  // 业务失败
  [ExceptionCode.BUSINESS_ERROR]: '业务处理失败',
  [ExceptionCode.PARAM_ERROR]: '参数错误',
  [ExceptionCode.DATA_NOT_FOUND]: '数据不存在',
  [ExceptionCode.DATA_ALREADY_EXISTS]: '数据已存在',
  [ExceptionCode.OPERATION_NOT_ALLOWED]: '操作不允许',

  // 用户相关
  [ExceptionCode.USER_NOT_FOUND]: '用户名或密码错误',
  [ExceptionCode.USER_ALREADY_EXISTS]: '注册失败，请重试',
  [ExceptionCode.USER_DISABLED]: '账号已禁用',
  [ExceptionCode.USER_LOCKED]: '账号已锁定',
  [ExceptionCode.PASSWORD_ERROR]: '用户名或密码错误',
  [ExceptionCode.OLD_PASSWORD_ERROR]: '原密码错误',
  [ExceptionCode.PASSWORD_NOT_MATCH]: '两次密码不一致',

  // 认证相关
  [ExceptionCode.UNAUTHORIZED]: '请先登录',
  [ExceptionCode.LOGIN_FAILED]: '用户名或密码错误',
  [ExceptionCode.TOKEN_INVALID]: '登录已失效，请重新登录',
  [ExceptionCode.TOKEN_EXPIRED]: '登录已过期，请重新登录',
  [ExceptionCode.TOKEN_NOT_FOUND]: '请先登录',
  [ExceptionCode.REFRESH_TOKEN_INVALID]: '登录已失效，请重新登录',
  [ExceptionCode.REFRESH_TOKEN_EXPIRED]: '登录已过期，请重新登录',
  [ExceptionCode.PERMISSION_DENIED]: '权限不足',
  [ExceptionCode.ACCESS_DENIED]: '访问被拒绝',
  [ExceptionCode.REFRESH_TOKEN_REQUIRED]: '刷新令牌不能为空',
  [ExceptionCode.NOT_LOGGED_IN]: '未登录，请先登录',

  // 验证码相关
  [ExceptionCode.CAPTCHA_CREATE_ERROR]: '图形验证码生成失败',
  [ExceptionCode.CAPTCHA_VALIDATE_ERROR]: '图形验证码验证失败',
  [ExceptionCode.SMS_CAPTCHA_ERROR]: '短信验证码发送失败',
  [ExceptionCode.RATE_LIMIT_EXCEEDED]: '请求过于频繁，请稍后再试',
  [ExceptionCode.REQUEST_TOO_FREQUENT]: '请求过于频繁，请稍后再试',

  // 文件上传相关
  [ExceptionCode.FILE_UPLOAD_SIZE_EXCEEDED]: '上传文件大小超过限制',

  // Token相关
  [ExceptionCode.TOKEN_VALID]: 'Token有效',
  [ExceptionCode.TOKEN_MISMATCH]: 'Token不匹配',
  [ExceptionCode.TOKEN_PROCESS_ERROR]: 'Token处理异常',

  // 邀请码相关
  [ExceptionCode.INVITE_CODE_EXISTS]: '已有未过期的邀请码',
  [ExceptionCode.INVALID_INVITE_CODE]: '无效的邀请码',

  // 系统错误
  [ExceptionCode.SYSTEM_ERROR]: '系统错误',
  [ExceptionCode.DATABASE_ERROR]: '数据库错误',
  [ExceptionCode.NETWORK_ERROR]: '网络错误',
  [ExceptionCode.CONFIG_ERROR]: '配置错误',

  // 外部服务错误
  [ExceptionCode.EXTERNAL_SERVICE_ERROR]: '外部服务错误',
  [ExceptionCode.THIRD_PARTY_API_ERROR]: '第三方API错误',

  // 安全相关错误
  [ExceptionCode.SECURITY_ERROR]: '操作失败',
  [ExceptionCode.AUTHENTICATION_ERROR]: '操作失败',
  [ExceptionCode.AUTHORIZATION_ERROR]: '操作失败',
  [ExceptionCode.ENCRYPTION_ERROR]: '数据处理失败',

  // 参数验证错误
  [ExceptionCode.VALIDATION_ERROR]: '参数验证失败',
  [ExceptionCode.REQUIRED_PARAM_MISSING]: '缺少必需参数',
  [ExceptionCode.INVALID_PARAM_FORMAT]: '参数格式无效',
  [ExceptionCode.PARAM_OUT_OF_RANGE]: '参数超出范围',

  // 文件相关错误
  [ExceptionCode.FILE_UPLOAD_ERROR]: '文件上传失败',
  [ExceptionCode.FILE_DOWNLOAD_ERROR]: '文件下载失败',
  [ExceptionCode.FILE_NOT_FOUND]: '文件不存在',
  [ExceptionCode.FILE_SIZE_EXCEEDED]: '文件大小超限',
  [ExceptionCode.FILE_TYPE_NOT_SUPPORTED]: '不支持的文件类型',

  // 业务规则错误
  [ExceptionCode.BUSINESS_RULE_VIOLATION]: '违反业务规则',
  [ExceptionCode.STATE_CONFLICT]: '状态冲突',
  [ExceptionCode.CONCURRENT_MODIFICATION]: '并发修改冲突',
  [ExceptionCode.RESOURCE_CONFLICT]: '资源冲突',

  // 未知错误
  [ExceptionCode.UNKNOWN_ERROR]: '未知错误',
};

/**
 * 检查是否为成功响应
 * @param code 状态码
 */
export const isSuccessCode = (code: string): boolean => code.startsWith('S')

/**
 * 检查是否为业务失败响应
 * @param code 状态码
 */
export const isBusinessError = (code: string): boolean => code.startsWith('F')

/**
 * 检查是否为系统错误响应
 * @param code 状态码
 */
export const isSystemError = (code: string): boolean => code.startsWith('E')

/**
 * 检查是否为认证错误（需要重新登录）
 * @param code 状态码
 */
export const isAuthError = (code: string): boolean => {
  const authErrorCodes: string[] = [
    ExceptionCode.UNAUTHORIZED,
    ExceptionCode.TOKEN_INVALID,
    ExceptionCode.TOKEN_EXPIRED,
    ExceptionCode.TOKEN_NOT_FOUND,
    ExceptionCode.REFRESH_TOKEN_INVALID,
    ExceptionCode.REFRESH_TOKEN_EXPIRED,
    ExceptionCode.ACCESS_DENIED,
    ExceptionCode.NOT_LOGGED_IN,
  ];
  return authErrorCodes.includes(code);
}
