<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getUserPlanPage, modifyUserPlanStatus } from '@/service'
import { NButton, NDropdown, NPopconfirm, NSpace, NTag } from 'naive-ui'
import TaskDrawer from './components/TaskDrawer.vue'
import AdjustmentDrawer from './components/AdjustmentDrawer.vue'
import ShareDrawer from './components/ShareDrawer.vue'
import CycleReportDrawer from './components/CycleReportDrawer.vue'
import DetailDrawer from './components/DetailDrawer.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const statusOptions = [
  { label: '全部', value: 0 },
  { label: '进行中', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已过期', value: 3 },
  { label: '已终止', value: 4 },
]

const searchModel = ref({
  keyword: '',
  status: undefined as number | undefined,
})

const statusMap: Record<number, { label: string, type: 'success' | 'info' | 'warning' | 'error' }> = {
  1: { label: '进行中', type: 'success' },
  2: { label: '已完成', type: 'info' },
  3: { label: '已过期', type: 'warning' },
  4: { label: '已终止', type: 'error' },
}

async function handlePause(id: string) {
  await modifyUserPlanStatus(id, 3)
  window.$message.success('已暂停')
  getList()
}

// 抽屉引用
const currentPlanId = ref('')
const taskDrawerRef = ref()
const adjustmentDrawerRef = ref()
const shareDrawerRef = ref()
const cycleReportDrawerRef = ref()
const detailDrawerRef = ref()

function openDrawer(drawerRef: any, planId: string) {
  currentPlanId.value = planId
  nextTick(() => drawerRef.value?.open())
}

function getDropdownOptions(_row: Entity.UserPlan) {
  const options: Array<{ label: string, key: string }> = [
    { label: '方案任务', key: 'task' },
    { label: '调整记录', key: 'adjustment' },
    { label: '分享记录', key: 'share' },
    { label: '周期报告', key: 'report' },
  ]
  return options
}

function handleDropdownSelect(key: string, row: Entity.UserPlan) {
  const id = row.id ?? ''
  switch (key) {
    case 'task':
      openDrawer(taskDrawerRef, id)
      break
    case 'adjustment':
      openDrawer(adjustmentDrawerRef, id)
      break
    case 'share':
      openDrawer(shareDrawerRef, id)
      break
    case 'report':
      openDrawer(cycleReportDrawerRef, id)
      break
  }
}

const columns: DataTableColumns<Entity.UserPlan> = [
  {
    title: '用户ID',
    key: 'accountId',
    width: 240,
    ellipsis: { tooltip: true },
  },
  {
    title: '体质',
    key: 'constitutionName',
    width: 80,
  },
  {
    title: '季节',
    key: 'seasonName',
    width: 80,
  },
  {
    title: '方案标题',
    key: 'planTitle',
    ellipsis: { tooltip: true },
  },
  {
    title: '完成率',
    key: 'completionRate',
    width: 80,
    align: 'center',
    render: row => `${((row.completionRate ?? 0) * 100).toFixed(0)}%`,
  },
  {
    title: '任务',
    key: 'tasks',
    width: 80,
    align: 'center',
    render: row => `${row.completedTasks ?? 0}/${row.totalTasks ?? 0}`,
  },
  {
    title: '评分',
    key: 'userRating',
    width: 60,
    align: 'center',
  },
  {
    title: '状态',
    key: 'status',
    width: 80,
    align: 'center',
    render: (row) => {
      const s = statusMap[row.status ?? 0]
      return s ? <NTag size="small" type={s.type}>{s.label}</NTag> : '-'
    },
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
  },
  {
    title: '操作',
    key: 'actions',
    width: 300,
    align: 'center',
    render: (row) => {
      return (
        <NSpace justify="center" size="small">
          <NButton size="small" onClick={() => detailDrawerRef.value?.open(row.id)}>
            详情
          </NButton>
          <NDropdown
            options={getDropdownOptions(row)}
            onSelect={(key: string) => handleDropdownSelect(key, row)}
          >
            <NButton size="small" secondary>
              更多 ▾
            </NButton>
          </NDropdown>
          {row.status === 1 && (
            <NPopconfirm onPositiveClick={() => handlePause(row.id!)}>
              {{
                default: () => '确认暂停该方案？',
                trigger: () => <NButton size="small" type="warning">暂停</NButton>,
              }}
            </NPopconfirm>
          )}
        </NSpace>
      )
    },
  },
]

const listData = ref<Entity.UserPlan[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getUserPlanPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    keyword: searchModel.value.keyword || undefined,
    status: searchModel.value.status || undefined,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

function handleResetSearch() {
  searchModel.value = { keyword: '', status: undefined }
  currentPage.value = 1
  getList()
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
          <n-form-item label="状态" path="status">
            <n-select
              v-model:value="searchModel.status"
              :options="statusOptions"
              clearable
              placeholder="全部状态"
              style="width: 120px"
            />
          </n-form-item>
          <n-form-item label="关键词" path="keyword">
            <n-input v-model:value="searchModel.keyword" placeholder="方案标题" />
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

    <!-- 抽屉 -->
    <DetailDrawer ref="detailDrawerRef" />
    <TaskDrawer ref="taskDrawerRef" :user-plan-id="currentPlanId" />
    <AdjustmentDrawer ref="adjustmentDrawerRef" :user-plan-id="currentPlanId" />
    <ShareDrawer ref="shareDrawerRef" :user-plan-id="currentPlanId" />
    <CycleReportDrawer ref="cycleReportDrawerRef" :user-plan-id="currentPlanId" />
  </NSpace>
</template>
