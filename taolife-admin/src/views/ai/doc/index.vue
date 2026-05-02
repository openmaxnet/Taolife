<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getDocumentPage, removeDocument } from '@/service/api/ai'
import { NButton, NPopconfirm, NSelect, NSpace, NTag } from 'naive-ui'
import TableModal from './components/TableModal.vue'
import PreviewModal from './components/PreviewModal.vue'

const modalRef = ref()
const previewRef = ref()
const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const category = ref<string | null>(null)
const keyword = ref('')

const columns: DataTableColumns<Entity.KnowledgeVO> = [
  { title: '标题', align: 'center', key: 'title', ellipsis: { tooltip: true } },
  { title: '分类', align: 'center', key: 'categoryName', width: 100,
    render: (row) => <NTag size="small">{row.categoryName || '-'}</NTag> },
  { title: '标签', align: 'center', key: 'tags', width: 150,
    render: (row) => row.tags?.map((t: string) => <NTag size="small" style="margin-right: 4px">{t}</NTag>) },
  { title: '来源', align: 'center', key: 'source', width: 100, ellipsis: { tooltip: true } },
  { title: '创建时间', align: 'center', key: 'createdAt', width: 180 },
  {
    title: '操作', align: 'center', key: 'actions', width: 240,
    render: (row) => (
      <NSpace justify="center">
        <NButton size="small" onClick={() => previewRef.value.openModal(row)}>预览</NButton>
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
const listData = ref<Entity.KnowledgeVO[]>([])

async function handleDelete(id: string) {
  await removeDocument(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getDocumentPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    category: category.value || undefined,
    keyword: keyword.value || undefined,
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
      <n-form label-placement="left" inline>
        <n-flex>
          <n-form-item label="分类" path="category">
            <NSelect
              v-model:value="category"
              :options="[
                { label: '全部', value: null },
                { label: '体质', value: 'constitution' },
                { label: '食疗', value: 'food' },
                { label: '穴位', value: 'acupoint' },
                { label: '养生', value: 'health' },
              ]"
              placeholder="请选择"
              clearable
              style="width: 150px"
              @update:value="getList"
            />
          </n-form-item>
          <n-form-item label="关键词" path="keyword">
            <n-input v-model:value="keyword" placeholder="标题搜索" @keyup.enter="getList" />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" @click="getList">
              <template #icon><icon-park-outline-search /></template>
              搜索
            </NButton>
          </n-flex>
        </n-flex>
      </n-form>
    </n-card>

    <n-card class="flex-1">
      <template #header>
        <NButton type="primary" @click="modalRef.openModal('add')">
          <template #icon><icon-park-outline-add-one /></template>
          新建文档
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>

  <TableModal ref="modalRef" modal-name="文档" @saved="getList" />
  <PreviewModal ref="previewRef" />
</template>
