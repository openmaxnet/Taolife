<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getBannerPage, modifyBannerStatus, removeBanner } from '@/service/api/fee'
import { NButton, NPopconfirm, NSpace, NSwitch } from 'naive-ui'
import BannerModal from './components/BannerModal.vue'

const modalRef = ref()
const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const columns: DataTableColumns<Entity.BannerVO> = [
  { title: '标题', align: 'center', key: 'title', width: 150 },
  { title: '副标题', align: 'center', key: 'subtitle', ellipsis: { tooltip: true } },
  {
    title: '图片', align: 'center', key: 'imageUrl', width: 100,
    render: (row) => row.imageUrl ? <n-image width={60} src={row.imageUrl} /> : '-',
  },
  { title: '排序', align: 'center', key: 'sortOrder', width: 80 },
  {
    title: '状态', align: 'center', key: 'status', width: 100,
    render: (row) => {
      return (
        <NSwitch
          value={row.status}
          checked-value={1}
          unchecked-value={0}
          onUpdateValue={(value: 0 | 1) => handleStatusChange(value, row.id!)}
        >
          {{ checked: () => '启用', unchecked: () => '禁用' }}
        </NSwitch>
      )
    },
  },
  { title: '创建时间', align: 'center', key: 'createTime', width: 180 },
  {
    title: '操作', align: 'center', key: 'actions', width: 180,
    render: (row) => (
      <NSpace justify="center">
        <NButton size="small" onClick={() => modalRef.value.openModal('edit', row)}>编辑</NButton>
        <NPopconfirm onPositiveClick={() => handleDelete(row.id!)}>
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
const listData = ref<Entity.BannerVO[]>([])

async function handleStatusChange(value: 0 | 1, id: string) {
  await modifyBannerStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1) listData.value[index].status = value
  window.$message.success(value === 1 ? '已启用' : '已禁用')
}

async function handleDelete(id: string) {
  await removeBanner(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getBannerPage({ pageNo: currentPage.value, pageSize: currentPageSize.value }).then((res) => {
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
          新建轮播
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
  <BannerModal ref="modalRef" modal-name="轮播" @saved="getList" />
</template>
