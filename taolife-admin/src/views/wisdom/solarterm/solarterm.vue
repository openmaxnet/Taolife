<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getSolarTermPage, removeSolarTerm } from '@/service/api/wisdom'
import { NButton, NPopconfirm, NSpace, NTag } from 'naive-ui'
import SolarTermModal from './components/SolarTermModal.vue'

const modalRef = ref()
const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const seasonMap: Record<number, { label: string, type: 'success' | 'warning' | 'error' | 'info' }> = {
  1: { label: '春', type: 'success' },
  2: { label: '夏', type: 'error' },
  3: { label: '秋', type: 'warning' },
  4: { label: '冬', type: 'info' },
}

const columns: DataTableColumns<Entity.SolarTermVO> = [
  { title: '序号', align: 'center', key: 'termOrder', width: 70 },
  { title: '节气名称', align: 'center', key: 'termName', width: 100 },
  {
    title: '日期范围', align: 'center', key: 'dateRange', width: 150,
    render: (row) => `${row.startMonth}/${row.startDay} - ${row.endMonth}/${row.endDay}`,
  },
  {
    title: '季节', align: 'center', key: 'season', width: 80,
    render: (row) => {
      const info = seasonMap[row.season || 0]
      return info ? <NTag type={info.type} size="small">{info.label}</NTag> : '-'
    },
  },
  { title: '描述', align: 'center', key: 'description', ellipsis: { tooltip: true } },
  { title: '更新时间', align: 'center', key: 'updateTime', width: 180 },
  {
    title: '操作', align: 'center', key: 'actions', width: 180,
    render: (row) => (
      <NSpace justify="center">
        <NButton size="small" onClick={() => modalRef.value.openModal('edit', row)}>编辑</NButton>
        <NPopconfirm onPositiveClick={() => handleDelete(row.id)}>
          {{
            default: () => '确认删除？',
            trigger: () => <NButton size="small" type="error">删除</NButton>,
          }}
        </NPopconfirm>
      </NSpace>
    ),
  },
]

const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)
const listData = ref<Entity.SolarTermVO[]>([])

async function handleDelete(id: string) {
  await removeSolarTerm(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getSolarTermPage({ pageNo: currentPage.value, pageSize: currentPageSize.value }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

onMounted(() => { getList() })

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getList()
}
</script>

<template>
  <NSpace vertical>
    <n-card class="flex-1">
      <template #header>
        <NButton type="primary" @click="modalRef.openModal('add')">
          <template #icon><icon-park-outline-add-one /></template>
          新建节气
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
  <SolarTermModal ref="modalRef" modal-name="节气" @saved="getList" />
</template>
