/**
 * 统一响应结果类型
 */
export interface ExceptionResult<T = any> {
  code: string
  msg: string
  data: T
}
