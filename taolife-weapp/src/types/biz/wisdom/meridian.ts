/**
 * 经络穴位类型定义
 * 对应后端 MeridianListVO、MeridianDetailVO、AcupointListVO、AcupointDetailVO
 */

// 经络类型

/**
 * 经络列表项
 */
export interface MeridianListItem {
  /** 经络ID */
  id: string;
  /** 经络编码 */
  code: string;
  /** 经络名称 */
  name: string;
  /** 拼音 */
  namePinyin: string;
  /** 分类：1-十二正经，2-奇经八脉 */
  category: number;
  /** 分类名称 */
  categoryName: string;
  /** 描述（简要） */
  description: string;
  /** 主治 */
  mainIndications: string;
  /** 线条颜色 */
  lineColor: string;
  /** 线条宽度 */
  lineWidth: number;
}

/**
 * 经络详情
 */
export interface MeridianDetail extends MeridianListItem {
  /** 循行描述 */
  pathDescription: string;
  /** 排序 */
  sortOrder: number;
}

/**
 * 经络查询参数
 */
export interface MeridianQueryParam {
  /** 分类：0-全部，1-十二正经，2-奇经八脉 */
  category?: number;
  /** 经络编码（精确匹配） */
  code?: string;
  /** 关键词搜索（名称模糊匹配） */
  keyword?: string;
  /** 页码，默认1 */
  pageNo?: number;
  /** 每页数量，默认10 */
  pageSize?: number;
}

/**
 * 经络列表响应（分页）
 */
export interface MeridianPageResult {
  /** 数据列表 */
  list: MeridianListItem[];
  /** 总数 */
  total: number;
  /** 当前页码 */
  page: number;
  /** 每页数量 */
  pageSize: number;
}

// 穴位类型

/**
 * 穴位列表项
 */
export interface AcupointListItem {
  /** 穴位ID */
  id: string;
  /** 穴位名称 */
  name: string;
  /** 拼音 */
  namePinyin: string;
  /** 分类：1-经穴 */
  category: number;
  /** 分类名称 */
  categoryName: string;
  /** 经络编码 */
  meridianCode: string;
  /** 经络名称 */
  meridianName: string;
  /** 定位描述 */
  locationDescription: string;
  /** 功效（简要） */
  efficacy: string;
  /** 标注类型：1-普通，2-重要，3-关键 */
  markerType: number;
  /** 标注类型名称 */
  markerTypeName: string;
}

/**
 * 穴位详情
 */
export interface AcupointDetail extends AcupointListItem {
  /** 主治 */
  indications: string;
  /** 操作方法 */
  operationMethod: string;
  /** 按摩提示 */
  massageTips: string;
  /** 排序 */
  sortOrder: number;
}

/**
 * 穴位查询参数
 */
export interface AcupointQueryParam {
  /** 经络编码（精确匹配） */
  meridianCode?: string;
  /** 标注类型：0-全部，1-普通，2-重要，3-关键 */
  markerType?: number;
  /** 关键词搜索（名称或拼音模糊匹配） */
  keyword?: string;
  /** 页码，默认1 */
  pageNo?: number;
  /** 每页数量，默认10 */
  pageSize?: number;
}

/**
 * 穴位列表响应（分页）
 */
export interface AcupointPageResult {
  /** 数据列表 */
  list: AcupointListItem[];
  /** 总数 */
  total: number;
  /** 当前页码 */
  page: number;
  /** 每页数量 */
  pageSize: number;
}