import { get } from '@/utils/request'
import type { MemberPlanVO, MemberStatusVO, MemberGrowthStatusVO, MemberGrowthRecordVO, GrowthDetailVO } from '@/types/biz/fee/member'
import type { PageResult } from '@/types/common/request'

export const getMemberStatus = () =>
  get<MemberStatusVO>('/api/fee/member/getMemberStatus')

export const getEnabledPlans = () =>
  get<MemberPlanVO[]>('/api/fee/member/getEnabledPlans')

export const getGrowthStatus = () =>
  get<MemberGrowthStatusVO>('/api/fee/member/getGrowthStatus')

export const getGrowthRecords = (pageNo: number, pageSize: number) =>
  get<PageResult<MemberGrowthRecordVO>>(`/api/fee/member/getGrowthRecords?pageNo=${pageNo}&pageSize=${pageSize}`)

export const getGrowthDetail = () =>
  get<GrowthDetailVO>('/api/fee/member/getGrowthDetail')
