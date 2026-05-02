<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { useBoolean } from '@/hooks'
import { createQuestion, getQuestionDetail, modifyQuestionInfo, createOption, modifyOptionInfo, removeOption } from '@/service/api/wisdom'
import { NButton, NDataTable, NForm, NFormItem, NInput, NInputNumber, NSpace, NPopconfirm, NGrid, NDrawer, NDrawerContent } from 'naive-ui'

const props = defineProps<{ modalName: string }>()
const emit = defineEmits<{ saved: [] }>()

const { bool: drawerVisible, setTrue: openDrawer, setFalse: closeDrawer } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)
const drawerType = ref<'add' | 'edit'>('add')
const currentData = ref<Entity.QuestionVO | null>(null)
const formRef = ref()

const formData = ref({
  questionNo: 0, questionText: '', questionTextSecondary: '',
  category: '', dimension: '', answerType: 1 as number, isRequired: 1 as number,
  questionMode: 1 as number, weight: 1, reverseScore: 0 as number,
  isConsistencyCheck: 0 as number, sortOrder: 0, isDisabled: 0 as number,
})

// 选项相关
const options = ref<Entity.OptionVO[]>([])
const editingOptionId = ref<string | null>(null)
const optionForm = ref({
  optionNo: 0, optionText: '', optionValue: 1, targetTypeCode: '', reverseValue: 0, sortOrder: 0,
})

const optionColumns: DataTableColumns<Entity.OptionVO> = [
  { title: '选项号', align: 'center', key: 'optionNo', width: 100 },
  { title: '选项内容', align: 'center', key: 'optionText', width: 200 },
  { title: '分值', align: 'center', key: 'optionValue', width: 80 },
  { title: '关联体质', align: 'center', key: 'targetTypeName', width: 150 },
  { title: '排序', align: 'center', key: 'sortOrder', width: 80 },
  {
    title: '操作',
    align: 'center',
    key: 'actions',
    width: 200,
    render: (row) => (
      <NSpace justify="center">
        <NButton size="small" onClick={() => handleEditOption(row)}>编辑</NButton>
        <NPopconfirm onPositiveClick={() => handleDeleteOption(row.id)}>
          {{
            default: () => '确认删除？',
            trigger: () => <NButton size="small" type="error">删除</NButton>,
          }}
        </NPopconfirm>
      </NSpace>
    ),
  },
]

function open(type: 'add' | 'edit', data?: Entity.QuestionVO) {
  drawerType.value = type
  if (type === 'edit' && data) {
    currentData.value = data
    formData.value = {
      id: data.id,
      questionNo: data.questionNo || 0, questionText: data.questionText,
      questionTextSecondary: data.questionTextSecondary || '', category: data.category || '',
      dimension: data.dimension || '', answerType: data.answerType || 1,
      isRequired: data.isRequired || 1, questionMode: data.questionMode || 1,
      weight: data.weight || 1, reverseScore: data.reverseScore || 0,
      isConsistencyCheck: data.isConsistencyCheck || 0, sortOrder: data.sortOrder || 0,
      isDisabled: data.isDisabled || 0,
    }
    loadOptions(data.id)
  } else {
    currentData.value = null
    formData.value = {
      questionNo: 0, questionText: '', questionTextSecondary: '', category: '',
      dimension: '', answerType: 1, isRequired: 1, questionMode: 1, weight: 1,
      reverseScore: 0, isConsistencyCheck: 0, sortOrder: 0, isDisabled: 0,
    }
    options.value = []
  }
  editingOptionId.value = null
  optionForm.value = { optionNo: 0, optionText: '', optionValue: 1, targetTypeCode: '', reverseValue: 0, sortOrder: 0 }
  openDrawer()
}

defineExpose({ openModal: open })

async function loadOptions(questionId: string) {
  await getQuestionDetail(questionId).then((res) => {
    options.value = res.data.options || []
  })
}

// 选项操作
function handleEditOption(option: Entity.OptionVO) {
  editingOptionId.value = option.id
  optionForm.value = {
    id: option.id,
    optionNo: option.optionNo || 0,
    optionText: option.optionText,
    optionValue: option.optionValue || 1,
    targetTypeCode: option.targetTypeCode || '',
    reverseValue: option.reverseValue || 0,
    sortOrder: option.sortOrder || 0,
  }
}

async function handleAddOption() {
  if (!currentData.value?.id) {
    window.$message.warning('请先保存题目后再添加选项')
    return
  }
  startLoading()
  try {
    await createOption({ questionId: currentData.value.id, ...optionForm.value })
    window.$message.success('添加成功')
    editingOptionId.value = null
    optionForm.value = { optionNo: 0, optionText: '', optionValue: 1, targetTypeCode: '', reverseValue: 0, sortOrder: 0 }
    await loadOptions(currentData.value.id)
  } finally {
    endLoading()
  }
}

async function handleUpdateOption() {
  if (!editingOptionId.value || !currentData.value?.id) return
  startLoading()
  try {
    await modifyOptionInfo(optionForm.value)
    window.$message.success('修改成功')
    editingOptionId.value = null
    optionForm.value = { optionNo: 0, optionText: '', optionValue: 1, targetTypeCode: '', reverseValue: 0, sortOrder: 0 }
    await loadOptions(currentData.value.id)
  } finally {
    endLoading()
  }
}

async function handleDeleteOption(id: string) {
  if (!currentData.value?.id) return
  await removeOption(id)
  window.$message.success('删除成功')
  await loadOptions(currentData.value.id)
}

function handleCancelEdit() {
  editingOptionId.value = null
  optionForm.value = { optionNo: 0, optionText: '', optionValue: 1, targetTypeCode: '', reverseValue: 0, sortOrder: 0 }
}

async function handleSubmit() {
  formRef.value?.validate(async (errors: any) => {
    if (errors) return
    startLoading()
    try {
      if (drawerType.value === 'add') {
        const result = await createQuestion(formData.value)
        await loadOptions(result.data)
        window.$message.success('创建成功')
      } else {
        await modifyQuestionInfo(formData.value)
        window.$message.success('修改成功')
      }
      emit('saved')
      closeDrawer()
    } finally {
      endLoading()
    }
  })
}

function handleClose() {
  closeDrawer()
}
</script>

<template>
  <NDrawer
    v-model:show="drawerVisible"
    :width="900"
    :mask-closable="false"
    @close="handleClose"
  >
    <NDrawerContent :title="drawerType === 'add' ? `新建${props.modalName}` : `编辑${props.modalName}`" closable>
      <n-form ref="formRef" :model="formData" label-placement="left" label-width="90">
        <n-grid :cols="3" :x-gap="12">
          <n-form-item-gi label="题号" path="questionNo">
            <n-input-number v-model:value="formData.questionNo" :min="0" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="模式" path="questionMode">
            <n-select v-model:value="formData.questionMode" :options="[
              { label: '简易模式', value: 1 },
              { label: '精细模式', value: 2 },
            ]" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="题型" path="answerType">
            <n-select v-model:value="formData.answerType" :options="[
              { label: '单选', value: 1 },
              { label: '多选', value: 2 },
            ]" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="必答" path="isRequired">
            <n-select v-model:value="formData.isRequired" :options="[
              { label: '是', value: 1 },
              { label: '否', value: 0 },
            ]" style="width: 100%" />
          </n-form-item-gi>
          <n-form-item-gi label="分类" path="category">
            <n-input v-model:value="formData.category" placeholder="分类" />
          </n-form-item-gi>
          <n-form-item-gi label="维度" path="dimension">
            <n-input v-model:value="formData.dimension" placeholder="维度" />
          </n-form-item-gi>
        </n-grid>
        <n-form-item label="题目内容" path="questionText">
          <n-input v-model:value="formData.questionText" type="textarea" placeholder="题目内容" :autosize="{ minRows: 2, maxRows: 4 }" />
        </n-form-item>
        <n-form-item label="补充说明" path="questionTextSecondary">
          <n-input v-model:value="formData.questionTextSecondary" type="textarea" placeholder="补充说明" :autosize="{ minRows: 1, maxRows: 3 }" />
        </n-form-item>

        <!-- 选项管理区域 -->
        <n-divider title-placement="left">选项管理</n-divider>

        <!-- 选项列表 -->
        <div class="option-table">
          <n-data-table :columns="optionColumns" :data="options" :bordered="true" size="small" />
        </div>

        <!-- 添加/编辑表单（始终在末尾） -->
        <div class="option-form">
          <!-- 编辑模式 -->
          <n-form v-if="editingOptionId" :model="optionForm" inline>
            <n-form-item label="选项号" path="optionNo" class="option-form-item">
              <n-input-number v-model:value="optionForm.optionNo" :min="0" style="width: 70px" />
            </n-form-item>
            <n-form-item label="选项内容" path="optionText" class="option-form-item" style="flex: 1">
              <n-input v-model:value="optionForm.optionText" placeholder="选项内容" />
            </n-form-item>
            <n-form-item label="分值" path="optionValue" class="option-form-item">
              <n-input-number v-model:value="optionForm.optionValue" :min="0" style="width: 60px" />
            </n-form-item>
            <n-form-item label="关联体质" path="targetTypeCode" class="option-form-item">
              <n-input v-model:value="optionForm.targetTypeCode" placeholder="体质编码" style="width: 100px" />
            </n-form-item>
            <n-form-item label="排序" path="sortOrder" class="option-form-item">
              <n-input-number v-model:value="optionForm.sortOrder" :min="0" style="width: 60px" />
            </n-form-item>
            <n-form-item label=" " class="option-form-item">
              <n-space>
                <NButton type="primary" size="small" :loading="submitLoading" onClick={handleUpdateOption}>保存</NButton>
                <NButton size="small" onClick={handleCancelEdit}>取消</NButton>
              </n-space>
            </n-form-item>
          </n-form>

          <!-- 添加模式 -->
          <n-form v-else inline>
            <n-form-item label="选项号" class="option-form-item">
              <n-input-number v-model:value="optionForm.optionNo" :min="0" style="width: 100px" />
            </n-form-item>
            <n-form-item label="选项内容" class="option-form-item" style="width: 200px">
              <n-input v-model:value="optionForm.optionText" placeholder="选项内容" />
            </n-form-item>
            <n-form-item label="分值" class="option-form-item">
              <n-input-number v-model:value="optionForm.optionValue" :min="0" style="width: 80px" />
            </n-form-item>
            <n-form-item label="关联体质" class="option-form-item">
              <n-input v-model:value="optionForm.targetTypeCode" placeholder="体质编码" style="width: 200px" />
            </n-form-item>
            <n-form-item label="排序" class="option-form-item">
              <n-input-number v-model:value="optionForm.sortOrder" :min="0" style="width: 80px" />
            </n-form-item>
            <n-form-item label=" " class="option-form-item">
              <NButton type="primary" size="small" :loading="submitLoading" onClick={handleAddOption}>添加</NButton>
            </n-form-item>
          </n-form>
        </div>

        <n-empty v-if="options.length === 0 && !editingOptionId" description="暂无选项，请添加" style="margin-top: 16px" />
      </n-form>

      <template #footer>
        <n-space justify="end">
          <NButton type="primary" :loading="submitLoading" onClick={handleSubmit}>确定</NButton>
        </n-space>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>

<style scoped>
.option-form {
  padding: 12px;
  background-color: var(--n-color-elevated);
  border-radius: 4px;
  margin-top: 12px;
}

.option-table {
  margin-bottom: 0;
}

.option-form-item {
  margin-bottom: 0;
}
</style>
