/// <reference path="../global.d.ts"/>

namespace Entity {
  interface AcupointCombine {
    id?: string
    name?: string
    category?: number
    targetConstitutionCodes?: string
    targetSeason?: string
    targetSymptom?: string
    description?: string
    acupointIds?: string
    acupointNames?: string
    sequence?: number
    operationMethod?: string
    durationMin?: number
    frequencyPerDay?: number
    efficacy?: string
    indications?: string
    contraindications?: string
    imageUrl?: string
    videoUrl?: string
    viewCount?: number
    collectCount?: number
    sortOrder?: number
    isDisabled?: 0 | 1
    createTime?: string
  }

  interface AcupointCombinePageParam {
    pageNo?: number
    pageSize?: number
    category?: number
    keyword?: string
  }

  interface AcupointCombineSaveParam {
    name: string
    category?: number
    targetConstitutionCodes?: string
    targetSeason?: string
    targetSymptom?: string
    description?: string
    acupointIds?: string
    acupointNames?: string
    sequence?: number
    operationMethod?: string
    durationMin?: number
    frequencyPerDay?: number
    efficacy?: string
    indications?: string
    contraindications?: string
    imageUrl?: string
    videoUrl?: string
    sortOrder?: number
  }
}
