/**
 * 请求相关类型定义
 */

/**
 * 请求配置选项
 */
export interface RequestOptions {
  /** 请求URL */
  url: string;
  /** 请求方法 */
  method?: 'GET' | 'POST' | 'OPTIONS';
  /** 请求数据 */
  data?: any;
  /** 是否显示加载中 */
  loading?: boolean;
  /** 是否显示错误提示 */
  showError?: boolean;
  /** 自定义请求头 */
  header?: Record<string, string>;
}

/**
 * 分页请求参数
 */
export interface PageParam {
  /** 当前页码 */
  page: number;
  /** 每页条数 */
  pageSize: number;
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  /** 数据列表 */
  list: T[];
  /** 总条数 */
  total: number;
  /** 当前页码 */
  page: number;
  /** 每页条数 */
  pageSize: number;
  /** 总页数 */
  totalPage: number;
}
