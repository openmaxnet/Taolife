<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getSessionPage, removeSession } from '@/service/api/ai'
import { NButton, NPopconfirm, NSpace } from 'naive-ui'
import SessionDetail from './components/SessionDetail.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = {
  accountId: '',
  keyword: '',
}
const model = ref({ ...initialModel })
function handleResetSearch() {
  model.value = { ...initialModel }
}

const detailRef = ref()
const sessionId = ref('')

const columns: DataTableColumns<Entity.SessionAdminVO> = [
  {
    title: '会话ID',
    align: 'center',
    key: 'sessionId',
    width: 280,
    ellipsis: { tooltip: true },
  },
  {
    title: '用户ID',
    align: 'center',
    key: 'accountId',
    width: 240,
    ellipsis: { tooltip: true },
  },
  {
    title: '会话标题',
    align: 'center',
    key: 'sessionTitle',
    width: 200,
    ellipsis: { tooltip: true },
  },
  {
    title: '体质',
    align: 'center',
    key: 'constitutionName',
    width: 100,
    render: (row) => row.constitutionName || '-',
  },
  {
    title: '消息数',
    align: 'center',
    key: 'messageCount',
    width: 80,
  },
  {
    title: '模型',
    align: 'center',
    key: 'chatModel',
    width: 100,
    render: (row) => row.chatModel || '-',
  },
  {
    title: '最后消息',
    align: 'center',
    key: 'lastMessage',
    ellipsis: { tooltip: true },
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
    width: 200,
    render: (row) => {
      return (
        <NSpace justify="center">
          <NButton
            size="small"
            onClick={() => {
              sessionId.value = row.sessionId
              detailRef.value.open()
            }}
          >
            查看详情
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDelete(row.sessionId)}>
            {{
              default: () => '确认删除该会话？',
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
const listData = ref<Entity.SessionAdminVO[]>([])

async function handleDelete(id: string) {
  await removeSession(id)
  window.$message.success('删除成功')
  getSessionList()
}

async function getSessionList() {
  startLoading()
  await getSessionPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    accountId: model.value.accountId || undefined,
    keyword: model.value.keyword || undefined,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

onMounted(() => {
  getSessionList()
})

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getSessionList()
}
</script>

<template>
  <NSpace vertical>
    <n-card>
      <n-form :model="model" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="用户ID" path="accountId">
            <n-input v-model:value="model.accountId" placeholder="精确搜索" />
          </n-form-item>
          <n-form-item label="关键词" path="keyword">
            <n-input v-model:value="model.keyword" placeholder="标题/消息搜索" />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" @click="getSessionList">
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
      <!-- <template #header>
        <NTag type="info">会话列表</NTag>
      </template> -->
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>

  <SessionDetail ref="detailRef" :session-id="sessionId" />
</template>
