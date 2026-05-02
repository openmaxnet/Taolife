/**
 * Web Vitals 采集 + 上报
 * 采集 LCP/FID/CLS/TTFB/INP，批量上报到后端
 */

interface MetricItem {
  type: string
  name: string
  value: number
  rating: string
  timestamp: number
  tags?: Record<string, string>
}

const buffer: MetricItem[] = []
let flushTimer: ReturnType<typeof setInterval> | null = null
const FLUSH_INTERVAL = 30_000
const MAX_BUFFER_SIZE = 20

function getReportUrl(): string {
  return `${import.meta.env.VITE_SERVICE_URL}/api/monitor/report`
}

function onMetric(metric: { name: string; value: number; rating: string; delta: number }) {
  buffer.push({
    type: 'web_vital',
    name: metric.name,
    value: Math.round(metric.value * 100) / 100,
    rating: metric.rating,
    timestamp: Date.now(),
    tags: { page: location.pathname },
  })
  if (buffer.length >= MAX_BUFFER_SIZE) flush()
}

function flush() {
  if (buffer.length === 0) return
  const metrics = buffer.splice(0, buffer.length)
  const body = { source: 'admin', metrics }

  fetch(getReportUrl(), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
    keepalive: true,
  }).catch(() => {
    // 上报失败静默忽略
  })
}

export function initTelemetry() {
  // web-vitals v5 使用动态导入避免打包到 SSR 环境中
  import('web-vitals').then(({ onLCP, onCLS, onTTFB, onINP }) => {
    onLCP(onMetric)
    onCLS(onMetric)
    onTTFB(onMetric)
    onINP(onMetric)
  }).catch(() => {
    // web-vitals 加载失败不影响应用
  })

  // 定时批量上报
  flushTimer = setInterval(flush, FLUSH_INTERVAL)

  // 页面关闭前最后一次上报
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'hidden') flush()
  })
}

export function destroyTelemetry() {
  if (flushTimer) {
    clearInterval(flushTimer)
    flushTimer = null
  }
  flush()
}
