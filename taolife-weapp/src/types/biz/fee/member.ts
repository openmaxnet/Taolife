export interface MemberPlanVO {
  id: string
  planCode: string
  planName: string
  memberLevel: number
  durationDays: number
  originalPrice: number
  currentPrice: number
  discountLabel: string
  aiDailyQuota: number
  maxActivePlans: number
  maxCycleDays: number
  maxAdjustments: number
  assessmentMonthlyQuota: number
  pointsMultiplier: number
  storeDiscount: number
  pointsToYuanRatio: number
  benefitsJson: string
  sortOrder: number
  isEnabled: number
  createTime: string
}

export interface MemberStatusVO {
  isActiveMember: boolean
  memberLevel: number
  memberLevelName: string
  expireTime: string
  remainDays: number
  aiQuotaUsed: number
  aiQuotaTotal: number
  aiQuotaRemaining: number
  maxActivePlans: number
  currentActivePlans: number
  pointsMultiplier: number
  storeDiscount: number
  pointsToYuanRatio: number
  growthValue: number
  growthLevel: number
  growthLevelName: string
  bonusAiQuota: number
  bonusPointsMultiplier: number
  bonusStoreDiscount: number
}

export interface MemberGrowthStatusVO {
  growthValue: number
  growthLevel: number
  growthLevelName: string
  currentLevelMinGrowth: number
  nextLevelMinGrowth: number | null
  progressPercent: number
  bonusAiQuota: number
  bonusPointsMultiplier: number
  bonusStoreDiscount: number
  privilege: string
}

export interface MemberGrowthRecordVO {
  id: string
  growthChange: number
  growthSource: number
  businessType: string
  remark: string
  growthValueAfter: number
  createTime: string
}

export interface GrowthLevelVO {
  id: string
  level: number
  levelName: string
  minGrowthValue: number
  bonusAiQuota: number
  bonusPointsMultiplier: number
  bonusStoreDiscount: number
  privilege: string
  currentLevel: boolean
}

export interface GrowthDetailVO {
  growthValue: number
  growthLevel: number
  levels: GrowthLevelVO[]
}

export interface CreateOrderVO {
  orderNo: string
  prepayId: string
  payParams: string
  timeStamp: string
  nonceStr: string
  packageStr: string
  signType: string
  paySign: string
}

export interface PayOrderVO {
  id: string
  orderNo: string
  accountId: string
  memberPlanId: string
  planCode: string
  planName: string
  memberLevel: number
  durationDays: number
  orderAmount: number
  payAmount: number
  status: number
  statusDesc: string
  wxPrepayId: string
  wxTransactionId: string
  paidTime: string
  expireTime: string
  createTime: string
}
