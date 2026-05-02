/// <reference path="../global.d.ts"/>

namespace Api {
  /** 分页结果 */
  interface PageResult<T> {
    /** 数据列表 */
    list: T[]
    /** 页码 */
    pageNo: number
    /** 每页大小 */
    pageSize: number
    /** 总页数 */
    totalPage: number
    /** 总记录数 */
    totalRow: number
  }
}
