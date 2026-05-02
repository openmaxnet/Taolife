/// <reference path="../global.d.ts"/>

namespace Entity {
  interface Acupoint {
    id?: string
    name?: string
    namePinyin?: string
    category?: number
    categoryName?: string
    meridianCode?: string
    meridianName?: string
    locationDescription?: string
    efficacy?: string
    indications?: string
    operationMethod?: string
    massageTips?: string
    markerType?: number
    markerTypeName?: string
    sortOrder?: number
    isDisabled?: 0 | 1
    createTime?: string
  }

  interface AcupointPageParam {
    pageNo?: number
    pageSize?: number
    meridianCode?: string
    markerType?: number
    keyword?: string
  }

  interface AcupointSaveParam {
    name: string
    namePinyin?: string
    category?: number
    meridianCode: string
    meridianName?: string
    locationDescription?: string
    efficacy?: string
    indications?: string
    operationMethod?: string
    massageTips?: string
    markerType?: number
    sortOrder?: number
  }
}
