/// <reference path="../global.d.ts"/>

namespace Entity {
  interface Article {
    id?: string
    title?: string
    subtitle?: string
    category?: number
    categoryName?: string
    tags?: string
    coverImageUrl?: string
    summary?: string
    content?: string
    contentType?: number
    author?: string
    source?: string
    relatedConstitutionCodes?: string
    relatedSeason?: string
    relatedSolarTerm?: string
    readCount?: number
    likeCount?: number
    collectCount?: number
    shareCount?: number
    isRecommended?: 0 | 1
    isFeatured?: 0 | 1
    publishTime?: string
    sortOrder?: number
    status?: number
    isDisabled?: 0 | 1
    createTime?: string
  }

  interface ArticlePageParam {
    pageNo?: number
    pageSize?: number
    category?: number
    keyword?: string
  }

  interface ArticleSaveParam {
    title: string
    subtitle?: string
    category: number
    tags?: string
    coverImageUrl?: string
    summary?: string
    content?: string
    contentType?: number
    author?: string
    source?: string
    relatedConstitutionCodes?: string
    relatedSeason?: string
    relatedSolarTerm?: string
    isRecommended?: 0 | 1
    isFeatured?: 0 | 1
    publishTime?: string
    sortOrder?: number
    status?: number
  }
}
