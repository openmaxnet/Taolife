/**
 * 方案任务接口
 */

import { get, post } from '@/utils/request'
import type { PageResult } from '@/types/common/request'
import type {
  PlanTaskItem,
  PlanTaskDetail,
  PlanTaskQueryParam,
  TaskCompleteParam,
} from '@/types/biz/plan/plan'

/** 分页查询任务列表 */
export const getTaskPage = (userPlanId: string, params: PlanTaskQueryParam) => {
  return get<PageResult<PlanTaskItem>>('/api/plan/task/getTaskPage', { userPlanId, ...params })
}

/** 获取任务详情 */
export const getTaskDetail = (id: string) => {
  return get<PlanTaskDetail>('/api/plan/task/getTaskDetail', { id })
}

/** 完成任务 */
export const completeTask = (params: TaskCompleteParam) => {
  return post('/api/plan/task/completeTask', params)
}

/** 跳过任务 */
export const skipTask = (id: string) => {
  return post('/api/plan/task/skipTask', null, { data: { id } })
}
