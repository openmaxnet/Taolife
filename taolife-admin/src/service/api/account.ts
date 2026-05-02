import { request } from '../http'

/**
 * 分页查询账号列表
 */
export function getAccountPage(params: Entity.AccountPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.AccountVO>>>('/api/identity/admin/account/getAccountPage', { params })
}

/**
 * 获取账号详情
 */
export function getAccountDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.AccountDetailVO>>('/api/identity/admin/account/getAccountDetail', { params: { id } })
}

/**
 * 修改账号状态
 */
export function modifyAccountStatus(data: Entity.ModifyAccountStatusParam) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/account/modifyAccountStatus', data)
}

/**
 * 修改会员等级
 */
export function modifyMemberLevel(data: Entity.ModifyMemberLevelParam) {
  return request.Post<Service.ResponseResult<void>>('/api/identity/admin/account/modifyMemberLevel', data)
}
