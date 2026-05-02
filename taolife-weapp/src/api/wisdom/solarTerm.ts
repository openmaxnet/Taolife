import { get } from '@/utils/request'
import type { SolarTermListItem, SolarTermDetail } from '@/types/biz/wisdom/solarTerm'

/** 获取24节气列表 */
export const getSolarTermList = async () => {
  return get<SolarTermListItem[]>('/api/wisdom/solarterm/getSolarTermList')
}

/** 获取节气详情 */
export const getSolarTermDetail = async (id: string) => {
  return get<SolarTermDetail>('/api/wisdom/solarterm/getSolarTermDetail', { id })
}

/** 获取当前节气 */
export const getCurrentSolarTerm = async () => {
  return get<SolarTermDetail>('/api/wisdom/solarterm/getCurrentSolarTerm')
}
