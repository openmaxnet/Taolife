<script setup lang="tsx">
import type { DataTableColumns, FormInst } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getSensitiveWordPage, modifySensitiveWordStatus, removeSensitiveWord } from '@/service/api/ai'
import { NButton, NPopconfirm, NSpace, NSwitch, NTag } from 'naive-ui'
import TableModal from './components/TableModal.vue'
import ImportModal from './components/ImportModal.vue'

const modalRef = ref()
const importRef = ref()

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = {
  wordType: null as number | null,
  keyword: '',
}
const model = ref({ ...initialModel })
function handleResetSearch() {
  model.value = { ...initialModel }
}

const columns: DataTableColumns<Entity.SensitiveWordVO> = [
  {
    title: '敏感词',
    align: 'center',
    key: 'word',
    width: 120,
  },
  {
    title: '类型',
    align: 'center',
    key: 'wordTypeName',
    width: 100,
  },
  {
    title: '严重程度',
    align: 'center',
    key: 'severityName',
    width: 100,
  },
  {
    title: '处理方式',
    align: 'center',
    key: 'actionTypeName',
    width: 100,
  },
  {
    title: '替换词',
    align: 'center',
    key: 'replaceWord',
    width: 100,
    ellipsis: { tooltip: true },
    render: (row) => row.replaceWord || '-',
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
          <NButton
            size="small"
            onClick={() => modalRef.value.openModal('edit', row)}
          >
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
const listData = ref<Entity.SensitiveWordVO[]>([])

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifySensitiveWordStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isEnabled = value
  window.$message.success(value === 1 ? '已启用' : '已禁用')
}

async function handleDelete(id: string) {
  await removeSensitiveWord(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getSensitiveWordPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    wordType: model.value.wordType || undefined,
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
          <n-form-item label="类型" path="wordType">
            <n-select
              v-model:value="model.wordType"
              :options="[
                { label: '全部', value: null },
                { label: '医疗诊断', value: 1 },
                { label: '政治敏感', value: 2 },
                { label: '不当内容', value: 3 },
              ]"
              placeholder="请选择"
              clearable
              style="width: 150px"
            />
          </n-form-item>
          <n-form-item label="关键词" path="keyword">
            <n-input v-model:value="model.keyword" placeholder="敏感词搜索" />
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
            新建敏感词
          </NButton>
          <NButton @click="importRef.openModal()">
            <template #icon>
              <icon-park-outline-upload />
            </template>
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

  <TableModal ref="modalRef" modal-name="敏感词" @saved="getList" />
  <ImportModal ref="importRef" @success="getList" />
</template>
