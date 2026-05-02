/// <reference path="../global.d.ts"/>

namespace Entity {
  interface Exercise {
    id?: string
    name?: string
    namePinyin?: string
    category?: number
    intensity?: number
    targetConstitutionCodes?: string
    contraConstitutionCodes?: string
    targetSeason?: string
    targetAgeGroup?: string
    efficacy?: string
    indications?: string
    contraindications?: string
    description?: string
    steps?: string
    durationMin?: number
    caloriesConsumption?: number
    difficultyLevel?: number
    videoUrl?: string
    imageUrl?: string
    viewCount?: number
    collectCount?: number
    sortOrder?: number
    isDisabled?: 0 | 1
    createTime?: string
  }

  interface ExercisePageParam {
    pageNo?: number
    pageSize?: number
    category?: number
    keyword?: string
  }

  interface ExerciseSaveParam {
    name: string
    namePinyin?: string
    category?: number
    intensity?: number
    targetConstitutionCodes?: string
    contraConstitutionCodes?: string
    targetSeason?: string
    targetAgeGroup?: string
    efficacy?: string
    indications?: string
    contraindications?: string
    description?: string
    steps?: string
    durationMin?: number
    caloriesConsumption?: number
    difficultyLevel?: number
    videoUrl?: string
    imageUrl?: string
    sortOrder?: number
  }
}
