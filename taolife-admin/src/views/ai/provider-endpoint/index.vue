<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import {
  getAiProviderEndpointPage,
  getEnabledAiProviderList,
  modifyAiProviderEndpointStatus,
  removeAiProviderEndpoint,
} from '@/service/api/ai'
import { NButton, NInput, NPopconfirm, NSpace, NSelect, NSwitch } from 'naive-ui'
import TableModal from './components/TableModal.vue'

const modalRef = ref()

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = {
  providerId: null as string | null,
  endpointType: null as string | null,
  keyword: '',
}
const model = ref({ ...initialModel })
function handleResetSearch() {
  model.value = { ...initialModel }
}

const providerOptions = ref<{ label: string; value: string }[]>([])

const ENDPOINT_TYPE_MAP: Record<string, string> = {
  model_api: '模型API',
  tool_api: '工具API',
  agent_api: 'Agent API',
  file_api: '文件API',
  batch_api: '批处理API',
  knowledge_api: '知识库API',
  realtime_api: '实时API',
}

const REQUEST_TYPE_OPTIONS = ['GET', 'POST', 'PUT', 'DELETE', 'WSS']

const columns: DataTableColumns<Entity.AiProviderEndpointVO> = [
  {
    title: '厂商名称',
    align: 'center',
    key: 'providerName',
    width: 120,
  },
  {
    title: '端点类型',
    align: 'center',
    key: 'endpointTypeName',
    width: 120,
    render: (row) => ENDPOINT_TYPE_MAP[row.endpointType] || row.endpointType,
  },
  {
    title: '端点URI',
    align: 'center',
    key: 'endpointUri',
    width: 200,
    ellipsis: { tooltip: true },
  },
  {
    title: '请求类型',
    align: 'center',
    key: 'requestType',
    width: 100,
  },
  {
    title: '超时(ms)',
    align: 'center',
    key: 'timeoutMs',
    width: 100,
  },
  {
    title: '重试次数',
    align: 'center',
    key: 'retryTimes',
    width: 100,
  },
  {
    title: '状态',
    align: 'center',
    key: 'isEnabled',
    width: 100,
    render: (row) => {
      return (
        <NSwitch
          value={row.isEnabled}
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
const listData = ref<Entity.AiProviderEndpointVO[]>([])

async function loadProviderOptions() {
  const res = await getEnabledAiProviderList('')
  providerOptions.value = res.data.map(item => ({
    label: item.providerName,
    value: item.id,
  }))
}

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifyAiProviderEndpointStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isEnabled = value
  window.$message.success(value === 1 ? '已启用' : '已禁用')
}

async function handleDelete(id: string) {
  await removeAiProviderEndpoint(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getAiProviderEndpointPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    providerId: model.value.providerId || undefined,
    endpointType: model.value.endpointType || undefined,
    keyword: model.value.keyword || undefined,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

onMounted(() => {
  loadProviderOptions()
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
          <n-form-item label="厂商" path="providerId">
            <NSelect
              v-model:value="model.providerId"
              :options="providerOptions"
              placeholder="请选择"
              clearable
              filterable
              style="width: 150px"
            />
          </n-form-item>
          <n-form-item label="端点类型" path="endpointType">
            <NSelect
              v-model:value="model.endpointType"
              :options="[
                { label: '全部', value: null },
                { label: '模型API', value: 'model_api' },
                { label: '工具API', value: 'tool_api' },
                { label: 'Agent API', value: 'agent_api' },
                { label: '文件API', value: 'file_api' },
                { label: '批处理API', value: 'batch_api' },
                { label: '知识库API', value: 'knowledge_api' },
                { label: '实时API', value: 'realtime_api' },
              ]"
              placeholder="请选择"
              clearable
              style="width: 150px"
            />
          </n-form-item>
          <n-form-item label="关键词" path="keyword">
            <NInput v-model:value="model.keyword" placeholder="描述搜索" />
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
            新建端点配置
          </NButton>
        </NSpace>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>

  <TableModal ref="modalRef" modal-name="端点配置" @saved="getList" />
</template>
