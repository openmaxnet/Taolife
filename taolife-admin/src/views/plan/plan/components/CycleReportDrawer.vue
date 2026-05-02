<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getCycleReportPage } from '@/service'
import { NTag } from 'naive-ui'

const props = defineProps<{
  userPlanId: string
}>()

const { bool: visible, setTrue: show } = useBoolean(false)
const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const columns: DataTableColumns<Entity.PlanCycleReport> = [
  { title: '用户ID', key: 'accountId', width: 240 },
  { title: '新方案ID', key: 'newPlanId', width: 240, ellipsis: { tooltip: true } },
  {
    title: '是否已读', key: 'isRead', width: 90, align: 'center',
    render: (row) => {
      if (row.isRead === 1) return <NTag type="success" size="small">已读</NTag>
      return <NTag type="warning" size="small">未读</NTag>
    },
  },
  { title: '创建时间', key: 'createTime', width: 180 },
]

const listData = ref<Entity.PlanCycleReport[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getCycleReportPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
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
    <n-drawer-content title="周期报告">
      <n-data-table :columns="columns" :data="listData" :loading="loading" />
      <Pagination :count="count" @change="changePage" class="mt-12px" />
    </n-drawer-content>
  </n-drawer>
</template>
