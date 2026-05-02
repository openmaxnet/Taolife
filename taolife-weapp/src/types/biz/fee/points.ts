export interface PointsGoodsVO {
  id: string
  goodsCode: string
  goodsName: string
  goodsType: number
  value: number
  pointsRequired: number
  dailyLimit: number
  iconUrl: string
  description: string
}

export interface PointsExchangeVO {
  id: string
  goodsCode: string
  goodsName: string
  goodsType: number
  pointsCost: number
  exchangeValue: number
  status: number
  createTime: string
}

export interface PointsSummaryVO {
  availablePoints: number
  todayEarned: number
  userLevel: number
}

export interface QuotaStatusVO {
  used: number
  total: number
  remaining: number
  hasRemaining: boolean
}
