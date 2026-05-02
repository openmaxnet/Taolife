<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getAgreementPage, removeAgreement } from '@/service'
import { NButton, NPopconfirm, NSpace } from 'naive-ui'
import TableModal from './components/TableModal.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const searchModel = ref({ keyword: '' })

const tableModalRef = ref()

const columns: DataTableColumns<Entity.SysAgreement> = [
  { title: '标识', key: 'code', width: 140 },
  { title: '标题', key: 'title', width: 180, ellipsis: { tooltip: true } },
  { title: '版本', key: 'version', width: 80, align: 'center' },
  { title: '更新时间', key: 'updateTime', width: 170 },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    align: 'center',
    fixed: 'right',
    render: row => (
      <NSpace justify="center" size="small">
        <NButton size="small" onClick={() => tableModalRef.value?.openModal('view', row)}>
          查看
        </NButton>
        <NButton size="small" type="primary" onClick={() => tableModalRef.value?.openModal('edit', row)}>
          编辑
        </NButton>
        <NPopconfirm onPositiveClick={() => handleDelete(row.id!)}>
          {{
            trigger: () => <NButton size="small" type="error">删除</NButton>,
            default: () => '确定删除该协议？',
          }}
        </NPopconfirm>
      </NSpace>
    ),
  },
]

const listData = ref<Entity.SysAgreement[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getAgreementPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    keyword: searchModel.value.keyword || undefined,
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
  searchModel.value = { keyword: '' }
  currentPage.value = 1
  getList()
}

async function handleDelete(id: string) {
  await removeAgreement(id)
  window.$message.success('删除成功')
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
            <n-input v-model:value="searchModel.keyword" placeholder="标识/标题" clearable style="width: 200px" />
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
        系统协议管理
      </template>
      <template #header-extra>
        <NButton type="primary" @click="tableModalRef?.openModal('add')">
          <template #icon>
            <icon-park-outline-add />
          </template>
          新增协议
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" :row-key="(row: any) => row.id" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>

    <TableModal ref="tableModalRef" modal-name="协议" @saved="getList" />
  </NSpace>
</template>
