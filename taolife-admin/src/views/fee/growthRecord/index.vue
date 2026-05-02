<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getGrowthRecordPage, adjustGrowth } from '@/service'
import { NButton, NInput, NSpace } from 'naive-ui'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const growthSourceMap: Record<number, string> = {
  1: '每日登录',
  2: '签到',
  3: '完成任务',
  4: '开通续费',
  5: '手动调整',
}

const searchAccountId = ref('')
const listData = ref<Entity.MemberGrowthRecordAdminVO[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(20)

// 调整弹窗
const { bool: adjustVisible, setTrue: showAdjust, setFalse: hideAdjust } = useBoolean(false)
const adjustForm = ref({ accountId: '', growth: 0, remark: '' })

function openAdjust() {
  adjustForm.value = { accountId: '', growth: 0, remark: '' }
  showAdjust()
}

async function submitAdjust() {
  if (!adjustForm.value.accountId || adjustForm.value.growth <= 0) {
    window.$message.warning('请填写账号ID和正整数成长值')
    return
  }
  await adjustGrowth(adjustForm.value.accountId, adjustForm.value.growth, adjustForm.value.remark || undefined)
  window.$message.success('调整成功')
  hideAdjust()
  getList()
}

const columns: DataTableColumns<Entity.MemberGrowthRecordAdminVO> = [
  { title: '账号ID', key: 'accountId', width: 240, ellipsis: { tooltip: true } },
  { title: '成长值', key: 'growthChange', width: 80, align: 'center' },
  {
    title: '来源',
    key: 'growthSource',
    width: 100,
    render: row => growthSourceMap[row.growthSource ?? 0] ?? '-',
  },
  { title: '业务类型', key: 'businessType', width: 120 },
  { title: '备注', key: 'remark', ellipsis: { tooltip: true } },
  { title: '变化后成长值', key: 'growthValueAfter', width: 120, align: 'center' },
  { title: '时间', key: 'createTime', width: 180 },
]

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getList()
}

function handleSearch() {
  currentPage.value = 1
  getList()
}

function handleReset() {
  searchAccountId.value = ''
  currentPage.value = 1
  getList()
}

async function getList() {
  startLoading()
  await getGrowthRecordPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    accountId: searchAccountId.value || undefined,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

onMounted(() => {
  getList()
})
</script>

<template>
  <NSpace vertical>
    <n-card>
      <n-flex justify="space-between" align="center">
        <n-flex>
          <n-input v-model:value="searchAccountId" placeholder="账号ID" clearable style="width: 240px" @keydown.enter="handleSearch" />
          <NButton type="primary" @click="handleSearch">查询</NButton>
          <NButton @click="handleReset">重置</NButton>
        </n-flex>
        <NButton type="primary" @click="openAdjust">
          <template #icon><icon-park-outline-add-one /></template>
          调整成长值
        </NButton>
      </n-flex>
    </n-card>

    <n-card>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>

    <n-modal v-model:show="adjustVisible" preset="card" title="调整成长值" class="w-500px" :segmented="{ content: true, action: true }">
      <n-form label-placement="left" :label-width="80">
        <n-form-item label="账号ID">
          <n-input v-model:value="adjustForm.accountId" placeholder="请输入账号ID" />
        </n-form-item>
        <n-form-item label="成长值">
          <n-input-number v-model:value="adjustForm.growth" :min="1" placeholder="正整数" class="w-full" />
        </n-form-item>
        <n-form-item label="备注">
          <n-input v-model:value="adjustForm.remark" type="textarea" placeholder="可选" />
        </n-form-item>
      </n-form>
      <template #action>
        <NSpace justify="center">
          <NButton @click="hideAdjust">取消</NButton>
          <NButton type="primary" @click="submitAdjust">提交</NButton>
        </NSpace>
      </template>
    </n-modal>
  </NSpace>
</template>
