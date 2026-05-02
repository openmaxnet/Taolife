<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getQuestionRulePage, modifyQuestionRuleStatus, removeQuestionRule } from '@/service/api/ai'
import { NButton, NPopconfirm, NSpace, NSwitch, NTag } from 'naive-ui'
import TableModal from './components/TableModal.vue'
import ImportModal from './components/ImportModal.vue'

const modalRef = ref()
const importRef = ref()

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = { keyword: '' }
const model = ref({ ...initialModel })

const columns: DataTableColumns<Entity.QuestionRuleVO> = [
  {
    title: '分类编码',
    align: 'center',
    key: 'categoryCode',
    width: 120,
  },
  {
    title: '分类名称',
    align: 'center',
    key: 'categoryName',
    width: 120,
  },
  {
    title: '关键词',
    align: 'center',
    key: 'keywords',
    ellipsis: { tooltip: true },
    render: (row) => {
      if (!row.keywords || row.keywords.length === 0) return '-'
      return row.keywords.slice(0, 3).map((k: string) => (
        <NTag size="small" style="margin-right: 4px">{k}</NTag>
      ))
    },
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
    key: 'isEnabled',
    width: 100,
    render: (row) => (
      <NSwitch
        value={row.isEnabled}
        checked-value={1}
        unchecked-value={0}
        onUpdateValue={(value: 0 | 1) => handleUpdateStatus(value, row.id)}
      >
        {{ checked: () => '启用', unchecked: () => '禁用' }}
      </NSwitch>
    ),
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
const listData = ref<Entity.QuestionRuleVO[]>([])

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifyQuestionRuleStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1) listData.value[index].isEnabled = value
  window.$message.success(value === 1 ? '已启用' : '已禁用')
}

async function handleDelete(id: string) {
  await removeQuestionRule(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getQuestionRulePage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    keyword: model.value.keyword || undefined,
  }).then((res) => {
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
    <n-card>
      <n-form :model="model" label-placement="left" inline>
        <n-flex>
          <n-form-item label="关键词" path="keyword">
            <n-input v-model:value="model.keyword" placeholder="分类名称搜索" />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" @click="getList">
              <template #icon><icon-park-outline-search /></template>
              搜索
            </NButton>
            <NButton strong secondary @click="model = { ...initialModel }">
              <template #icon><icon-park-outline-redo /></template>
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
            <template #icon><icon-park-outline-add-one /></template>
            新建规则
          </NButton>
          <NButton @click="importRef.openModal()">
            <template #icon><icon-park-outline-upload /></template>
            批量导入
          </NButton>
        </NSpace>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>

  <TableModal ref="modalRef" modal-name="规则" @saved="getList" />
  <ImportModal ref="importRef" @success="getList" />
</template>
