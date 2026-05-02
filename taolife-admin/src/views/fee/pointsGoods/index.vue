<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getGoodsPage, modifyGoodsStatus, removeGoods } from '@/service'
import { NButton, NSpace, NTag } from 'naive-ui'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const goodsTypeMap: Record<number, string> = {
  1: 'AI配额',
  2: '体质评估',
  3: '文章解锁',
  4: '优惠券',
}

const searchModel = ref({
  goodsCode: '',
  goodsType: undefined as number | undefined,
})

const goodsTypeOptions = [
  { label: '全部类型', value: undefined },
  { label: 'AI配额', value: 1 },
  { label: '体质评估', value: 2 },
  { label: '文章解锁', value: 3 },
  { label: '优惠券', value: 4 },
]

async function handleModifyStatus(id: string, isEnabled: number) {
  await modifyGoodsStatus(id, isEnabled)
  window.$message.success(isEnabled === 1 ? '启用成功' : '禁用成功')
  getList()
}

async function handleRemove(id: string) {
  window.$dialog.warning({
    title: '确认删除',
    content: '确定要删除这个积分商品吗？',
    positiveText: '确认',
    negativeText: '取消',
    async onPositiveClick() {
      await removeGoods(id)
      window.$message.success('删除成功')
      getList()
    },
  })
}

const columns: DataTableColumns<Entity.PointsGoodsAdminVO> = [
  { title: '商品编码', key: 'goodsCode', width: 120, ellipsis: { tooltip: true } },
  { title: '商品名称', key: 'goodsName', width: 150, ellipsis: { tooltip: true } },
  {
    title: '类型',
    key: 'goodsType',
    width: 100,
    render: row => goodsTypeMap[row.goodsType as number] ?? '-',
  },
  { title: '兑换值', key: 'value', width: 80, align: 'center' },
  { title: '所需积分', key: 'pointsRequired', width: 90, align: 'center' },
  { title: '每日限制', key: 'dailyLimit', width: 80, align: 'center' },
  { title: '总限制', key: 'totalLimit', width: 80, align: 'center' },
  {
    title: '状态',
    key: 'isEnabled',
    width: 70,
    render: row => <NTag type={row.isEnabled === 1 ? 'success' : 'error'} size="small">{row.isEnabled === 1 ? '启用' : '禁用'}</NTag>,
  },
  { title: '排序', key: 'sortOrder', width: 60, align: 'center' },
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

const listData = ref<Entity.PointsGoodsAdminVO[]>([])
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)

async function getList() {
  startLoading()
  await getGoodsPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    goodsCode: searchModel.value.goodsCode || undefined,
    goodsType: searchModel.value.goodsType,
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
  searchModel.value = { goodsCode: '', goodsType: undefined }
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
          <n-form-item label="商品编码" path="goodsCode">
            <n-input v-model:value="searchModel.goodsCode" placeholder="请输入商品编码" clearable style="width: 160px" />
          </n-form-item>
          <n-form-item label="商品类型" path="goodsType">
            <n-select
              v-model:value="searchModel.goodsType"
              :options="goodsTypeOptions"
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
        <n-data-table :columns="columns" :data="listData" :loading="loading" :row-key="(row: Entity.PointsGoodsAdminVO) => row.id ?? ''" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
