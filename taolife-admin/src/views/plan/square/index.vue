<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getSquarePage, modifySquareStatus } from '@/service'
import { NButton, NSpace, NTag } from 'naive-ui'
import CommentDrawer from './components/CommentDrawer.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const commentDrawerRef = ref()

const statusMap: Record<number, { label: string, type: string }> = {
  1: { label: '正常', type: 'success' },
  2: { label: '隐藏', type: 'warning' },
  3: { label: '删除', type: 'error' },
}

async function handleModifyStatus(id: string, status: number) {
  await modifySquareStatus(id, status)
  window.$message.success('操作成功')
  getList()
}

const columns: DataTableColumns<Entity.PlanSquare> = [
  { title: '昵称', key: 'nickname', width: 100 },
  { title: '体质', key: 'constitutionName', width: 80 },
  { title: '方案摘要', key: 'planSummary', ellipsis: { tooltip: true } },
  {
    title: '完成率',
    key: 'completionRate',
    width: 90,
    render: row => `${((row.completionRate ?? 0) * 100).toFixed(0)}%`,
  },
  { title: '点赞', key: 'likeCount', width: 60, align: 'center' },
  { title: '收藏', key: 'collectCount', width: 60, align: 'center' },
  { title: '评论', key: 'commentCount', width: 60, align: 'center' },
  {
    title: '状态',
    key: 'status',
    width: 80,
    render: (row) => {
      const item = statusMap[row.status ?? 0] ?? { label: '-', type: 'default' }
      return <NTag type={item.type as any} size="small">{item.label}</NTag>
    },
  },
  {
    title: '操作',
    key: 'actions',
    width: 220,
    align: 'center',
    render: (row) => {
      return (
        <NSpace justify="center">
          <NButton size="small" onClick={() => commentDrawerRef.value?.open(row.id!)}>
            评论
          </NButton>
          {row.status === 1 && (
            <NButton size="small" type="warning" onClick={() => handleModifyStatus(row.id!, 2)}>
              隐藏
            </NButton>
          )}
          {row.status === 2 && (
            <NButton size="small" type="info" onClick={() => handleModifyStatus(row.id!, 1)}>
              恢复
            </NButton>
          )}
        </NSpace>
      )
    },
  },
]

const listData = ref<Entity.PlanSquare[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getSquarePage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getList()
}

onMounted(() => {
  getList()
})
</script>

<template>
  <NSpace vertical>
    <n-card>
      <template #header>
        方案广场
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
    <CommentDrawer ref="commentDrawerRef" />
  </NSpace>
</template>
