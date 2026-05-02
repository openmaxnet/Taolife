import { get, post } from '@/utils/request'
import type {
  PointsGoodsVO,
  PointsExchangeVO,
  PointsSummaryVO,
  QuotaStatusVO,
} from '@/types/biz/fee/points'

export const getPointsGoodsList = () =>
  get<PointsGoodsVO[]>('/api/fee/points/getGoodsList')

export const exchangePoints = (goodsCode: string) =>
  post<PointsExchangeVO>('/api/fee/points/exchange', { goodsCode })

export const getExchangeRecords = () =>
  get<PointsExchangeVO[]>('/api/fee/points/getExchangeRecords')

export const getPointsSummary = () =>
  get<PointsSummaryVO>('/api/fee/points/getPointsSummary')

export const getAiQuotaStatus = () =>
  get<QuotaStatusVO>('/api/ai/chat/getQuotaStatus')
