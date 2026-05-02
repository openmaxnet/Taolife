/**
 * 请求相关类型定义
 */

export interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'OPTIONS'
  data?: any
  loading?: boolean
  showError?: boolean
  header?: Record<string, string>
}

export interface PageParam {
  page: number
  pageSize: number
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPage: number
}
