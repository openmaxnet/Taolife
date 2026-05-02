<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getSysRoleList, modifySysRoleStatus, removeSysRole } from '@/service'
import { NButton, NPopconfirm, NSpace, NSwitch } from 'naive-ui'
import PermissionModal from './components/PermissionModal.vue'
import TableModal from './components/TableModal.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const modalRef = ref()
const permissionModalRef = ref()

async function handleDeleteRole(id: string) {
  await removeSysRole(id)
  window.$message.success('删除成功')
  getRoleList()
}

const columns: DataTableColumns<Entity.Role> = [
  {
    title: '角色名称',
    align: 'center',
    key: 'roleName',
  },
  {
    title: '角色编码',
    align: 'center',
    key: 'roleCode',
  },
  {
    title: '描述',
    align: 'center',
    key: 'description',
    render: (row) => row.description || '-',
  },
  {
    title: '状态',
    align: 'center',
    key: 'isDisabled',
    render: (row) => {
      return (
        <NSwitch
          value={row.isDisabled === 0}
          checked-value={0}
          unchecked-value={1}
          onUpdateValue={(value: 0 | 1) =>
            handleUpdateDisabled(value, row.id ?? '')}
        >
          {{ checked: () => '启用', unchecked: () => '禁用' }}
        </NSwitch>
      )
    },
  },
  {
    title: '创建时间',
    align: 'center',
    key: 'createTime',
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
            onClick={() => permissionModalRef.value.openModal(row)}
          >
            权限管理
          </NButton>
          <NButton
            size="small"
            onClick={() => modalRef.value.openModal('edit', row)}
          >
            编辑
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDeleteRole(row.id ?? '')}>
            {{
              default: () => '确认删除该角色？',
              trigger: () => <NButton size="small" type="error">删除</NButton>,
            }}
          </NPopconfirm>
        </NSpace>
      )
    },
  },
]

const listData = ref<Entity.Role[]>([])

async function handleUpdateDisabled(value: 0 | 1, id: string) {
  await modifySysRoleStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isDisabled = value
  window.$message.success(value === 0 ? '已启用' : '已禁用')
}

async function getRoleList() {
  startLoading()
  const { data } = await getSysRoleList()
  listData.value = data || []
  endLoading()
}

onMounted(() => {
  getRoleList()
})
</script>

<template>
  <NSpace vertical>
    <n-card class="flex-1">
      <template #header>
        <NButton type="primary" @click="modalRef.openModal('add')">
          <template #icon>
            <icon-park-outline-add-one />
          </template>
          新建角色
        </NButton>
      </template>
      <n-data-table :columns="columns" :data="listData" :loading="loading" />

      <TableModal ref="modalRef" modal-name="角色" @saved="getRoleList" />
      <PermissionModal ref="permissionModalRef" />
    </n-card>
  </NSpace>
</template>
