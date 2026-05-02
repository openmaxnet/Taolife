<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { createSensitiveWord, getSensitiveWordDetail, modifySensitiveWordInfo } from '@/service/api/ai'
import { NButton, NForm, NFormItem, NInput, NSelect, NSpace } from 'naive-ui'

const props = defineProps<{
  modalName: string
}>()

const emit = defineEmits<{
  saved: []
}>()

const { bool: modalVisible, setTrue: openModal, setFalse: closeModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const modalType = ref<'add' | 'edit'>('add')
const currentData = ref<Entity.SensitiveWordVO | null>(null)
const modalRef = ref()

const formData = ref({
  word: '',
  wordType: 1 as number,
  severity: 1 as number,
  actionType: 1 as number,
  replaceWord: '',
  isEnabled: 1 as number,
})

const rules = {
  word: { required: true, message: '请输入敏感词', trigger: 'blur' },
  wordType: { required: true, type: 'number', message: '请选择类型', trigger: 'change' },
  severity: { required: true, type: 'number', message: '请选择严重程度', trigger: 'change' },
  actionType: { required: true, type: 'number', message: '请选择处理方式', trigger: 'change' },
}

function open(type: 'add' | 'edit', data?: Entity.SensitiveWordVO) {
  modalType.value = type
  if (type === 'edit' && data) {
    currentData.value = data
    formData.value = {
      id: data.id,
      word: data.word,
      wordType: data.wordType,
      severity: data.severity,
      actionType: data.actionType,
      replaceWord: data.replaceWord || '',
      isEnabled: data.isEnabled,
    }
    loadDetail(data.id)
  } else {
    currentData.value = null
    formData.value = {
      word: '',
      wordType: 1,
      severity: 1,
      actionType: 1,
      replaceWord: '',
      isEnabled: 1,
    }
  }
  openModal()
}

async function loadDetail(id: string) {
  await getSensitiveWordDetail(id).then((res) => {
    currentData.value = res.data
  })
}

defineExpose({ openModal: open })

async function handleSubmit() {
  modalRef.value?.validate(async (errors: any) => {
    if (errors) return

    startLoading()
    try {
      if (modalType.value === 'add') {
        await createSensitiveWord(formData.value)
        window.$message.success('创建成功')
      } else {
        await modifySensitiveWordInfo(formData.value)
        window.$message.success('修改成功')
      }
      emit('saved')
      closeModal()
    } finally {
      endLoading()
    }
  })
}

function handleClose() {
  closeModal()
}
</script>

<template>
  <n-modal
    v-model:show="modalVisible"
    :mask-closable="false"
    preset="card"
    :title="modalType === 'add' ? `新建${props.modalName}` : `编辑${props.modalName}`"
    style="width: 500px"
    @close="handleClose"
  >
    <n-form
      ref="modalRef"
      :model="formData"
      :rules="rules"
      label-placement="left"
      label-width="100"
    >
      <n-form-item label="敏感词" path="word">
        <n-input v-model:value="formData.word" placeholder="请输入敏感词" />
      </n-form-item>
      <n-form-item label="类型" path="wordType">
        <n-select
          v-model:value="formData.wordType"
          :options="[
            { label: '医疗诊断', value: 1 },
            { label: '政治敏感', value: 2 },
            { label: '不当内容', value: 3 },
          ]"
          placeholder="请选择"
        />
      </n-form-item>
      <n-form-item label="严重程度" path="severity">
        <n-select
          v-model:value="formData.severity"
          :options="[
            { label: '低', value: 1 },
            { label: '中', value: 2 },
            { label: '高', value: 3 },
          ]"
          placeholder="请选择"
        />
      </n-form-item>
      <n-form-item label="处理方式" path="actionType">
        <n-select
          v-model:value="formData.actionType"
          :options="[
            { label: '拒绝回答', value: 1 },
            { label: '替换', value: 2 },
            { label: '警告', value: 3 },
          ]"
          placeholder="请选择"
        />
      </n-form-item>
      <n-form-item label="替换词" path="replaceWord">
        <n-input v-model:value="formData.replaceWord" placeholder="请输入替换词" />
      </n-form-item>
    </n-form>

    <template #footer>
      <NSpace justify="end">
        <NButton @click="handleClose">取消</NButton>
        <NButton type="primary" :loading="submitLoading" @click="handleSubmit">确定</NButton>
      </NSpace>
    </template>
  </n-modal>
</template>
