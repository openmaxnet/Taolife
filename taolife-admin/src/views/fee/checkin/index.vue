<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getCheckinRecordPage } from '@/service'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const columns: DataTableColumns<Entity.CheckinRecord> = [
  { title: '用户ID', key: 'accountId', width: 240, ellipsis: { tooltip: true } },
  { title: '签到日期', key: 'checkinDate', width: 180 },
  { title: '连续天数', key: 'consecutiveCheckinDays', width: 90, align: 'center' },
  { title: '累计天数', key: 'totalCheckinDays', width: 90, align: 'center' },
  { title: '获得积分', key: 'pointsEarned', width: 120, align: 'center' },
  { title: '备注', key: 'remark', ellipsis: { tooltip: true } },
  { title: '创建时间', key: 'createTime', width: 180 },
]

const listData = ref<Entity.CheckinRecord[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getCheckinList() {
  startLoading()
  await getCheckinRecordPage({ pageNo: currentPage.value, pageSize: currentPageSize.value }).then((res) => {
    listData.value = res.data?.list ?? []
    count.value = res.data?.totalRow ?? 0
    endLoading()
  }).catch(() => {
    endLoading()
  })
}

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getCheckinList()
}

onMounted(() => {
  getCheckinList()
})
</script>

<template>
  <n-card>
    <NSpace vertical>
      <n-data-table :columns="columns" :data="listData" :loading="loading" :row-key="(row: Entity.CheckinRecord) => row.id ?? ''" />
      <Pagination :count="count" @change="changePage" />
    </NSpace>
  </n-card>
</template>
