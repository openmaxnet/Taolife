<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getShareRecordPage } from '@/service'

const props = defineProps<{
  userPlanId: string
}>()

const { bool: visible, setTrue: show, setFalse: hide } = useBoolean(false)
const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const shareTypeMap: Record<number, string> = { 1: '朋友圈', 2: '好友', 3: '方案广场' }

const columns: DataTableColumns<Entity.PlanShareRecord> = [
  { title: '分享类型', key: 'shareType', width: 90, render: row => shareTypeMap[row.shareType ?? 0] ?? '-' },
  { title: '分享文字', key: 'shareText', ellipsis: { tooltip: true } },
  { title: '浏览', key: 'viewCount', width: 60, align: 'center' },
  { title: '点赞', key: 'likeCount', width: 60, align: 'center' },
  { title: '收藏', key: 'collectCount', width: 60, align: 'center' },
  { title: '积分', key: 'pointsAwarded', width: 60, align: 'center' },
  { title: '创建时间', key: 'createTime', width: 180 },
]

const listData = ref<Entity.PlanShareRecord[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getShareRecordPage({
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

function open() {
  currentPage.value = 1
  show()
  getList()
}

defineExpose({ open })
</script>

<template>
  <n-drawer v-model:show="visible" :width="700">
    <n-drawer-content title="分享记录">
      <n-data-table :columns="columns" :data="listData" :loading="loading" />
      <Pagination :count="count" @change="changePage" class="mt-12px" />
    </n-drawer-content>
  </n-drawer>
</template>
