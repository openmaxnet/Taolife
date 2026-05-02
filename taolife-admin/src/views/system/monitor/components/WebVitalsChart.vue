<script setup lang="ts">
import { useEcharts } from '@/hooks'
import type { ECOption } from '@/hooks'

const props = defineProps<{ clientMetrics: Entity.ClientMetricsAgg | null }>()

const chartOptions = computed<ECOption>(() => {
  const m = props.clientMetrics?.metrics
  if (!m || Object.keys(m).length === 0) {
    return { title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 14 } } }
  }

  const names = Object.keys(m)
  const p50 = names.map((n) => m[n].p50)
  const p95 = names.map((n) => m[n].p95)
  const p99 = names.map((n) => m[n].p99)

  return {
    tooltip: { trigger: 'axis' },
    legend: { data: ['P50', 'P95', 'P99'] },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '15%', containLabel: true },
    xAxis: { type: 'category', data: names, axisTick: { show: false }, axisLine: { show: false } },
    yAxis: { type: 'value', name: 'ms', axisLabel: { show: true }, splitLine: { lineStyle: { type: 'dashed' } } },
    series: [
      { name: 'P50', type: 'bar', data: p50, itemStyle: { color: '#67c23a' } },
      { name: 'P95', type: 'bar', data: p95, itemStyle: { color: '#e6a23c' } },
      { name: 'P99', type: 'bar', data: p99, itemStyle: { color: '#f56c6c' } },
    ],
  }
})

useEcharts('webVitalsRef', chartOptions)
</script>

<template>
  <div ref="webVitalsRef" class="h-250px" />
</template>
