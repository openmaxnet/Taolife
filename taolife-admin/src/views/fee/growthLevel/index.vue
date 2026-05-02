<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getGrowthLevelPage, modifyGrowthLevelStatus, removeGrowthLevel } from '@/service'
import { NButton, NPopconfirm, NSpace, NSwitch, NTag } from 'naive-ui'
import GrowthLevelModal from './components/GrowthLevelModal.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const modalRef = ref()
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(20)
const listData = ref<Entity.GrowthLevelAdminVO[]>([])

async function handleDelete(id: string) {
  await removeGrowthLevel(id)
  window.$message.success('删除成功')
  getList()
}

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifyGrowthLevelStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isEnabled = value
  window.$message.success(value === 1 ? '已启用' : '已禁用')
}

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getList()
}

const columns: DataTableColumns<Entity.GrowthLevelAdminVO> = [
  { title: '等级', key: 'level', width: 80, align: 'center', render: row => `V${row.level}` },
  { title: '等级名称', key: 'levelName', width: 120 },
  { title: '最低成长值', key: 'minGrowthValue', width: 100, align: 'center' },
  { title: 'AI额外配额', key: 'bonusAiQuota', width: 100, align: 'center' },
  { title: '积分倍率', key: 'bonusPointsMultiplier', width: 90, align: 'center' },
  { title: '商城折扣', key: 'bonusStoreDiscount', width: 90, align: 'center', render: row => `${((row.bonusStoreDiscount ?? 1) * 10).toFixed(1)}折` },
  {
    title: '状态',
    key: 'isEnabled',
    width: 80,
    align: 'center',
    render: row => <NTag type={row.isEnabled === 1 ? 'success' : 'error'} size="small">{row.isEnabled === 1 ? '启用' : '禁用'}</NTag>,
  },
  { title: '排序', key: 'sortOrder', width: 70, align: 'center' },
  { title: '创建时间', key: 'createTime', width: 170 },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    align: 'center',
    render: (row) => {
      return (
        <NSpace justify="center">
          <NButton size="small" onClick={() => modalRef.value.openModal('edit', row)}>
            编辑
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDelete(row.id ?? '')}>
            {{
              default: () => '确认删除该等级？',
              trigger: () => <NButton size="small" type="error">删除</NButton>,
            }}
          </NPopconfirm>
        </NSpace>
      )
    },
  },
]

async function getList() {
  startLoading()
  const res = await getGrowthLevelPage({ pageNo: currentPage.value, pageSize: currentPageSize.value })
  listData.value = res.data.list
  count.value = res.data.totalRow
  endLoading()
}

onMounted(() => {
  getList()
})
</script>

<template>
  <n-card>
    <template #header>
      <NButton type="primary" @click="modalRef.openModal('add')">
        <template #icon>
          <icon-park-outline-add-one />
        </template>
        新建等级
      </NButton>
    </template>
    <NSpace vertical>
      <n-data-table :columns="columns" :data="listData" :loading="loading" />
      <Pagination :count="count" @change="changePage" />
    </NSpace>

    <GrowthLevelModal ref="modalRef" modal-name="成长等级" @saved="getList" />
  </n-card>
</template>
