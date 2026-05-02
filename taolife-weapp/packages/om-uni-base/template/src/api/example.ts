/**
 * 示例 API 文件
 * 展示如何使用 @om/uni-base 的 request 封装
 */

import { get, post } from '@om/uni-base'
import type { PageResult } from '@om/uni-base'

export interface Article {
  id: string
  title: string
  content: string
  createdAt: string
}

export const getArticleList = (page: number, pageSize: number) =>
  get<PageResult<Article>>('/api/article/list', { page, pageSize })

export const getArticleDetail = (id: string) =>
  get<Article>('/api/article/detail', { id })

export const createArticle = (data: Partial<Article>) =>
  post<Article>('/api/article/create', data)
