<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getAdjustmentPage } from '@/service'

const props = defineProps<{
  userPlanId: string
}>()

const { bool: visible, setTrue: show, setFalse: hide } = useBoolean(false)
const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const planTypeMap: Record<number, string> = { 1: '饮食', 2: '运动', 3: '穴位', 4: '生活' }
const adjustmentTypeMap: Record<number, string> = { 1: 'AI', 2: '手动', 3: '系统' }

const columns: DataTableColumns<Entity.PlanAdjustmentRecord> = [
  { title: '方案类型', key: 'planType', width: 80, render: row => planTypeMap[row.planType ?? 0] ?? '-' },
  { title: '调整类型', key: 'adjustmentType', width: 80, render: row => adjustmentTypeMap[row.adjustmentType ?? 0] ?? '-' },
  { title: '调整原因', key: 'adjustmentReason', ellipsis: { tooltip: true } },
  { title: '效果评分', key: 'effectivenessScore', width: 80, align: 'center' },
  { title: '创建时间', key: 'createTime', width: 180 },
]

const listData = ref<Entity.PlanAdjustmentRecord[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getAdjustmentPage({
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
    <n-drawer-content title="调整记录">
      <n-data-table :columns="columns" :data="listData" :loading="loading" />
      <Pagination :count="count" @change="changePage" class="mt-12px" />
    </n-drawer-content>
  </n-drawer>
</template>
