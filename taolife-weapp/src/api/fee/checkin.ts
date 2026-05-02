/**
 * 签到接口
 */
import { get, post } from '@/utils/request'
import type { TodayCheckinStatus, CheckinCalendar } from '@/types/biz/fee/checkin'

export const getTodayCheckinStatus = async () => {
  return get<TodayCheckinStatus>('/api/fee/checkin/getTodayCheckinStatus')
}

export const dailyCheckin = async () => {
  return post<TodayCheckinStatus>('/api/fee/checkin/dailyCheckin')
}

export const getCheckinCalendar = async (param: { year: number; month: number }) => {
  return get<CheckinCalendar>('/api/fee/checkin/getCheckinCalendar', param)
}
