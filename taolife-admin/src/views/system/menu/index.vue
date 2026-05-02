<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getPermissionTree, removePermission, removePermissionBatch } from '@/service'
import { arrayToTree, createIcon } from '@/utils'
import { NButton, NPopconfirm, NSpace, NTag } from 'naive-ui'
import { renderProCopyableText } from 'pro-naive-ui'
import TableModal from './components/TableModal.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

async function deleteData(id: string) {
  await removePermission(id)
  window.$message.success('删除成功')
  getAllRoutes()
}

const tableModalRef = ref()

const columns: DataTableColumns<Entity.Menu> = [
  {
    type: 'selection',
    width: 30,
  },
  {
    title: '权限名称',
    key: 'permissionName',
    ellipsis: {
      tooltip: true,
    },
  },
  {
    title: '图标',
    align: 'center',
    key: 'icon',
    render: (row) => {
      return row.icon && createIcon(row.icon, { size: 20 })
    },
  },
  {
    title: '权限编码',
    key: 'permissionCode',
    width: 200,
  },
  {
    title: '路径',
    key: 'path',
    render: row => renderProCopyableText(row.path),
  },
  {
    title: '组件路径',
    key: 'component',
    ellipsis: {
      tooltip: true,
    },
    render: (row) => {
      return row.component || '-'
    },
  },
  {
    title: '排序值',
    key: 'sortOrder',
    align: 'center',
    width: '6em',
  },
  {
    title: '权限类型',
    align: 'center',
    key: 'permissionType',
    width: '6em',
    render: (row) => {
      const typeMap: Record<number, { label: string, color: NaiveUI.ThemeColor }> = {
        1: { label: '菜单', color: 'warning' },
        2: { label: '按钮', color: 'primary' },
      }
      const item = typeMap[row.permissionType ?? 1] || { label: '未知', color: 'default' as const }
      return <NTag type={item.color}>{item.label}</NTag>
    },
  },
  {
    title: '操作',
    align: 'center',
    key: 'actions',
    width: 300,
    render: (row) => {
      return (
        <NSpace justify="center">
          <NButton
            size="small"
            onClick={() => tableModalRef.value.openModal('view', row)}
          >
            查看
          </NButton>
          <NButton
            size="small"
            onClick={() => tableModalRef.value.openModal('edit', row)}
          >
            编辑
          </NButton>
          <NPopconfirm onPositiveClick={() => deleteData(row.id ?? '')}>
            {{
              default: () => '确认删除',
              trigger: () => <NButton size="small" type="error">删除</NButton>,
            }}
          </NPopconfirm>
        </NSpace>
      )
    },
  },
]

const tableData = ref<Entity.Menu[]>([])

onMounted(() => {
  getAllRoutes()
})
async function getAllRoutes() {
  startLoading()
  const { data } = await getPermissionTree(1)
  tableData.value = arrayToTree(data)
  endLoading()
}

const checkedRowKeys = ref<string[]>([])
async function handlePositiveClick() {
  await removePermissionBatch(checkedRowKeys.value)
  window.$message.success('批量删除成功')
  checkedRowKeys.value = []
  getAllRoutes()
}
</script>

<template>
  <n-card>
    <template #header>
      <NButton type="primary" @click="tableModalRef.openModal('add')">
        <template #icon>
          <icon-park-outline-add-one />
        </template>
        新建
      </NButton>
    </template>

    <template #header-extra>
      <n-flex>
        <NButton type="primary" secondary @click="getAllRoutes">
          <template #icon>
            <icon-park-outline-refresh />
          </template>
          刷新
        </NButton>
        <NPopconfirm @positive-click="handlePositiveClick">
          <template #trigger>
            <NButton type="error" secondary :disabled="checkedRowKeys.length === 0">
              <template #icon>
                <icon-park-outline-delete-five />
              </template>
              批量删除
            </NButton>
          </template>
          确认删除所有选中菜单？
        </NPopconfirm>
      </n-flex>
    </template>
    <n-data-table
      v-model:checked-row-keys="checkedRowKeys"
      :row-key="(row:Entity.Menu) => row.id ?? ''"
      :columns="columns"
      :data="tableData"
      :loading="loading"
      size="small"
      :scroll-x="1200"
    />
    <TableModal ref="tableModalRef" :all-routes="tableData" modal-name="菜单" @saved="getAllRoutes" />
  </n-card>
</template>
