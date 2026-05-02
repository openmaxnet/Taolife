<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getAiPromptTemplatePage, modifyAiPromptTemplateStatus, removeAiPromptTemplate } from '@/service/api/ai'
import { NButton, NInput, NPopconfirm, NSpace, NSwitch } from 'naive-ui'
import TableModal from './components/TableModal.vue'

const modalRef = ref()

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = {
  templateType: null as string | null,
  keyword: '',
}
const model = ref({ ...initialModel })
function handleResetSearch() {
  model.value = { ...initialModel }
}

const columns: DataTableColumns<Entity.AiPromptTemplateVO> = [
  {
    title: '模板编码',
    align: 'center',
    key: 'templateCode',
    width: 150,
  },
  {
    title: '模板名称',
    align: 'center',
    key: 'templateName',
    width: 150,
  },
  {
    title: '类型',
    align: 'center',
    key: 'templateType',
    width: 120,
    render: (row) => {
      const map: Record<string, string> = {
        system_prompt: '系统提示词',
        user_prompt: '用户提示词',
        assistant_prompt: '助手提示词',
        common_prompt: '通用提示词',
        global_context: '全局上下文',
      }
      return map[row.templateType] || row.templateType
    },
  },
  {
    title: '版本',
    align: 'center',
    key: 'version',
    width: 80,
  },
  {
    title: '内容预览',
    align: 'center',
    key: 'templateContent',
    width: 200,
    ellipsis: { tooltip: true },
    render: (row) => row.templateContent?.substring(0, 50) + '...' || '-',
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
const listData = ref<Entity.AiPromptTemplateVO[]>([])

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifyAiPromptTemplateStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isEnabled = value
  window.$message.success(value === 1 ? '已启用' : '已禁用')
}

async function handleDelete(id: string) {
  await removeAiPromptTemplate(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getAiPromptTemplatePage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    templateType: model.value.templateType || undefined,
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
          <n-form-item label="类型" path="templateType">
            <n-select
              v-model:value="model.templateType"
              :options="[
                { label: '全部', value: null },
                { label: '系统提示词', value: 'system_prompt' },
                { label: '用户提示词', value: 'user_prompt' },
                { label: '助手提示词', value: 'assistant_prompt' },
              ]"
              placeholder="请选择"
              clearable
              style="width: 150px"
            />
          </n-form-item>
          <n-form-item label="关键词" path="keyword">
            <n-input v-model:value="model.keyword" placeholder="模板名称搜索" />
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
            新建提示词模板
          </NButton>
        </NSpace>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>

  <TableModal ref="modalRef" modal-name="提示词模板" @saved="getList" />
</template>
