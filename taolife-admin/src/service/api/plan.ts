import { request } from '../http'

// ==================== 用户方案管理 ====================

export function getUserPlanPage(params: { pageNo?: number, pageSize?: number, keyword?: string, status?: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.UserPlan>>>('/api/plan/admin/plan/getUserPlanPage', { params })
}

export function getUserPlanDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.UserPlanDetail>>('/api/plan/admin/plan/getUserPlanDetail', { params: { id } })
}

export function modifyUserPlanStatus(id: string, status: number) {
  return request.Post<Service.ResponseResult<void>>('/api/plan/admin/plan/modifyUserPlanStatus', undefined, { params: { id, status } })
}

// ==================== 方案广场管理 ====================

export function getSquarePage(params: { pageNo?: number, pageSize?: number, status?: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.PlanSquare>>>('/api/plan/admin/square/getSquarePage', { params })
}

export function modifySquareStatus(id: string, status: number) {
  return request.Post<Service.ResponseResult<void>>('/api/plan/admin/square/modifySquareStatus', undefined, { params: { id, status } })
}

// ==================== 评论管理 ====================

export function getCommentPage(params: { pageNo?: number, pageSize?: number, planSquareId?: string }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.PlanComment>>>('/api/plan/admin/comment/getCommentPage', { params })
}

export function removeComment(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/plan/admin/comment/removeComment', undefined, { params: { id } })
}

export function modifyCommentStatus(id: string, status: number) {
  return request.Post<Service.ResponseResult<void>>('/api/plan/admin/comment/modifyCommentStatus', undefined, { params: { id, status } })
}

// ==================== 方案任务 ====================

export function getTaskPage(params: { pageNo?: number, pageSize?: number, userPlanId?: string }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.PlanTask>>>('/api/plan/admin/task/getTaskPage', { params })
}

// ==================== 方案调整记录 ====================

export function getAdjustmentPage(params: { pageNo?: number, pageSize?: number, userPlanId?: string }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.PlanAdjustmentRecord>>>('/api/plan/admin/adjustment/getAdjustmentPage', { params })
}

// ==================== 周期报告 ====================

export function getCycleReportPage(params: { pageNo?: number, pageSize?: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.PlanCycleReport>>>('/api/plan/admin/cycleReport/getCycleReportPage', { params })
}

// ==================== 分享记录 ====================

export function getShareRecordPage(params: { pageNo?: number, pageSize?: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.PlanShareRecord>>>('/api/plan/admin/shareRecord/getShareRecordPage', { params })
}
