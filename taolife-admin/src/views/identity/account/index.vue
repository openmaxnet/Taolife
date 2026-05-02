<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getAccountPage, modifyAccountStatus } from '@/service'
import { NButton, NDatePicker, NImage, NSpace, NSelect, NTag } from 'naive-ui'
import DetailDrawer from './components/DetailDrawer.vue'
import MemberLevelModal from './components/MemberLevelModal.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const genderMap: Record<number, string> = { 0: '未知', 1: '男', 2: '女' }
const memberLevelMap: Record<number, { label: string, type: string }> = {
  0: { label: '普通用户', type: 'default' },
  1: { label: '月卡会员', type: 'warning' },
  2: { label: '年卡会员', type: 'warning' },
  3: { label: '终身会员', type: 'error' },
}

const searchModel = ref({
  keyword: '',
  memberLevel: undefined as number | undefined,
  dateRange: null as [number, number] | null,
})

const memberLevelOptions = [
  { label: '全部', value: undefined },
  { label: '普通用户', value: 0 },
  { label: '月卡会员', value: 1 },
  { label: '年卡会员', value: 2 },
  { label: '终身会员', value: 3 },
]

async function handleModifyStatus(id: string, isDisabled: number) {
  await modifyAccountStatus({ id, isDisabled })
  window.$message.success(isDisabled === 0 ? '启用成功' : '禁用成功')
  getList()
}

const columns: DataTableColumns<Entity.AccountVO> = [
  {
    title: '头像',
    key: 'avatarUrl',
    width: 60,
    render: row => row.avatarUrl
      ? <NImage width={40} height={40} src={row.avatarUrl} round />
      : <div class="w40px h40px rounded-full bg-gray-200" />,
  },
  { title: '昵称', key: 'nickname', width: 100, ellipsis: { tooltip: true } },
  { title: '手机号', key: 'phone', width: 130, ellipsis: { tooltip: true } },
  {
    title: '性别',
    key: 'gender',
    width: 60,
    align: 'center',
    render: row => genderMap[row.gender ?? 0] ?? '-',
  },
  {
    title: '会员等级',
    key: 'memberLevel',
    width: 100,
    render: (row) => {
      const item = memberLevelMap[row.memberLevel ?? 0]
      return item ? <NTag type={item.type as any} size="small">{item.label}</NTag> : '-'
    },
  },
  { title: '会员到期', key: 'memberExpireTime', width: 170, ellipsis: { tooltip: true } },
  { title: '注册时间', key: 'createTime', width: 170 },
  {
    title: '状态',
    key: 'isDisabled',
    width: 70,
    render: row => <NTag type={row.isDisabled === 0 ? 'success' : 'error'} size="small">{row.isDisabled === 0 ? '正常' : '禁用'}</NTag>,
  },
  {
    title: '操作',
    key: 'actions',
    width: 240,
    align: 'center',
    fixed: 'right',
    render: (row) => {
      return (
        <NSpace justify="center" size="small">
          <NButton size="small" onClick={() => detailDrawerRef.value?.open(row.id!)}>
            详情
          </NButton>
          <NButton size="small" onClick={() => memberLevelModalRef.value?.openModal(row.id!)}>
            会员等级
          </NButton>
          {row.isDisabled === 0
            ? <NButton size="small" type="warning" onClick={() => handleModifyStatus(row.id!, 1)}>禁用</NButton>
            : <NButton size="small" type="info" onClick={() => handleModifyStatus(row.id!, 0)}>启用</NButton>
          }
        </NSpace>
      )
    },
  },
]

const listData = ref<Entity.AccountVO[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

const detailDrawerRef = ref()
const memberLevelModalRef = ref()

async function getList() {
  startLoading()
  const [startDate, endDate] = searchModel.value.dateRange ?? [null, null]
  await getAccountPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    keyword: searchModel.value.keyword || undefined,
    memberLevel: searchModel.value.memberLevel,
    startDate: startDate ? new Date(startDate).toLocaleDateString() : undefined,
    endDate: endDate ? new Date(endDate).toLocaleDateString() : undefined,
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
  searchModel.value = { keyword: '', memberLevel: undefined, dateRange: null }
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
          <n-form-item label="关键词" path="keyword">
            <n-input v-model:value="searchModel.keyword" placeholder="手机号/昵称" clearable style="width: 160px" />
          </n-form-item>
          <n-form-item label="会员等级" path="memberLevel">
            <n-select
              v-model:value="searchModel.memberLevel"
              :options="memberLevelOptions"
              placeholder="全部"
              clearable
              style="width: 120px"
            />
          </n-form-item>
          <n-form-item label="注册时间" path="dateRange">
            <n-date-picker
              v-model:value="searchModel.dateRange"
              type="daterange"
              clearable
              style="width: 240px"
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
      <template #header>
        小程序用户
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" :row-key="(row: Entity.AccountVO) => row.id" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>

    <DetailDrawer ref="detailDrawerRef" />
    <MemberLevelModal ref="memberLevelModalRef" @saved="getList" />
  </NSpace>
</template>
