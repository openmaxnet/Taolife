<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getTaskPage } from '@/service'
import { NTag } from 'naive-ui'

const props = defineProps<{
  userPlanId: string
}>()

const { bool: visible, setTrue: show, setFalse: hide } = useBoolean(false)
const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const planTypeMap: Record<number, string> = { 1: '饮食', 2: '运动', 3: '穴位', 4: '生活' }
const statusMap: Record<number, { label: string, type: string }> = {
  1: { label: '待完成', type: 'info' },
  2: { label: '已完成', type: 'success' },
  3: { label: '已跳过', type: 'warning' },
}

const columns: DataTableColumns<Entity.PlanTask> = [
  { title: '任务名', key: 'taskName', width: 120 },
  { title: '类型', key: 'planType', width: 70, render: row => planTypeMap[row.planType ?? 0] ?? '-' },
  { title: '任务日期', key: 'taskDate', width: 180 },
  { title: '进度', key: 'progress', width: 80, render: row => `${row.completedCount ?? 0}/${row.targetCount ?? 0}` },
  {
    title: '状态', key: 'status', width: 80,
    render: (row) => {
      const s = statusMap[row.status ?? 0]
      return s ? <NTag size="small" type={s.type as any}>{s.label}</NTag> : '-'
    },
  },
  { title: '创建时间', key: 'createTime', width: 180 },
]

const listData = ref<Entity.PlanTask[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getTaskPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    userPlanId: props.userPlanId,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getList()
}

function open() {
  currentPage.value = 1
  show()
  getList()
}

defineExpose({ open })
</script>

<template>
  <n-drawer v-model:show="visible" :width="700">
    <n-drawer-content title="方案任务">
      <n-data-table :columns="columns" :data="listData" :loading="loading" />
      <Pagination :count="count" @change="changePage" class="mt-12px" />
    </n-drawer-content>
  </n-drawer>
</template>
