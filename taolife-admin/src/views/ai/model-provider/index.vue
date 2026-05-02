<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getAiProviderPage, modifyAiProviderStatus, removeAiProvider } from '@/service/api/ai'
import { NButton, NInput, NPopconfirm, NSpace, NSwitch, NTag } from 'naive-ui'
import TableModal from './components/TableModal.vue'

const modalRef = ref()

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = {
  providerType: null as string | null,
  keyword: '',
}
const model = ref({ ...initialModel })
function handleResetSearch() {
  model.value = { ...initialModel }
}

const columns: DataTableColumns<Entity.AiProviderVO> = [
  {
    title: '厂商编码',
    align: 'center',
    key: 'providerCode',
    width: 120,
  },
  {
    title: '厂商名称',
    align: 'center',
    key: 'providerName',
    width: 120,
  },
  {
    title: '类型',
    align: 'center',
    key: 'providerType',
    width: 100,
    render: (row) => {
      const map: Record<string, string> = { chat: '聊天', embedding: '向量', image: '图像' }
      return map[row.providerType] || row.providerType
    },
  },
  {
    title: 'API Endpoint',
    align: 'center',
    key: 'apiEndpoint',
    width: 200,
    ellipsis: { tooltip: true },
  },
  {
    title: 'API Key',
    align: 'center',
    key: 'apiKeyMasked',
    width: 150,
    ellipsis: { tooltip: true },
    render: (row) => row.apiKeyMasked || '******',
  },
  {
    title: '默认',
    align: 'center',
    key: 'isDefault',
    width: 80,
    render: (row) => (row.isDefault === 1 ? '是' : '否'),
  },
  {
    title: '优先级',
    align: 'center',
    key: 'priority',
    width: 80,
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
const listData = ref<Entity.AiProviderVO[]>([])

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifyAiProviderStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].status = value
  window.$message.success(value === 1 ? '已启用' : '已禁用')
}

async function handleDelete(id: string) {
  await removeAiProvider(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getAiProviderPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    providerType: model.value.providerType || undefined,
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
          <n-form-item label="类型" path="providerType">
            <n-select
              v-model:value="model.providerType"
              :options="[
                { label: '全部', value: null },
                { label: '聊天', value: 'chat' },
                { label: '向量', value: 'embedding' },
                { label: '图像', value: 'image' },
              ]"
              placeholder="请选择"
              clearable
              style="width: 150px"
            />
          </n-form-item>
          <n-form-item label="关键词" path="keyword">
            <n-input v-model:value="model.keyword" placeholder="厂商名称搜索" />
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
            新建厂商
          </NButton>
        </NSpace>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>

  <TableModal ref="modalRef" modal-name="AI厂商" @saved="getList" />
</template>
