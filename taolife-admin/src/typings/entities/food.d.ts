/// <reference path="../global.d.ts"/>

namespace Entity {
  interface Food {
    id?: string
    name?: string
    namePinyin?: string
    category?: number
    categoryName?: string
    nature?: number
    natureName?: string
    flavor?: string
    meridianEntry?: string
    efficacy?: string
    indications?: string
    contraindications?: string
    usage?: string
    recipes?: string
    imageUrl?: string
    viewCount?: number
    collectCount?: number
    sortOrder?: number
    isDisabled?: 0 | 1
    createTime?: string
  }

  interface FoodPageParam {
    pageNo?: number
    pageSize?: number
    category?: number
    nature?: number
    keyword?: string
  }

  interface FoodSaveParam {
    name: string
    namePinyin?: string
    category: number
    nature?: number
    flavor?: string
    meridianEntry?: string
    efficacy?: string
    indications?: string
    contraindications?: string
    usage?: string
    recipes?: string
    imageUrl?: string
    sortOrder?: number
  }
}
