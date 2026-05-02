/// <reference path="../global.d.ts"/>

namespace Entity {
  // 方案广场
  interface PlanSquare {
    id?: string
    accountId?: string
    userPlanId?: string
    planType?: number
    nickname?: string
    avatarUrl?: string
    constitutionName?: string
    planSummary?: string
    planTags?: string
    completionRate?: number
    adjustmentCount?: number
    viewCount?: number
    likeCount?: number
    collectCount?: number
    commentCount?: number
    isOfficial?: number
    status?: number
    createTime?: string
  }

  // 评论
  interface PlanComment {
    id?: string
    accountId?: string
    planSquareId?: string
    nickname?: string
    avatarUrl?: string
    parentId?: string
    content?: string
    images?: string
    likeCount?: number
    replyCount?: number
    status?: number
    createTime?: string
  }

  // 方案任务
  interface PlanTask {
    id?: string
    userPlanId?: string
    planType?: number
    taskName?: string
    taskDescription?: string
    targetCount?: number
    completedCount?: number
    taskDate?: string
    taskCategory?: number
    priority?: number
    resourceType?: string
    resourceId?: string
    status?: number
    createTime?: string
  }

  // 方案调整记录
  interface PlanAdjustmentRecord {
    id?: string
    userPlanId?: string
    planType?: number
    adjustmentType?: number
    adjustmentReason?: string
    beforeContent?: string
    afterContent?: string
    aiModel?: string
    userFeedback?: string
    effectivenessScore?: number
    createTime?: string
  }

  // 周期报告
  interface PlanCycleReport {
    id?: string
    accountId?: string
    planId?: string
    newPlanId?: string
    reportContent?: string
    isRead?: number
    createTime?: string
  }

  // 分享记录
  interface PlanShareRecord {
    id?: string
    planHistoryId?: string
    accountId?: string
    shareType?: number
    shareText?: string
    shareImageUrl?: string
    viewCount?: number
    likeCount?: number
    collectCount?: number
    pointsAwarded?: number
    createTime?: string
  }

  // 用户偏好
  interface UserPreference {
    id?: string
    accountId?: string
    preferredFoodNature?: string
    dislikedFoods?: string
    allergicFoods?: string
    preferredExerciseType?: number
    preferredExerciseIntensity?: number
    preferredExerciseTime?: number
    preferredAcupoints?: string
    totalInteractions?: number
    lastLearnTime?: string
    createTime?: string
  }
}
