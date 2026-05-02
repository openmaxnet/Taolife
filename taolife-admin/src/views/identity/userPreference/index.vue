<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getUserPreferencePage } from '@/service'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const exerciseTimeMap: Record<number, string> = {
  1: '清晨',
  2: '上午',
  3: '下午',
  4: '傍晚',
  5: '晚上',
}

const columns: DataTableColumns<Entity.UserPreference> = [
  { title: '用户ID', key: 'accountId', width: 240 },
  { title: '食材属性偏好', key: 'preferredFoodNature', ellipsis: { tooltip: true } },
  { title: '运动类型', key: 'preferredExerciseType', width: 100 },
  { title: '运动强度', key: 'preferredExerciseIntensity', width: 100 },
  {
    title: '运动时间',
    key: 'preferredExerciseTime',
    width: 90,
    render: row => exerciseTimeMap[row.preferredExerciseTime ?? 0] ?? '-',
  },
  { title: '互动次数', key: 'totalInteractions', width: 90, align: 'center' },
  { title: '最后学习', key: 'lastLearnTime', width: 180 },
  { title: '创建时间', key: 'createTime', width: 180 },
]

const listData = ref<Entity.UserPreference[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getUserPreferencePage({
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

onMounted(() => {
  getList()
})
</script>

<template>
  <n-card>
    <template #header>
      用户偏好
    </template>
    <NSpace vertical>
      <n-data-table :columns="columns" :data="listData" :loading="loading" />
      <Pagination :count="count" @change="changePage" />
    </NSpace>
  </n-card>
</template>
