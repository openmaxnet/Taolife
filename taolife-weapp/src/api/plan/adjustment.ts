/**
 * 方案调整接口
 */

import { get, post } from '@/utils/request'
import type { PageResult } from '@/types/common/request'
import type {
  AdjustmentRecordItem,
  ManualAdjustmentParam,
  AdjustmentRecordQueryParam,
  AiAdjustParam,
  AiAdjustResult,
  BatchAiAdjustParam,
  BatchAiAdjustResult,
} from '@/types/biz/plan/plan'

/** 用户手动调整方案 */
export const manualAdjust = (params: ManualAdjustmentParam) => {
  return post('/api/plan/adjustment/manualAdjust', params)
}

/** 分页查询调整记录 */
export const getAdjustmentRecordPage = (userPlanId: string, params: AdjustmentRecordQueryParam) => {
  return get<PageResult<AdjustmentRecordItem>>('/api/plan/adjustment/getAdjustmentRecordPage', { userPlanId, ...params })
}

/** 获取智能调整推荐标签 */
export const getAdjustmentSuggestions = (userPlanId: string, planType: number) => {
  return get<string[]>('/api/plan/adjustment/suggestions', { userPlanId, planType }, { loading: false })
}

/** AI调整方案（单条） */
export const aiAdjust = (params: AiAdjustParam) => {
  return post<AiAdjustResult>('/api/plan/adjustment/aiAdjust', params)
}

/** AI批量调整方案 */
export const batchAiAdjust = (params: BatchAiAdjustParam) => {
  return post<BatchAiAdjustResult>('/api/plan/adjustment/batchAiAdjust', params)
}

/** 确认AI调整（单条） */
export const confirmAiAdjust = (adjustmentId: string) => {
  return post('/api/plan/adjustment/confirmAiAdjust', { adjustmentId })
}

/** 批量确认AI调整 */
export const batchConfirmAiAdjust = (adjustmentIds: string[]) => {
  return post('/api/plan/adjustment/batchConfirmAiAdjust', { adjustmentIds })
}
