<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getCommentPage, removeComment, modifyCommentStatus } from '@/service'
import { NButton, NPopconfirm, NSpace, NTag } from 'naive-ui'

const planSquareId = ref('')

const { bool: visible, setTrue: show } = useBoolean(false)
const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const statusMap: Record<number, { label: string, type: string }> = {
  1: { label: '正常', type: 'success' },
  2: { label: '隐藏', type: 'warning' },
  3: { label: '删除', type: 'error' },
}

async function handleDelete(id: string) {
  await removeComment(id)
  window.$message.success('删除成功')
  getList()
}

async function handleModifyStatus(id: string, status: number) {
  await modifyCommentStatus(id, status)
  window.$message.success('操作成功')
  getList()
}

const columns: DataTableColumns<Entity.PlanComment> = [
  { title: '昵称', key: 'nickname', width: 100 },
  { title: '内容', key: 'content', ellipsis: { tooltip: true } },
  { title: '点赞', key: 'likeCount', width: 60, align: 'center' },
  { title: '回复', key: 'replyCount', width: 60, align: 'center' },
  {
    title: '状态',
    key: 'status',
    width: 80,
    render: (row) => {
      const item = statusMap[row.status ?? 0] ?? { label: '-', type: 'default' }
      return <NTag type={item.type as any} size="small">{item.label}</NTag>
    },
  },
  { title: '创建时间', key: 'createTime', width: 180 },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    align: 'center',
    render: (row) => {
      return (
        <NSpace justify="center">
          <NPopconfirm onPositiveClick={() => handleDelete(row.id!)}>
            {{
              default: () => '确认删除该评论？',
              trigger: () => <NButton size="small" type="error">删除</NButton>,
            }}
          </NPopconfirm>
          {row.status === 1 && (
            <NButton size="small" type="warning" onClick={() => handleModifyStatus(row.id!, 2)}>
              隐藏
            </NButton>
          )}
          {row.status === 2 && (
            <NButton size="small" type="info" onClick={() => handleModifyStatus(row.id!, 1)}>
              显示
            </NButton>
          )}
        </NSpace>
      )
    },
  },
]

const listData = ref<Entity.PlanComment[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getCommentPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    planSquareId: planSquareId.value,
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

function open(id: string) {
  planSquareId.value = id
  currentPage.value = 1
  show()
  getList()
}

defineExpose({ open })
</script>

<template>
  <n-drawer v-model:show="visible" :width="800">
    <n-drawer-content title="评论列表">
      <n-data-table :columns="columns" :data="listData" :loading="loading" />
      <Pagination :count="count" @change="changePage" class="mt-12px" />
    </n-drawer-content>
  </n-drawer>
</template>
