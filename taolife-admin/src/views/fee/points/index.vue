<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getPointsRecordPage } from '@/service'
import { NButton, NFlex } from 'naive-ui'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const pointsTypeMap: Record<number, string> = {
  1: '签到',
  2: '方案',
  3: '分享',
  4: '消费',
  5: '过期',
}

const searchModel = ref({
  accountId: '',
})

function handleResetSearch() {
  searchModel.value = { accountId: '' }
  currentPage.value = 1
  getList()
}

const columns: DataTableColumns<Entity.PointsRecord> = [
  { title: '用户ID', key: 'accountId', width: 240 },
  {
    title: '积分变动',
    key: 'pointsChange',
    width: 120,
    render: (row) => {
      const val = row.pointsChange ?? 0
      const color = val >= 0 ? '#18a058' : '#d03050'
      const prefix = val >= 0 ? '+' : ''
      return <span style={{ color }}>{prefix}{val}</span>
    },
  },
  {
    title: '类型',
    key: 'pointsType',
    width: 80,
    render: row => pointsTypeMap[row.pointsType ?? 0] ?? '-',
  },
  { title: '业务类型', key: 'businessType', width: 160 },
  { title: '余额', key: 'balanceAfter', width: 120, align: 'center' },
  { title: '备注', key: 'remark', ellipsis: { tooltip: true } },
  { title: '创建时间', key: 'createTime', width: 180 },
]

const listData = ref<Entity.PointsRecord[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getPointsRecordPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    accountId: searchModel.value.accountId || undefined,
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
      <n-form :model="searchModel" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="用户ID" path="accountId">
            <n-input v-model:value="searchModel.accountId" placeholder="请输入用户ID" />
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

    <n-card>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
