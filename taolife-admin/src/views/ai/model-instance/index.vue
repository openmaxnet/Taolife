<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getAiModelPage, modifyAiModelStatus, removeAiModel } from '@/service/api/ai'
import { NButton, NInput, NPopconfirm, NSpace, NSwitch, NTag } from 'naive-ui'
import TableModal from './components/TableModal.vue'

const modalRef = ref()

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = {
  providerId: null as string | null,
  modelType: null as string | null,
  keyword: '',
}
const model = ref({ ...initialModel })
function handleResetSearch() {
  model.value = { ...initialModel }
}

const columns: DataTableColumns<Entity.AiModelVO> = [
  {
    title: '模型编码',
    align: 'center',
    key: 'modelCode',
    width: 150,
  },
  {
    title: '模型名称',
    align: 'center',
    key: 'modelName',
    width: 150,
  },
  {
    title: '厂商',
    align: 'center',
    key: 'providerName',
    width: 120,
  },
  {
    title: '类型',
    align: 'center',
    key: 'modelType',
    width: 100,
    render: (row) => (row.modelType === 'chat' ? '聊天' : '向量'),
  },
  {
    title: '默认',
    align: 'center',
    key: 'isDefault',
    width: 80,
    render: (row) => (row.isDefault === 1 ? '是' : '否'),
  },
  {
    title: '思考模式',
    align: 'center',
    key: 'supportsThinking',
    width: 90,
    render: (row) => {
      return row.supportsThinking === 1
        ? <NTag size="small" type="success">支持</NTag>
        : <NTag size="small">不支持</NTag>
    },
  },
  {
    title: '图像输入',
    align: 'center',
    key: 'supportsImage',
    width: 90,
    render: (row) => {
      return row.supportsImage === 1
        ? <NTag size="small" type="success">支持</NTag>
        : <NTag size="small">不支持</NTag>
    },
  },
  {
    title: '状态',
    align: 'center',
    key: 'status',
    width: 100,
    render: (row) => {
      return (
        <NSwitch
          value={row.status}
          checked-value={1}
          unchecked-value={0}
          onUpdateValue={(value: 0 | 1) => handleUpdateStatus(value, row.id)}
        >
          {{ checked: () => '启用', unchecked: () => '禁用' }}
        </NSwitch>
      )
    },
  },
  {
    title: '创建时间',
    align: 'center',
    key: 'createTime',
    width: 180,
  },
  {
    title: '操作',
    align: 'center',
    key: 'actions',
    width: 180,
    render: (row) => {
      return (
        <NSpace justify="center">
          <NButton size="small" onClick={() => modalRef.value.openModal('edit', row)}>
            编辑
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDelete(row.id)}>
            {{
              default: () => '确认删除？',
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
const listData = ref<Entity.AiModelVO[]>([])

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifyAiModelStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].status = value
  window.$message.success(value === 1 ? '已启用' : '已禁用')
}

async function handleDelete(id: string) {
  await removeAiModel(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getAiModelPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    providerId: model.value.providerId || undefined,
    modelType: model.value.modelType || undefined,
    keyword: model.value.keyword || undefined,
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
      <n-form :model="model" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="类型" path="modelType">
            <n-select
              v-model:value="model.modelType"
              :options="[
                { label: '全部', value: null },
                { label: '聊天', value: 'chat' },
                { label: '向量', value: 'embedding' },
              ]"
              placeholder="请选择"
              clearable
              style="width: 150px"
            />
          </n-form-item>
          <n-form-item label="关键词" path="keyword">
            <n-input v-model:value="model.keyword" placeholder="模型名称搜索" />
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
        <NSpace>
          <NButton type="primary" @click="modalRef.openModal('add')">
            <template #icon>
              <icon-park-outline-add-one />
            </template>
            新建模型实例
          </NButton>
        </NSpace>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>

  <TableModal ref="modalRef" modal-name="模型实例" @saved="getList" />
</template>
