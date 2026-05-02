/**
 * 养生方案接口
 */

import { get, post } from '@/utils/request'
import type { PageResult } from '@/types/common/request'
import type {
  HealthPlanDetail,
  PlanSummary,
  GeneratePlanVO,
  GenerationStatusVO,
  PlanFeedbackParam,
  HealthPlanQueryParam,
  PlanHistoryItem,
  PlanSquareItem,
  PlanSquareDetail,
  PlanSquareQueryParam,
  PlanSquareActionParam,
  PlanSquareShareParam,
} from '@/types/biz/plan/plan'

/** 获取养生方案（最新） */
export const getHealthPlan = () => {
  return get<PlanSummary>('/api/plan/getHealthPlan')
}

/** 获取方案详情 */
export const getHealthPlanDetail = (id: string) => {
  return get<HealthPlanDetail>('/api/plan/getHealthPlanDetail', { id })
}

/** 分页查询方案历史 */
export const getPlanHistoryPage = (params: HealthPlanQueryParam) => {
  return get<PageResult<PlanHistoryItem>>('/api/plan/getPlanHistoryPage', params)
}

/** 提交方案生成请求（体质、季节、偏好由后端自动获取） */
export const generatePlan = (cycleDays?: number) => {
  return post<GeneratePlanVO>('/api/plan/generate', cycleDays ? { cycleDays } : {})
}

/** 查询生成任务状态 */
export const getGenerationStatus = (taskId: string) => {
  return get<GenerationStatusVO>('/api/plan/getGenerationStatus', { taskId })
}

/** 获取未读的周期报告 */
export const getUnreadCycleReport = () => {
  return get<{ id: string; planId: string; newPlanId: string; reportContent: string } | null>('/api/plan/cycleReport/getUnread')
}

/** 标记周期报告为已读 */
export const markCycleReportRead = (reportId: string) => {
  return post('/api/plan/cycleReport/markRead?reportId=' + reportId)
}

/** 根据方案ID获取周期报告（用于历史方案查看自身执行总结） */
export const getCycleReportByPlanId = (planId: string) => {
  return get<{ id: string; planId: string; newPlanId: string; reportContent: string } | null>('/api/plan/cycleReport/getByPlanId', { planId })
}

/** 提交反馈 */
export const submitFeedback = (params: PlanFeedbackParam) => {
  return post('/api/plan/submitFeedback', params)
}

/** 分页查询方案广场 */
export const getSquarePage = (params: PlanSquareQueryParam) => {
  return get<PageResult<PlanSquareItem>>('/api/plan/square/getSquarePage', params)
}

/** 获取方案广场详情 */
export const getSquareDetail = (id: string) => {
  return get<PlanSquareDetail>('/api/plan/square/getSquareDetail', { id })
}

/** 分享方案到广场 */
export const shareToSquare = (params: PlanSquareShareParam) => {
  return post('/api/plan/square/shareToSquare', params)
}

/** 广场操作（点赞/收藏） */
export const planSquareAction = (params: PlanSquareActionParam) => {
  return post('/api/plan/square/planAction', params)
}
