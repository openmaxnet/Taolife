/**
 * 统一响应结果类型
 * 与后端 ExceptionResult<T> 对应
 */

export interface ExceptionResult<T = any> {
  /** 响应状态码 */
  code: string;
  /** 响应消息 */
  msg: string;
  /** 响应数据 */
  data: T;
}
