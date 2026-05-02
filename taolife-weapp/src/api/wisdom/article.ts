/**
 * 文章接口
 * 封装文章相关的API调用
 */

import { get } from '@/utils/request';
import type { ArticleQueryParam, ArticlePageResult, ArticleDetail } from '@/types/biz/wisdom/article';

/**
 * 获取文章列表
 * 根据条件分页获取文章信息列表
 *
 * @param param 查询参数
 * @returns 分页结果
 */
export const getArticlePage = async (param: ArticleQueryParam) => {
  return get<ArticlePageResult>('/api/wisdom/article/getArticlePage', param);
};

/**
 * 获取文章详情
 * 根据文章ID获取文章的详细信息
 *
 * @param id 文章ID
 * @returns 文章详情
 */
export const getArticleDetail = async (id: string) => {
  return get<ArticleDetail>('/api/wisdom/article/getArticleDetail', { id });
};
