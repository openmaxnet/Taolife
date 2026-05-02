<script setup lang="tsx">
import type { DataTableColumns, FormInst } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getArticlePage, modifyArticleStatus, removeArticle } from '@/service'
import { NButton, NPopconfirm, NSpace, NSwitch, NTag } from 'naive-ui'
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

async function handleDeleteArticle(id: string) {
  await removeArticle(id)
  window.$message.success('删除成功')
  getArticleList()
}

const categoryMap: Record<number, string> = {
  1: '养生',
  2: '食疗',
  3: '运动',
  4: '穴位',
  5: '经络',
}

const columns: DataTableColumns<Entity.Article> = [
  {
    title: '标题',
    align: 'center',
    key: 'title',
  },
  {
    title: '分类',
    align: 'center',
    key: 'categoryName',
  },
  {
    title: '作者',
    align: 'center',
    key: 'author',
  },
  {
    title: '阅读量',
    align: 'center',
    key: 'readCount',
  },
  {
    title: '点赞量',
    align: 'center',
    key: 'likeCount',
  },
  {
    title: '是否推荐',
    align: 'center',
    key: 'isRecommended',
    render: (row) => {
      const isRecommended = row.isRecommended === 1
      return (
        <NTag type={isRecommended ? 'success' : 'default'}>
          {isRecommended ? '推荐' : '未推荐'}
        </NTag>
      )
    },
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
            handleUpdateStatus(value, row.id!)}
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
          <NPopconfirm onPositiveClick={() => handleDeleteArticle(row.id ?? '')}>
            {{
              default: () => '确认删除该文章？',
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
const listData = ref<Entity.Article[]>([])

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifyArticleStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isDisabled = value
  window.$message.success(value === 0 ? '已启用' : '已禁用')
}

async function getArticleList() {
  startLoading()
  await getArticlePage({
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
  getArticleList()
})

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getArticleList()
}
</script>

<template>
  <NSpace vertical>
    <n-card>
      <n-form ref="formRef" :model="model" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="标题搜索" path="keyword">
            <n-input v-model:value="model.keyword" placeholder="请输入" />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" @click="getArticleList">
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
          新建文章
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>

      <TableModal ref="modalRef" modal-name="文章" @saved="getArticleList" />
    </n-card>
  </NSpace>
</template>
