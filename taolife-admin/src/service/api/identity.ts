import { request } from '../http'

/**
 * 用户偏好分页
 */
export function getUserPreferencePage(params: { pageNo: number; pageSize: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.UserPreference>>>('/api/identity/admin/userPreference/getUserPreferencePage', { params })
}

// ==================== 系统协议管理 ====================

export function getAgreementPage(params: { pageNo: number; pageSize: number; keyword?: string }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.SysAgreement>>>('/api/identity/admin/agreement/getAgreementPage', { params })
}

export function getAgreementDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.SysAgreement>>('/api/identity/admin/agreement/getAgreementDetail', { params: { id } })
}

export function createAgreement(data: Entity.SysAgreementSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/agreement/createAgreement', data)
}

export function modifyAgreementInfo(id: string, data: Entity.SysAgreementSaveParam) {
  return request.Post<Service.ResponseResult<void>>(`/api/identity/admin/agreement/modifyAgreementInfo?id=${id}`, data)
}

export function removeAgreement(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/agreement/removeAgreement', undefined, { params: { id } })
}
