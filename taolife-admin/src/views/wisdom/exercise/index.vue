<script setup lang="tsx">
import type { DataTableColumns, FormInst } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getExercisePage, modifyExerciseStatus, removeExercise } from '@/service'
import { NButton, NPopconfirm, NSpace, NSwitch } from 'naive-ui'
import TableModal from './components/TableModal.vue'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const initialModel = {
  keyword: '',
  category: undefined as number | undefined,
}
const model = ref({ ...initialModel })
function handleResetSearch() {
  model.value = { ...initialModel }
}

const categoryOptions = [
  { label: '传统功法', value: 1 },
  { label: '有氧运动', value: 2 },
  { label: '力量训练', value: 3 },
  { label: '柔韧训练', value: 4 },
  { label: '休闲运动', value: 5 },
]

const formRef = ref<FormInst | null>()
const modalRef = ref()

async function handleDelete(id: string) {
  await removeExercise(id)
  window.$message.success('删除成功')
  getList()
}

function getCategoryName(category?: number) {
  return categoryOptions.find(o => o.value === category)?.label ?? ''
}

const columns: DataTableColumns<Entity.Exercise> = [
  {
    title: '名称',
    align: 'center',
    key: 'name',
  },
  {
    title: '分类',
    align: 'center',
    key: 'category',
    render: row => getCategoryName(row.category),
  },
  {
    title: '强度',
    align: 'center',
    key: 'intensity',
    render: (row) => {
      const map: Record<number, string> = { 1: '温和', 2: '轻度', 3: '中度', 4: '重度' }
      return map[row.intensity ?? 0] ?? ''
    },
  },
  {
    title: '时长(分钟)',
    align: 'center',
    key: 'durationMin',
  },
  {
    title: '难度',
    align: 'center',
    key: 'difficultyLevel',
    render: (row) => {
      const map: Record<number, string> = { 1: '简单', 2: '中等', 3: '困难' }
      return map[row.difficultyLevel ?? 0] ?? ''
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
          <NPopconfirm onPositiveClick={() => handleDelete(row.id ?? '')}>
            {{
              default: () => '确认删除该运动项目？',
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
const listData = ref<Entity.Exercise[]>([])

async function handleUpdateDisabled(value: 0 | 1, id: string) {
  await modifyExerciseStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1)
    listData.value[index].isDisabled = value
  window.$message.success(value === 0 ? '已启用' : '已禁用')
}

async function getList() {
  startLoading()
  await getExercisePage({
    pageNo: currentPage.value,
    pageSize: currentPageSize.value,
    keyword: model.value.keyword || undefined,
    category: model.value.category || undefined,
  }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

onMounted(() => {
  getList()
})

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getList()
}
</script>

<template>
  <NSpace vertical>
    <n-card>
      <n-form ref="formRef" :model="model" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="分类" path="category">
            <n-select v-model:value="model.category" :options="categoryOptions" clearable placeholder="全部分类" style="width: 140px" />
          </n-form-item>
          <n-form-item label="名称搜索" path="keyword">
            <n-input v-model:value="model.keyword" placeholder="请输入" />
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

    <n-card class="flex-1">
      <template #header>
        <NButton type="primary" @click="modalRef.openModal('add')">
          <template #icon>
            <icon-park-outline-add-one />
          </template>
          新建运动项目
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>

      <TableModal ref="modalRef" modal-name="运动项目" @saved="getList" />
    </n-card>
  </NSpace>
</template>
