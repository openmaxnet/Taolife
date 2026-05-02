/**
 * 文章类型定义
 * 对应后端 ArticleListVO 和 ArticleDetailVO
 */

/**
 * 文章列表项
 */
export interface ArticleListItem {
  /** 文章ID */
  id: string;
  /** 标题 */
  title: string;
  /** 副标题 */
  subtitle: string;
  /** 分类：1-养生方法，2-四季养生，3-节气养生，4-食疗方案，5-中医知识 */
  category: number;
  /** 分类名称 */
  categoryName: string;
  /** 标签（JSON数组） */
  tags: string;
  /** 封面图 */
  coverImageUrl: string;
  /** 摘要 */
  summary: string;
  /** 作者 */
  author: string;
  /** 来源 */
  source: string;
  /** 阅读数 */
  readCount: number;
  /** 点赞数 */
  likeCount: number;
  /** 收藏数 */
  collectCount: number;
  /** 是否推荐：0-否，1-是 */
  isRecommended: number;
  /** 是否精选：0-否，1-是 */
  isFeatured: number;
  /** 发布时间 */
  publishTime: string;
}

/**
 * 文章详情
 */
export interface ArticleDetail extends ArticleListItem {
  /** 正文内容 */
  content: string;
  /** 内容类型：1-Markdown，2-HTML */
  contentType: number;
  /** 关联体质编码 */
  relatedConstitutionCodes: string;
  /** 关联季节：1-春，2-夏，3-秋，4-冬 */
  relatedSeason: number;
  /** 关联节气 */
  relatedSolarTerm: string;
  /** 分享数 */
  shareCount: number;
}

/**
 * 文章查询参数
 */
export interface ArticleQueryParam {
  /** 分类：0-全部，1-养生方法，2-四季养生，3-节气养生，4-食疗方案，5-中医知识 */
  category?: number;
  /** 是否推荐：0-否，1-是 */
  isRecommended?: number;
  /** 是否精选：0-否，1-是 */
  isFeatured?: number;
  /** 关键词搜索（标题） */
  keyword?: string;
  /** 页码，默认1 */
  pageNo?: number;
  /** 每页数量，默认10 */
  pageSize?: number;
}

/**
 * 文章列表响应（分页）
 */
export interface ArticlePageResult {
  /** 数据列表 */
  list: ArticleListItem[];
  /** 总数 */
  total: number;
  /** 当前页码 */
  pageNo: number;
  /** 每页数量 */
  pageSize: number;
}
