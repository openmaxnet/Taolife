<script setup lang="tsx">
import type { DataTableColumns, FormInst } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getAcupointCombinePage, modifyAcupointCombineStatus, removeAcupointCombine } from '@/service'
import { NButton, NPopconfirm, NSpace, NSwitch, NTag } from 'naive-ui'
import TableModal from './components/TableModal.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = {
  keyword: '',
  category: undefined as number | undefined,
}
const model = ref({ ...initialModel })
function handleResetSearch() {
  model.value = { ...initialModel }
}

const categoryOptions = [
  { label: '体质调理', value: 1 },
  { label: '症状调理', value: 2 },
  { label: '季节养生', value: 3 },
  { label: '日常保健', value: 4 },
]

const formRef = ref<FormInst | null>()
const modalRef = ref()

async function handleDelete(id: string) {
  await removeAcupointCombine(id)
  window.$message.success('删除成功')
  getList()
}

function getCategoryName(category?: number) {
  return categoryOptions.find(o => o.value === category)?.label ?? ''
}

const columns: DataTableColumns<Entity.AcupointCombine> = [
  {
    title: '名称',
    align: 'center',
    key: 'name',
  },
  {
    title: '分类',
    align: 'center',
    key: 'category',
    render: row => getCategoryName(row.category),
  },
  {
    title: '目标穴位',
    align: 'center',
    key: 'acupointNames',
    width: 300,
    render: (row) => {
      if (!row.acupointNames)
        return '-'
      try {
        const names: string[] = JSON.parse(row.acupointNames)
        return names.map(name => <NTag size="small" type="info">{name}</NTag>)
      }
      catch {
        return row.acupointNames
      }
    },
  },
  {
    title: '功效',
    align: 'center',
    key: 'efficacy',
    ellipsis: { tooltip: true },
  },
  {
    title: '状态',
    align: 'center',
    key: 'isDisabled',
    render: (row) => {
      return (
        <NSwitch
          value={row.isDisabled === 0}
          checked-value={0}
          unchecked-value={1}
          onUpdateValue={(value: 0 | 1) =>
            handleUpdateDisabled(value, row.id!)}
        >
          {{ checked: () => '启用', unchecked: () => '禁用' }}
        </NSwitch>
      )
    },
  },
  {
    title: '操作',
    align: 'center',
    key: 'actions',
    width: 300,
    render: (row) => {
      return (
        <NSpace justify="center">
          <NButton
            size="small"
            onClick={() => modalRef.value.openModal('edit', row)}
          >
            编辑
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDelete(row.id ?? '')}>
            {{
              default: () => '确认删除该穴位配伍？',
              trigger: () => <NButton size="small" type="error">删除</NButton>,
            }}
          </NPopconfirm>
        </NSpace>
      )
    },
  },
]

const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)
const listData = ref<Entity.AcupointCombine[]>([])

async function handleUpdateDisabled(value: 0 | 1, id: string) {
  await modifyAcupointCombineStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isDisabled = value
  window.$message.success(value === 0 ? '已启用' : '已禁用')
}

async function getList() {
  startLoading()
  await getAcupointCombinePage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    keyword: model.value.keyword || undefined,
    category: model.value.category || undefined,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

onMounted(() => {
  getList()
})

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getList()
}
</script>

<template>
  <NSpace vertical>
    <n-card>
      <n-form ref="formRef" :model="model" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="分类" path="category">
            <n-select v-model:value="model.category" :options="categoryOptions" clearable placeholder="全部分类" style="width: 140px" />
          </n-form-item>
          <n-form-item label="名称搜索" path="keyword">
            <n-input v-model:value="model.keyword" placeholder="请输入" />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" @click="getList">
              <template #icon>
                <icon-park-outline-search />
              </template>
              搜索
            </NButton>
            <NButton strong secondary @click="handleResetSearch">
              <template #icon>
                <icon-park-outline-redo />
              </template>
              重置
            </NButton>
          </n-flex>
        </n-flex>
      </n-form>
    </n-card>

    <n-card class="flex-1">
      <template #header>
        <NButton type="primary" @click="modalRef.openModal('add')">
          <template #icon>
            <icon-park-outline-add-one />
          </template>
          新建穴位配伍
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>

      <TableModal ref="modalRef" modal-name="穴位配伍" @saved="getList" />
    </n-card>
  </NSpace>
</template>
