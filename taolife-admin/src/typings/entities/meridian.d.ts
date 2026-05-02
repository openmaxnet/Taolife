/// <reference path="../global.d.ts"/>

namespace Entity {
  interface Meridian {
    id?: string
    code?: string
    name?: string
    namePinyin?: string
    category?: number
    categoryName?: string
    description?: string
    pathDescription?: string
    mainIndications?: string
    lineColor?: string
    lineWidth?: number
    sortOrder?: number
    isDisabled?: 0 | 1
    createTime?: string
  }

  interface MeridianPageParam {
    pageNo?: number
    pageSize?: number
    category?: number
    code?: string
    keyword?: string
  }

  interface MeridianSaveParam {
    code: string
    name: string
    namePinyin?: string
    category: number
    description?: string
    pathDescription?: string
    mainIndications?: string
    lineColor?: string
    lineWidth?: number
    sortOrder?: number
  }
}
