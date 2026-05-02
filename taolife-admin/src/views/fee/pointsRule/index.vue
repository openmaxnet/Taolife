<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getPointsRulePage, removePointsRule, modifyPointsRuleStatus } from '@/service'
import { NButton, NPopconfirm, NSpace, NSwitch } from 'naive-ui'
import TableModal from './components/TableModal.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const pointsTypeMap: Record<number, string> = {
  1: '签到',
  2: '健康计划',
  3: '消费',
  4: '广告奖励',
}

const conditionTypeMap: Record<number, string> = {
  0: '无条件',
  1: '连续天数',
  2: '累计天数',
}

const modalRef = ref()
const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(20)
const listData = ref<Entity.PointsRule[]>([])

async function handleDelete(id: string) {
  await removePointsRule(id)
  window.$message.success('删除成功')
  getList()
}

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifyPointsRuleStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isEnabled = value
  window.$message.success(value === 1 ? '已启用' : '已禁用')
}

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getList()
}

const columns: DataTableColumns<Entity.PointsRule> = [
  { title: '规则名称', key: 'ruleName', width: 140 },
  { title: '规则编码', key: 'ruleCode', width: 160 },
  {
    title: '积分类型',
    key: 'pointsType',
    width: 100,
    render: row => pointsTypeMap[row.pointsType ?? 0] ?? '-',
  },
  { title: '积分', key: 'points', width: 80, align: 'center' },
  {
    title: '条件类型',
    key: 'conditionType',
    width: 100,
    render: row => conditionTypeMap[row.conditionType ?? 0] ?? '-',
  },
  {
    title: '条件值',
    key: 'conditionValue',
    width: 80,
    align: 'center',
    render: (row) => {
      if (row.conditionType === 0)
        return '-'
      return `${row.conditionValue ?? 0}`
    },
  },
  {
    title: '每日上限',
    key: 'dailyLimit',
    width: 80,
    align: 'center',
    render: row => row.dailyLimit === 0 ? '不限' : `${row.dailyLimit}`,
  },
  {
    title: '状态',
    key: 'isEnabled',
    width: 100,
    align: 'center',
    render: (row) => {
      return (
        <NSwitch
          value={row.isEnabled === 1}
          checked-value={1}
          unchecked-value={0}
          onUpdateValue={(value: 0 | 1) =>
            handleUpdateStatus(value, row.id!)}
        >
          {{ checked: () => '启用', unchecked: () => '禁用' }}
        </NSwitch>
      )
    },
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    align: 'center',
    render: (row) => {
      return (
        <NSpace justify="center">
          <NButton size="small" onClick={() => modalRef.value.openModal('edit', row)}>
            编辑
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDelete(row.id ?? '')}>
            {{
              default: () => '确认删除该规则？',
              trigger: () => <NButton size="small" type="error">删除</NButton>,
            }}
          </NPopconfirm>
        </NSpace>
      )
    },
  },
]

async function getList() {
  startLoading()
  await getPointsRulePage({ pageNo: currentPage.value, pageSize: currentPageSize.value }).then((res) => {
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
  <n-card>
    <template #header>
      <NButton type="primary" @click="modalRef.openModal('add')">
        <template #icon>
          <icon-park-outline-add-one />
        </template>
        新建规则
      </NButton>
    </template>
    <NSpace vertical>
      <n-data-table :columns="columns" :data="listData" :loading="loading" />
      <Pagination :count="count" @change="changePage" />
    </NSpace>

    <TableModal ref="modalRef" modal-name="积分规则" @saved="getList" />
  </n-card>
</template>
