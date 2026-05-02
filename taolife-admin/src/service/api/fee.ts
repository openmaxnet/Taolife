import { request } from '../http'

/**
 * 获取广告配置分页列表
 */
export function getAdConfigPage(params: { pageNo: number; pageSize: number; configKey?: string; adType?: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.AdConfigAdminVO>>>('/api/fee/admin/ad/getAdConfigPage', { params })
}

/**
 * 获取广告配置详情
 */
export function getAdConfig(id: string) {
  return request.Get<Service.ResponseResult<Entity.AdConfigAdminVO>>('/api/fee/admin/ad/getAdConfig', { params: { id } })
}

/**
 * 创建广告配置
 */
export function createAdConfig(data: Entity.AdConfigSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/fee/admin/ad/createAdConfig', data)
}

/**
 * 修改广告配置
 */
export function modifyAdConfig(id: string, data: Entity.AdConfigSaveParam) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/ad/modifyAdConfig?id=${id}`, data)
}

/**
 * 修改广告配置状态
 */
export function modifyAdConfigStatus(id: string, isEnabled: number) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/ad/modifyAdConfigStatus?id=${id}&isEnabled=${isEnabled}`)
}

/**
 * 删除广告配置
 */
export function removeAdConfig(id: string) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/ad/removeAdConfig?id=${id}`)
}

/**
 * 获取广告日志分页列表
 */
export function getAdLogPage(params: { pageNo: number; pageSize: number; accountId?: string; adType?: number; action?: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.AdLogAdminVO>>>('/api/fee/admin/ad/getAdLogPage', { params })
}

/**
 * 获取积分商品分页列表
 */
export function getGoodsPage(params: { pageNo: number; pageSize: number; goodsCode?: string; goodsType?: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.PointsGoodsAdminVO>>>('/api/fee/admin/goods/getGoodsPage', { params })
}

/**
 * 获取积分商品详情
 */
export function getGoods(id: string) {
  return request.Get<Service.ResponseResult<Entity.PointsGoodsAdminVO>>('/api/fee/admin/goods/getGoods', { params: { id } })
}

/**
 * 创建积分商品
 */
export function createGoods(data: Entity.PointsGoodsSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/fee/admin/goods/createGoods', data)
}

/**
 * 修改积分商品
 */
export function modifyGoods(id: string, data: Entity.PointsGoodsSaveParam) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/goods/modifyGoods?id=${id}`, data)
}

/**
 * 修改积分商品状态
 */
export function modifyGoodsStatus(id: string, isEnabled: number) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/goods/modifyGoodsStatus?id=${id}&isEnabled=${isEnabled}`)
}

/**
 * 删除积分商品
 */
export function removeGoods(id: string) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/goods/removeGoods?id=${id}`)
}

/**
 * 获取积分兑换记录分页列表
 */
export function getExchangeRecordPage(params: { pageNo: number; pageSize: number; accountId?: string; status?: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.PointsExchangeAdminVO>>>('/api/fee/admin/goods/getExchangeRecordPage', { params })
}

// ==================== 会员套餐管理 ====================

/**
 * 获取会员套餐分页列表
 */
export function getMemberPlanPage(params: { pageNo: number; pageSize: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.MemberPlanVO>>>('/api/fee/admin/member/getMemberPlanPage', { params })
}

/**
 * 获取会员套餐详情
 */
export function getMemberPlan(id: string) {
  return request.Get<Service.ResponseResult<Entity.MemberPlanVO>>('/api/fee/admin/member/getMemberPlan', { params: { id } })
}

/**
 * 获取启用的套餐列表
 */
export function getEnabledMemberPlans() {
  return request.Get<Service.ResponseResult<Entity.MemberPlanVO[]>>('/api/fee/admin/member/getEnabledPlans')
}

/**
 * 创建会员套餐
 */
export function createMemberPlan(data: Entity.MemberPlanSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/fee/admin/member/createMemberPlan', data)
}

/**
 * 修改会员套餐
 */
export function modifyMemberPlan(id: string, data: Entity.MemberPlanSaveParam) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/member/modifyMemberPlan?id=${id}`, data)
}

/**
 * 删除会员套餐
 */
export function removeMemberPlan(id: string) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/member/removeMemberPlan?id=${id}`)
}

// ==================== 积分记录管理 ====================

/**
 * 积分记录分页
 */
export function getPointsRecordPage(params: { pageNo: number; pageSize: number; accountId?: string }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.PointsRecord>>>('/api/fee/admin/points/getPointsRecordPage', { params })
}

// ==================== 签到记录管理 ====================

/**
 * 签到记录分页
 */
export function getCheckinRecordPage(params: { pageNo: number; pageSize: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<any>>>('/api/fee/admin/checkin/getCheckinRecordPage', { params })
}

// ==================== 积分规则管理 ====================

/**
 * 积分规则分页列表
 */
export function getPointsRulePage(params: { pageNo: number; pageSize: number }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.PointsRule>>>('/api/fee/admin/pointsRule/getRulePage', { params })
}

/**
 * 积分规则详情
 */
export function getPointsRule(id: string) {
  return request.Get<Service.ResponseResult<Entity.PointsRule>>('/api/fee/admin/pointsRule/getRule', { params: { id } })
}

/**
 * 获取所有启用的规则
 */
export function getEnabledPointsRules() {
  return request.Get<Service.ResponseResult<Entity.PointsRule[]>>('/api/fee/admin/pointsRule/getEnabledRules')
}

/**
 * 创建积分规则
 */
export function createPointsRule(data: Entity.PointsRuleSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/fee/admin/pointsRule/createRule', data)
}

/**
 * 修改积分规则
 */
export function modifyPointsRule(id: string, data: Entity.PointsRuleSaveParam) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/pointsRule/modifyRule?id=${id}`, data)
}

/**
 * 删除积分规则
 */
export function removePointsRule(id: string) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/pointsRule/removeRule?id=${id}`)
}

/**
 * 修改积分规则状态
 */
export function modifyPointsRuleStatus(id: string, isEnabled: number) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/pointsRule/modifyRuleStatus?id=${id}&isEnabled=${isEnabled}`)
}

// ==================== 会员成长管理 ====================

/**
 * 成长记录分页
 */
export function getGrowthRecordPage(params: { pageNo: number; pageSize: number; accountId?: string }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.MemberGrowthRecordAdminVO>>>('/api/fee/admin/growth/getGrowthRecordPage', { params })
}

/**
 * 手动调整成长值
 */
export function adjustGrowth(accountId: string, growth: number, remark?: string) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/growth/adjustGrowth?accountId=${accountId}&growth=${growth}${remark ? `&remark=${remark}` : ''}`)
}

// ==================== 成长等级配置 ====================

/**
 * 成长等级分页
 */
export function getGrowthLevelPage(params: { pageNo: number; pageSize: number; level?: number; levelName?: string }) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.GrowthLevelAdminVO>>>('/api/fee/admin/growth/getGrowthLevelPage', { params })
}

/**
 * 成长等级详情
 */
export function getGrowthLevelDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.GrowthLevelAdminVO>>('/api/fee/admin/growth/getGrowthLevelDetail', { params: { id } })
}

/**
 * 创建成长等级
 */
export function createGrowthLevel(data: Entity.GrowthLevelSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/fee/admin/growth/createGrowthLevel', data)
}

/**
 * 修改成长等级
 */
export function modifyGrowthLevelInfo(id: string, data: Entity.GrowthLevelSaveParam) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/growth/modifyGrowthLevelInfo?id=${id}`, data)
}

/**
 * 修改成长等级状态
 */
export function modifyGrowthLevelStatus(id: string, isEnabled: number) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/growth/modifyGrowthLevelStatus?id=${id}&isEnabled=${isEnabled}`)
}

/**
 * 删除成长等级
 */
export function removeGrowthLevel(id: string) {
  return request.Post<Service.ResponseResult<void>>(`/api/fee/admin/growth/removeGrowthLevel?id=${id}`)
}

// ==================== 轮播管理 ====================

export function getBannerPage(params: Entity.BannerPageParam) {
  return request.Get<Service.ResponseResult<Api.PageResult<Entity.BannerVO>>>('/api/fee/admin/banner/getBannerPage', { params })
}

export function getBannerDetail(id: string) {
  return request.Get<Service.ResponseResult<Entity.BannerVO>>('/api/fee/admin/banner/getBannerDetail', { params: { id } })
}

export function createBanner(data: Entity.BannerSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/fee/admin/banner/createBanner', data)
}

export function modifyBannerInfo(data: Entity.BannerSaveParam) {
  return request.Post<Service.ResponseResult<void>>('/api/fee/admin/banner/modifyBannerInfo', data)
}

export function removeBanner(id: string) {
  return request.Post<Service.ResponseResult<void>>('/api/fee/admin/banner/removeBanner', undefined, { params: { id } })
}

export function modifyBannerStatus(id: string, status: 0 | 1) {
  return request.Post<Service.ResponseResult<void>>('/api/fee/admin/banner/modifyBannerStatus', undefined, { params: { id, status } })
}