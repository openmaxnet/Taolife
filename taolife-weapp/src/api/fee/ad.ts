import { get, post } from '@/utils/request'
import type { AdConfigVO, AdActionParam } from '@/types/biz/fee/ad'

export const getAdConfigs = () =>
  get<AdConfigVO[]>('/api/fee/ad/getAdConfigs')

export const reportAdAction = (data: AdActionParam) =>
  post<void>('/api/fee/ad/reportAdAction', data)
