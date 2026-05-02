<script setup lang="tsx">
import type { DataTableColumns, FormInst } from 'naive-ui'
import CopyText from '@/components/custom/CopyText.vue'
import { useBoolean } from '@/hooks'
import { getSysUserPage, modifySysUserStatus, removeSysUser } from '@/service'
import { NButton, NPopconfirm, NSpace, NSwitch, NTag } from 'naive-ui'
import TableModal from './components/TableModal.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = {
  realName: '',
}
const model = ref({ ...initialModel })
function handleResetSearch() {
  model.value = { ...initialModel }
}

const formRef = ref<FormInst | null>()
const modalRef = ref()

async function handleDeleteUser(id: string) {
  await removeSysUser(id)
  window.$message.success('删除成功')
  getUserList()
}

const columns: DataTableColumns<Entity.User> = [
  {
    title: '用户名',
    align: 'center',
    key: 'username',
  },
  {
    title: '真实姓名',
    align: 'center',
    key: 'realName',
  },
  {
    title: '邮箱',
    align: 'center',
    key: 'email',
  },
  {
    title: '手机号',
    align: 'center',
    key: 'phone',
    render: (row) => {
      return (
        <CopyText value={row.phone} />
      )
    },
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
            handleUpdateDisabled(value, row.id!)}
        >
          {{ checked: () => '启用', unchecked: () => '禁用' }}
        </NSwitch>
      )
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
            onClick={() => modalRef.value.openModal('edit', row)}
          >
            编辑
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDeleteUser(row.id ?? '')}>
            {{
              default: () => '确认删除该用户？',
              trigger: () => <NButton size="small" type="error">删除</NButton>,
            }}
          </NPopconfirm>
        </NSpace>
      )
    },
  },
]

const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)
const listData = ref<Entity.User[]>([])

async function handleUpdateDisabled(value: 0 | 1, id: string) {
  await modifySysUserStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isDisabled = value
  window.$message.success(value === 0 ? '已启用' : '已禁用')
}

async function getUserList() {
  startLoading()
  await getSysUserPage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    realName: model.value.realName || undefined,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

onMounted(() => {
  getUserList()
})

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getUserList()
}
</script>

<template>
  <NSpace vertical>
    <n-card>
      <n-form ref="formRef" :model="model" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="真实姓名" path="realName">
            <n-input v-model:value="model.realName" placeholder="请输入" />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" @click="getUserList">
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

    <n-card class="flex-1">
      <template #header>
        <NButton type="primary" @click="modalRef.openModal('add')">
          <template #icon>
            <icon-park-outline-add-one />
          </template>
          新建用户
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>

      <TableModal ref="modalRef" modal-name="用户" @saved="getUserList" />
    </n-card>
  </NSpace>
</template>
