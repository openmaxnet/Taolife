<script setup lang="ts">
import { useEcharts } from '@/hooks'
import type { ECOption } from '@/hooks'

const props = defineProps<{ jvm: Entity.JvmMetrics }>()

const chartOptions = computed<ECOption>(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} MB ({d}%)' },
  series: [
    {
      type: 'pie',
      radius: ['55%', '75%'],
      center: ['30%', '50%'],
      data: [
        { value: props.jvm.heapUsed.toFixed(0), name: '已用堆', itemStyle: { color: '#f56c6c' } },
        { value: (props.jvm.heapMax - props.jvm.heapUsed).toFixed(0), name: '空闲堆', itemStyle: { color: '#e0e0e0' } },
      ],
      label: { show: false },
    },
    {
      type: 'pie',
      radius: ['55%', '75%'],
      center: ['70%', '50%'],
      data: [
        { value: (props.jvm.nonHeapUsed).toFixed(0), name: '非堆已用', itemStyle: { color: '#409eff' } },
        { value: Math.max(0, 512 - props.jvm.nonHeapUsed).toFixed(0), name: '非堆空闲', itemStyle: { color: '#e0e0e0' } },
      ],
      label: { show: false },
    },
  ],
  graphic: [
    { type: 'text', left: '22%', top: '47%', style: { text: '堆内存', fontSize: 12, textAlign: 'center' } },
    { type: 'text', left: '62%', top: '47%', style: { text: '非堆', fontSize: 12, textAlign: 'center' } },
  ],
}))

useEcharts('jvmChartRef', chartOptions)
</script>

<template>
  <div ref="jvmChartRef" class="h-250px" />
</template>
