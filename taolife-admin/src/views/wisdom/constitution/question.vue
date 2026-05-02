<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { getQuestionPage, modifyQuestionStatus, removeQuestion } from '@/service/api/wisdom'
import { NButton, NPopconfirm, NSpace, NSwitch } from 'naive-ui'
import QuestionModal from './components/QuestionModal.vue'

const questionModalRef = ref()
const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const questionMode = ref<number | null>(null)

const columns: DataTableColumns<Entity.QuestionVO> = [
  { title: '题号', align: 'center', key: 'questionNo', width: 70 },
  { title: '题目内容', align: 'center', key: 'questionText', ellipsis: { tooltip: true } },
  { title: '分类', align: 'center', key: 'category', width: 100 },
  { title: '模式', align: 'center', key: 'questionMode', width: 80, render: (row) => row.questionMode === 1 ? '简易' : '精细' },
  { title: '题型', align: 'center', key: 'answerType', width: 80, render: (row) => row.answerType === 1 ? '单选' : '多选' },
  { title: '选项数', align: 'center', key: 'optionCount', width: 80 },
  {
    title: '状态', align: 'center', key: 'isDisabled', width: 100,
    render: (row) => (
      <NSwitch
        value={row.isDisabled}
        checked-value={0}
        unchecked-value={1}
        onUpdate:value={(value: 0 | 1) => handleUpdateStatus(value, row.id)}
      >
        {{ checked: () => '启用', unchecked: () => '禁用' }}
      </NSwitch>
    ),
  },
  {
    title: '操作', align: 'center', key: 'actions', width: 150,
    render: (row) => (
      <NSpace justify="center">
        <NButton size="small" onClick={() => questionModalRef.value.openModal('edit', row)}>编辑</NButton>
        <NPopconfirm onPositiveClick={() => handleDelete(row.id)}>
          {{
            default: () => '确认删除？',
            trigger: () => <NButton size="small" type="error">删除</NButton>,
          }}
        </NPopconfirm>
      </NSpace>
    ),
  },
]

const count = ref(0)
const currentPage = ref(1)
const currentPageSize = ref(10)
const listData = ref<Entity.QuestionVO[]>([])

async function handleUpdateStatus(value: 0 | 1, id: string) {
  await modifyQuestionStatus(id, value)
  const index = listData.value.findIndex(item => item.id === id)
  if (index > -1) listData.value[index].isDisabled = value
  window.$message.success(value === 0 ? '已启用' : '已禁用')
}

async function handleDelete(id: string) {
  await removeQuestion(id)
  window.$message.success('删除成功')
  getList()
}

async function getList() {
  startLoading()
  await getQuestionPage({ pageNo: currentPage.value, pageSize: currentPageSize.value, questionMode: questionMode.value || undefined }).then((res) => {
    listData.value = res.data.list
    count.value = res.data.totalRow
    endLoading()
  })
}

onMounted(() => { getList() })

function changePage(page: number, size: number) {
  currentPage.value = page
  currentPageSize.value = size
  getList()
}
</script>

<template>
  <NSpace vertical>
    <n-card>
      <n-form label-placement="left" inline>
        <n-flex>
          <n-form-item label="模式" path="questionMode">
            <n-select
              v-model:value="questionMode"
              :options="[
                { label: '全部', value: null },
                { label: '简易模式', value: 1 },
                { label: '精细模式', value: 2 },
              ]"
              placeholder="请选择"
              clearable
              style="width: 150px"
              @update:value="getList"
            />
          </n-form-item>
        </n-flex>
      </n-form>
    </n-card>

    <n-card class="flex-1">
      <template #header>
        <NButton type="primary" @click="questionModalRef.openModal('add')">
          <template #icon><icon-park-outline-add-one /></template>
          新建题目
        </NButton>
      </template>
      <NSpace vertical>
        <n-data-table :columns="columns" :data="listData" :loading="loading" />
        <Pagination :count="count" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>

  <QuestionModal ref="questionModalRef" modal-name="题目" @saved="getList" />
</template>
