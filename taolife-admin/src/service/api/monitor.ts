import { request } from '../http'

// ==================== 监控指标 ====================

/** 获取后端系统指标 */
export function fetchMonitorMetrics() {
  return request.Get<Service.ResponseResult<Entity.MonitorMetrics>>('/api/admin/monitor/metrics')
}

/** 获取客户端指标聚合 */
export function fetchClientMetrics(source: string, type?: string, limit = 20) {
  const params: Record<string, any> = { source, limit }
  if (type) params.type = type
  return request.Get<Service.ResponseResult<Entity.ClientMetricsAgg>>('/api/admin/monitor/client-metrics', { params })
}
