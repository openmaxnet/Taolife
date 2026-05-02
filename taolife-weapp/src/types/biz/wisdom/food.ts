/**
 * 食材类型定义
 * 对应后端 FoodListVO 和 FoodDetailVO
 */

/**
 * 食材列表项
 */
export interface FoodListItem {
  /** 食材ID */
  id: string;
  /** 食材名称 */
  name: string;
  /** 拼音 */
  namePinyin: string;
  /** 分类：1-谷物，2-蔬菜，3-水果，4-肉类，5-药材 */
  category: number;
  /** 分类名称 */
  categoryName: string;
  /** 性质：1-寒，2-凉，3-平，4-温，5-热 */
  nature: number;
  /** 性质名称 */
  natureName: string;
  /** 味道 */
  flavor: string;
  /** 归经 */
  meridianEntry: string;
  /** 功效（简要） */
  efficacy: string;
  /** 图片 */
  imageUrl: string;
  /** 浏览次数 */
  viewCount: number;
  /** 收藏次数 */
  collectCount: number;
}

/**
 * 食材详情
 */
export interface FoodDetail extends FoodListItem {
  /** 适宜人群/症状 */
  indications: string;
  /** 禁忌人群 */
  contraindications: string;
  /** 用法用量 */
  usage: string;
  /** 养生食谱 */
  recipes: string;
}

/**
 * 食材查询参数
 */
export interface FoodQueryParam {
  /** 分类：1-谷物，2-蔬菜，3-水果，4-肉类，5-药材 */
  category?: number;
  /** 性质：1-寒，2-凉，3-平，4-温，5-热 */
  nature?: number;
  /** 味道 */
  flavor?: string;
  /** 归经 */
  meridianEntry?: string;
  /** 关键词搜索（名称或拼音） */
  keyword?: string;
  /** 页码，默认1 */
  page?: number;
  /** 每页数量，默认10 */
  pageSize?: number;
}

/**
 * 食材列表响应（分页）
 */
export interface FoodPageResult {
  /** 数据列表 */
  list: FoodListItem[];
  /** 总数 */
  total: number;
  /** 当前页码 */
  page: number;
  /** 每页数量 */
  pageSize: number;
}
