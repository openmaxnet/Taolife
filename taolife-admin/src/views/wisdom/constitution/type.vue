<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getConstitutionTypePage, modifyConstitutionTypeStatus, removeConstitutionType } from '@/service/api/wisdom'
import { NButton, NPopconfirm, NSpace, NSwitch, NTag } from 'naive-ui'
import TypeModal from './components/TypeModal.vue'

const modalRef = ref()
const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const columns: DataTableColumns<Entity.ConstitutionTypeVO> = [
  { title: '编码', align: 'center', key: 'code', width: 100 },
  { title: '名称', align: 'center', key: 'name', width: 100 },
  { title: '英文名', align: 'center', key: 'nameEn', width: 180 },
  { title: '描述', align: 'center', key: 'description', ellipsis: { tooltip: true } },
  { title: '排序', align: 'center', key: 'sortOrder', width: 80 },
  {
    title: '状态', align: 'center', key: 'isDisabled', width: 100,
    render: (row) => (
      <NSwitch
        value={row.isDisabled}
        checked-value={0}
        unchecked-value={1}
        onUpdate:value={(value: 0 | 1) => handleUpdateStatus(value, row.id)}
      >
        {{ checked: () => '启用', unchecked: () => '禁用' }}
      </NSwitch>
    ),
  },
  { title: '创建时间', align: 'center', key: 'createTime', width: 180 },
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
const listData = ref<Entity.ConstitutionTypeVO[]>([])

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifyConstitutionTypeStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1) listData.value[index].isDisabled = value
  window.$message.success(value === 0 ? '已启用' : '已禁用')
}

async function handleDelete(id: string) {
  await removeConstitutionType(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getConstitutionTypePage({ pageNo: currentPage.value, pageSize: currentPageSize.value }).then((res) => {
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
          新建体质类型
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
  <TypeModal ref="modalRef" modal-name="体质类型" @saved="getList" />
</template>
