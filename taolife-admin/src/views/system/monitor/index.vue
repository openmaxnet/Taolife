<script setup lang="ts">
import { fetchMonitorMetrics, fetchClientMetrics } from '@/service'
import JvmChart from './components/JvmChart.vue'
import DbPoolChart from './components/DbPoolChart.vue'
import WebVitalsChart from './components/WebVitalsChart.vue'
import MiniAppChart from './components/MiniAppChart.vue'

const loading = ref(false)
const metrics = ref<Entity.MonitorMetrics | null>(null)
const clientMetrics = ref<Entity.ClientMetricsAgg | null>(null)
const miniAppMetrics = ref<Entity.ClientMetricsAgg | null>(null)

async function loadMetrics() {
  loading.value = true
  try {
    const [sysRes, clientRes, miniRes] = await Promise.all([
      fetchMonitorMetrics(),
      fetchClientMetrics('admin'),
      fetchClientMetrics('miniapp'),
    ])
    if (sysRes.data) metrics.value = sysRes.data
    if (clientRes.data) clientMetrics.value = clientRes.data
    if (miniRes.data) miniAppMetrics.value = miniRes.data
  } catch {
    // 静默处理
  }
  loading.value = false
}

let refreshTimer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  loadMetrics()
  refreshTimer = setInterval(loadMetrics, 30_000)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
})

function formatUptime(seconds: number): string {
  const d = Math.floor(seconds / 86400)
  const h = Math.floor((seconds % 86400) / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  if (d > 0) return `${d}天 ${h}小时`
  if (h > 0) return `${h}小时 ${m}分`
  return `${m}分钟`
}

function pct(v: number): string {
  return v >= 0 ? `${(v * 100).toFixed(1)}%` : '-'
}
</script>

<template>
  <n-spin :show="loading && !metrics">
    <n-grid :x-gap="16" :y-gap="16" :cols="12" item-responsive responsive="screen">
      <!-- 统计卡片 -->
      <n-gi span="6 m:3">
        <n-card>
          <n-space justify="space-between" align="center">
            <n-statistic label="HTTP 请求">
              <n-number-animation :from="0" :to="metrics?.http?.totalRequests ?? 0" :precision="0" />
            </n-statistic>
            <n-icon color="#de4307" size="42">
              <icon-park-outline-chart-histogram />
            </n-icon>
          </n-space>
          <template #footer>
            <n-space justify="space-between">
              <span>平均响应</span>
              <span>{{ metrics?.http?.avgResponseTimeMs?.toFixed(1) ?? '-' }} ms</span>
            </n-space>
          </template>
        </n-card>
      </n-gi>

      <n-gi span="6 m:3">
        <n-card>
          <n-space justify="space-between" align="center">
            <n-statistic label="用户注册">
              <n-number-animation :from="0" :to="metrics?.business?.userRegisterTotal ?? 0" :precision="0" />
            </n-statistic>
            <n-icon color="#42218E" size="42">
              <icon-park-outline-chart-pie />
            </n-icon>
          </n-space>
          <template #footer>
            <n-space justify="space-between">
              <span>用户登录</span>
              <span>{{ metrics?.business?.userLoginTotal?.toFixed(0) ?? '-' }}</span>
            </n-space>
          </template>
        </n-card>
      </n-gi>

      <n-gi span="6 m:3">
        <n-card>
          <n-space justify="space-between" align="center">
            <n-statistic label="订单创建">
              <n-number-animation :from="0" :to="metrics?.business?.orderCreateTotal ?? 0" :precision="0" />
            </n-statistic>
            <n-icon color="#ffb549" size="42">
              <icon-park-outline-chart-graph />
            </n-icon>
          </n-space>
          <template #footer>
            <n-space justify="space-between">
              <span>AI 调用</span>
              <span>{{ metrics?.business?.aiRequestTotal?.toFixed(0) ?? '-' }}</span>
            </n-space>
          </template>
        </n-card>
      </n-gi>

      <n-gi span="6 m:3">
        <n-card>
          <n-space justify="space-between" align="center">
            <n-statistic label="运行时间">
              {{ metrics?.jvm?.uptimeSeconds ? formatUptime(metrics.jvm.uptimeSeconds) : '-' }}
            </n-statistic>
            <n-icon color="#1687a7" size="42">
              <icon-park-outline-average />
            </n-icon>
          </n-space>
          <template #footer>
            <n-space justify="space-between">
              <span>CPU</span>
              <span>{{ pct(metrics?.jvm?.processCpuUsage ?? -1) }}</span>
            </n-space>
          </template>
        </n-card>
      </n-gi>

      <!-- JVM 指标 -->
      <n-gi span="12 m:6">
        <n-card title="JVM 内存">
          <JvmChart v-if="metrics?.jvm" :jvm="metrics.jvm" />
          <n-empty v-else description="暂无数据" />
        </n-card>
      </n-gi>

      <!-- 数据库连接池 -->
      <n-gi span="12 m:6">
        <n-card title="数据库连接池">
          <DbPoolChart v-if="metrics?.dbPool" :db-pool="metrics.dbPool" />
          <n-empty v-else description="暂无数据" />
        </n-card>
      </n-gi>

      <!-- Admin Web Vitals -->
      <n-gi span="12 m:6">
        <n-card title="Admin 前端性能 (Web Vitals)">
          <WebVitalsChart :client-metrics="clientMetrics" />
        </n-card>
      </n-gi>

      <!-- 小程序指标 -->
      <n-gi span="12 m:6">
        <n-card title="小程序性能">
          <MiniAppChart :client-metrics="miniAppMetrics" />
        </n-card>
      </n-gi>

      <!-- 系统信息表 -->
      <n-gi :span="12">
        <n-card title="系统状态">
          <n-descriptions label-placement="left" :column="3" bordered>
            <n-descriptions-item label="堆内存使用">
              {{ metrics?.jvm?.heapUsed?.toFixed(0) ?? '-' }} /
              {{ metrics?.jvm?.heapMax?.toFixed(0) ?? '-' }} MB
              ({{ pct(metrics?.jvm?.heapUsage ?? -1) }})
            </n-descriptions-item>
            <n-descriptions-item label="活跃线程">
              {{ metrics?.jvm?.activeThreads ?? '-' }}
              (峰值 {{ metrics?.jvm?.peakThreads ?? '-' }})
            </n-descriptions-item>
            <n-descriptions-item label="系统 CPU">
              {{ pct(metrics?.jvm?.systemCpuUsage ?? -1) }}
            </n-descriptions-item>
            <n-descriptions-item label="连接池">
              {{ metrics?.dbPool?.activeConnections ?? 0 }} 活跃 /
              {{ metrics?.dbPool?.idleConnections ?? 0 }} 空闲 /
              {{ metrics?.dbPool?.maxConnections ?? 0 }} 最大
            </n-descriptions-item>
            <n-descriptions-item label="等待连接线程">
              {{ metrics?.dbPool?.pendingThreads ?? 0 }}
            </n-descriptions-item>
            <n-descriptions-item label="AI 平均耗时">
              {{ metrics?.business?.aiRequestAvgTimeMs?.toFixed(1) ?? '-' }} ms
            </n-descriptions-item>
          </n-descriptions>
        </n-card>
      </n-gi>
    </n-grid>
  </n-spin>
</template>
