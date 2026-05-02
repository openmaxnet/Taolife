/// <reference path="../global.d.ts"/>

namespace Entity {
  interface UserPlan {
    id?: string
    accountId?: string
    constitutionName?: string
    season?: number
    seasonName?: string
    planTitle?: string
    status?: number
    completionRate?: number
    totalTasks?: number
    completedTasks?: number
    userRating?: number
    adjustmentCount?: number
    startDate?: string
    endDate?: string
    createTime?: string
  }

  interface UserPlanDetail extends UserPlan {
    constitutionCode?: string
    cycleDays?: number
    isEffective?: number
    aiAdjustmentCount?: number
    planTags?: string
    userNotes?: string
    userFeedback?: string
    foodPlanContent?: string
    exercisePlanContent?: string
    acupointPlanContent?: string
    meridianPlanContent?: string
    lifestylePlanContent?: string
    foodPlanTags?: string
    exercisePlanTags?: string
    acupointPlanTags?: string
    meridianPlanTags?: string
    lifestylePlanTags?: string
    updateTime?: string
  }
}
