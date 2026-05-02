<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getExchangeRecordPage } from '@/service'
import { NTag } from 'naive-ui'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const statusMap: Record<number, { label: string; type: 'default' | 'info' | 'success' | 'warning' | 'error' }> = {
  0: { label: '待处理', type: 'warning' },
  1: { label: '成功', type: 'success' },
  2: { label: '已退还', type: 'info' },
}

const searchModel = ref({
  accountId: '',
  status: undefined as number | undefined,
})

const statusOptions = [
  { label: '全部状态', value: undefined },
  { label: '待处理', value: 0 },
  { label: '成功', value: 1 },
  { label: '已退还', value: 2 },
]

const columns: DataTableColumns<Entity.PointsExchangeAdminVO> = [
  { title: '用户ID', key: 'accountId', width: 240, ellipsis: { tooltip: true } },
  { title: '商品ID', key: 'goodsId', width: 200, ellipsis: { tooltip: true } },
  { title: '消耗积分', key: 'pointsCost', width: 90, align: 'center' },
  { title: '兑换值', key: 'exchangeValue', width: 80, align: 'center' },
  { title: '业务ID', key: 'businessId', width: 200, ellipsis: { tooltip: true } },
  {
    title: '状态',
    key: 'status',
    width: 80,
    render: row => {
      const item = statusMap[row.status ?? 0]
      return item ? <NTag type={item.type} size="small">{item.label}</NTag> : '-'
    },
  },
  { title: '创建时间', key: 'createTime', width: 170 },
]

const listData = ref<Entity.PointsExchangeAdminVO[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getExchangeRecordPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    accountId: searchModel.value.accountId || undefined,
    status: searchModel.value.status,
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

function handleResetSearch() {
  searchModel.value = { accountId: '', status: undefined }
  currentPage.value = 1
  getList()
}

onMounted(() => {
  getList()
})
</script>

<template>
  <NSpace vertical>
    <n-card>
      <n-form :model="searchModel" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="用户ID" path="accountId">
            <n-input v-model:value="searchModel.accountId" placeholder="请输入用户ID" clearable style="width: 200px" />
          </n-form-item>
          <n-form-item label="状态" path="status">
            <n-select
              v-model:value="searchModel.status"
              :options="statusOptions"
              placeholder="全部状态"
              clearable
              style="width: 120px"
            />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" @click="() => { currentPage = 1; getList() }">
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

    <n-card>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" :row-key="(row: Entity.PointsExchangeAdminVO) => row.id ?? ''" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
