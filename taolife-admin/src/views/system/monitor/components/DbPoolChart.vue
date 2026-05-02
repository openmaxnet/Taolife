<script setup lang="ts">
import { useEcharts } from '@/hooks'
import type { ECOption } from '@/hooks'

const props = defineProps<{ dbPool: Entity.DbPoolMetrics }>()

const chartOptions = computed<ECOption>(() => {
  const active = props.dbPool.activeConnections
  const idle = props.dbPool.idleConnections
  const pending = props.dbPool.pendingThreads
  const max = props.dbPool.maxConnections

  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['活跃', '空闲', '等待', '最大'],
      axisTick: { show: false },
      axisLine: { show: false },
    },
    yAxis: { type: 'value', axisLabel: { show: true }, splitLine: { lineStyle: { type: 'dashed' } } },
    series: [
      {
        type: 'bar',
        data: [
          { value: active, itemStyle: { color: '#409eff' } },
          { value: idle, itemStyle: { color: '#67c23a' } },
          { value: pending, itemStyle: { color: '#e6a23c' } },
          { value: max, itemStyle: { color: '#909399' } },
        ],
        barWidth: '40%',
        label: { show: true, position: 'top' },
      },
    ],
  }
})

useEcharts('dbPoolChartRef', chartOptions)
</script>

<template>
  <div ref="dbPoolChartRef" class="h-250px" />
</template>
