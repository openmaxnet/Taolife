<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { createQuestionRule, getQuestionRuleDetail, modifyQuestionRuleInfo } from '@/service/api/ai'
import { NButton, NForm, NFormItem, NInput, NInputNumber, NSelect, NSpace } from 'naive-ui'

const props = defineProps<{ modalName: string }>()
const emit = defineEmits<{ saved: [] }>()

const { bool: modalVisible, setTrue: openModal, setFalse: closeModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const modalType = ref<'add' | 'edit'>('add')
const currentData = ref<Entity.QuestionRuleVO | null>(null)
const modalRef = ref()

const formData = ref({
  categoryCode: '',
  categoryName: '',
  keywords: '',
  priority: 0,
  isEnabled: 1 as number,
})

function open(type: 'add' | 'edit', data?: Entity.QuestionRuleVO) {
  modalType.value = type
  if (type === 'edit' && data) {
    currentData.value = data
    formData.value = {
      id: data.id,
      categoryCode: data.categoryCode,
      categoryName: data.categoryName,
      keywords: data.keywordsStr || '',
      priority: data.priority,
      isEnabled: data.isEnabled,
    }
  } else {
    currentData.value = null
    formData.value = { categoryCode: '', categoryName: '', keywords: '', priority: 0, isEnabled: 1 }
  }
  openModal()
}

defineExpose({ openModal: open })

async function handleSubmit() {
  modalRef.value?.validate(async (errors: any) => {
    if (errors) return
    startLoading()
    try {
      if (modalType.value === 'add') {
        await createQuestionRule(formData.value)
        window.$message.success('创建成功')
      } else {
        await modifyQuestionRuleInfo(formData.value)
        window.$message.success('修改成功')
      }
      emit('saved')
      closeModal()
    } finally {
      endLoading()
    }
  })
}
</script>

<template>
  <n-modal
    v-model:show="modalVisible"
    :mask-closable="false"
    preset="card"
    :title="modalType === 'add' ? `新建${props.modalName}` : `编辑${props.modalName}`"
    style="width: 500px"
  >
    <n-form ref="modalRef" :model="formData" label-placement="left" label-width="100">
      <n-form-item label="分类编码" path="categoryCode">
        <n-input v-model:value="formData.categoryCode" placeholder="如: constitution" />
      </n-form-item>
      <n-form-item label="分类名称" path="categoryName">
        <n-input v-model:value="formData.categoryName" placeholder="如: 体质问题" />
      </n-form-item>
      <n-form-item label="关键词" path="keywords">
        <n-input v-model:value="formData.keywords" placeholder="逗号分隔，如: 体质,阳虚" />
      </n-form-item>
      <n-form-item label="优先级" path="priority">
        <n-input-number v-model:value="formData.priority" :min="0" style="width: 100%" />
      </n-form-item>
    </n-form>
    <template #footer>
      <NSpace justify="end">
        <NButton @click="closeModal">取消</NButton>
        <NButton type="primary" :loading="submitLoading" @click="handleSubmit">确定</NButton>
      </NSpace>
    </template>
  </n-modal>
</template>
