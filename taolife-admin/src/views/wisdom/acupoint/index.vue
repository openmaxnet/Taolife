<script setup lang="tsx">
import type { DataTableColumns, FormInst } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getAcupointPage, modifyAcupointStatus, removeAcupoint } from '@/service'
import { NButton, NPopconfirm, NSpace, NSwitch } from 'naive-ui'
import TableModal from './components/TableModal.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = {
  keyword: '',
}
const model = ref({ ...initialModel })
function handleResetSearch() {
  model.value = { ...initialModel }
}

const formRef = ref<FormInst | null>()
const modalRef = ref()

async function handleDeleteAcupoint(id: string) {
  await removeAcupoint(id)
  window.$message.success('删除成功')
  getAcupointList()
}

const columns: DataTableColumns<Entity.Acupoint> = [
  {
    title: '名称',
    align: 'center',
    key: 'name',
  },
  {
    title: '经络',
    align: 'center',
    key: 'meridianName',
  },
  {
    title: '定位',
    align: 'center',
    key: 'locationDescription',
  },
  {
    title: '功效',
    align: 'center',
    key: 'efficacy',
  },
  {
    title: '标记类型',
    align: 'center',
    key: 'markerTypeName',
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
          <NPopconfirm onPositiveClick={() => handleDeleteAcupoint(row.id ?? '')}>
            {{
              default: () => '确认删除该穴位？',
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
const listData = ref<Entity.Acupoint[]>([])

async function handleUpdateDisabled(value: 0 | 1, id: string) {
  await modifyAcupointStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isDisabled = value
  window.$message.success(value === 0 ? '已启用' : '已禁用')
}

async function getAcupointList() {
  startLoading()
  await getAcupointPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    keyword: model.value.keyword || undefined,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

onMounted(() => {
  getAcupointList()
})

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getAcupointList()
}
</script>

<template>
  <NSpace vertical>
    <n-card>
      <n-form ref="formRef" :model="model" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="关键词" path="keyword">
            <n-input v-model:value="model.keyword" placeholder="请输入" />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" @click="getAcupointList">
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
          新建穴位
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>

      <TableModal ref="modalRef" modal-name="穴位" @saved="getAcupointList" />
    </n-card>
  </NSpace>
</template>
