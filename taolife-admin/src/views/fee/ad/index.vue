<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getAdConfigPage, modifyAdConfigStatus, removeAdConfig } from '@/service'
import { NButton, NSpace, NSelect, NTag, NInput } from 'naive-ui'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const adTypeMap: Record<number, string> = {
  1: 'Banner广告',
  2: '插屏广告',
  3: '激励视频',
  4: '开屏广告',
}

const searchModel = ref({
  configKey: '',
  adType: undefined as number | undefined,
})

const adTypeOptions = [
  { label: '全部类型', value: undefined },
  { label: 'Banner广告', value: 1 },
  { label: '插屏广告', value: 2 },
  { label: '激励视频', value: 3 },
  { label: '开屏广告', value: 4 },
]

async function handleModifyStatus(id: string, isEnabled: number) {
  await modifyAdConfigStatus(id, isEnabled)
  window.$message.success(isEnabled === 1 ? '启用成功' : '禁用成功')
  getList()
}

async function handleRemove(id: string) {
  window.$dialog.warning({
    title: '确认删除',
    content: '确定要删除这条广告配置吗？',
    positiveText: '确认',
    negativeText: '取消',
    async onPositiveClick() {
      await removeAdConfig(id)
      window.$message.success('删除成功')
      getList()
    },
  })
}

const columns: DataTableColumns<Entity.AdConfigAdminVO> = [
  { title: '配置Key', key: 'configKey', width: 160, ellipsis: { tooltip: true } },
  {
    title: '广告类型',
    key: 'adType',
    width: 100,
    render: row => adTypeMap[row.adType as number] ?? '-',
  },
  { title: '广告单元ID', key: 'adUnitId', width: 200, ellipsis: { tooltip: true } },
  { title: '展示位置', key: 'placement', width: 120, ellipsis: { tooltip: true } },
  {
    title: '仅免费用户',
    key: 'freeUserOnly',
    width: 90,
    align: 'center',
    render: row => <NTag type={row.freeUserOnly === 1 ? 'info' : 'default'} size="small">{row.freeUserOnly === 1 ? '是' : '否'}</NTag>,
  },
  {
    title: '状态',
    key: 'isEnabled',
    width: 70,
    render: row => <NTag type={row.isEnabled === 1 ? 'success' : 'error'} size="small">{row.isEnabled === 1 ? '启用' : '禁用'}</NTag>,
  },
  { title: '优先级', key: 'priority', width: 70, align: 'center' },
  { title: '创建时间', key: 'createTime', width: 170 },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    align: 'center',
    fixed: 'right',
    render: (row) => {
      return (
        <NSpace justify="center" size="small">
          {row.isEnabled === 1
            ? <NButton size="small" type="warning" onClick={() => handleModifyStatus(row.id ?? '', 0)}>禁用</NButton>
            : <NButton size="small" type="info" onClick={() => handleModifyStatus(row.id ?? '', 1)}>启用</NButton>
          }
          <NButton size="small" type="error" onClick={() => handleRemove(row.id ?? '')}>删除</NButton>
        </NSpace>
      )
    },
  },
]

const listData = ref<Entity.AdConfigAdminVO[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getAdConfigPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    configKey: searchModel.value.configKey || undefined,
    adType: searchModel.value.adType,
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
  searchModel.value = { configKey: '', adType: undefined }
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
          <n-form-item label="配置Key" path="configKey">
            <n-input v-model:value="searchModel.configKey" placeholder="请输入配置Key" clearable style="width: 160px" />
          </n-form-item>
          <n-form-item label="广告类型" path="adType">
            <n-select
              v-model:value="searchModel.adType"
              :options="adTypeOptions"
              placeholder="全部类型"
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
        <n-data-table :columns="columns" :data="listData" :loading="loading" :row-key="(row: Entity.AdConfigAdminVO) => row.id ?? ''" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
