/**
 * 小程序自定义监控采集器
 * 采集 API 请求延迟、错误率、页面停留、运行时错误、内存警告
 * 批量上报到后端 /api/admin/monitor/report
 */

import { BASE_URL } from './config'

interface MetricRecord {
  type: 'api_request' | 'page_view' | 'error' | 'memory_warning'
  name: string
  value: number
  rating?: string
  timestamp: number
  tags?: Record<string, string>
}

class MiniAppMonitor {
  private buffer: MetricRecord[] = []
  private flushTimer: ReturnType<typeof setInterval> | null = null
  private readonly MAX_BUFFER = 20
  private readonly FLUSH_INTERVAL = 30_000

  start() {
    this.flushTimer = setInterval(() => this.flush(), this.FLUSH_INTERVAL)

    // 全局 JS 错误捕获
    // @ts-expect-error uni-app 全局事件
    uni.onError((error: string) => {
      this.recordError(error)
    })

    // 内存警告
    // @ts-expect-error uni-app 全局事件
    uni.onMemoryWarning((res: { level: number }) => {
      this.recordMemoryWarning(res.level)
    })
  }

  /** 记录 API 请求 */
  recordApiRequest(url: string, method: string, duration: number, statusCode: number) {
    const rating = statusCode >= 200 && statusCode < 300 ? 'ok'
      : statusCode >= 400 && statusCode < 500 ? 'client_error'
      : 'server_error'
    this.buffer.push({
      type: 'api_request',
      name: `${method} ${url}`,
      value: duration,
      rating,
      timestamp: Date.now(),
      tags: { url, method, statusCode: String(statusCode) },
    })
    if (this.buffer.length >= this.MAX_BUFFER) this.flush()
  }

  /** 记录页面停留 */
  recordPageView(page: string, duration: number) {
    this.buffer.push({
      type: 'page_view',
      name: page,
      value: duration,
      timestamp: Date.now(),
    })
  }

  /** 记录 JS 运行时错误 */
  recordError(error: string) {
    this.buffer.push({
      type: 'error',
      name: 'js_error',
      value: 1,
      timestamp: Date.now(),
      tags: { message: error.substring(0, 200) },
    })
    if (this.buffer.length >= this.MAX_BUFFER) this.flush()
  }

  /** 记录内存警告 */
  recordMemoryWarning(level: number) {
    this.buffer.push({
      type: 'memory_warning',
      name: 'memory_warning',
      value: level,
      timestamp: Date.now(),
      tags: { level: String(level) },
    })
  }

  /** 批量上报 */
  flush() {
    if (this.buffer.length === 0) return
    const metrics = this.buffer.splice(0, this.buffer.length)
    const body = JSON.stringify({ source: 'miniapp', metrics })

    uni.request({
      url: `${BASE_URL}/api/monitor/report`,
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: body,
      success: () => {},
      fail: () => {},
    })
  }
}

export const miniAppMonitor = new MiniAppMonitor()
