<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { createAiPromptTemplate, getAiPromptTemplateDetail, modifyAiPromptTemplateInfo } from '@/service/api/ai'
import { NButton, NForm, NFormItem, NInput, NSelect, NSpace, NSwitch } from 'naive-ui'

const props = defineProps<{
  modalName: string
}>()

const emit = defineEmits<{
  saved: []
}>()

const { bool: modalVisible, setTrue: openModal, setFalse: closeModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const modalType = ref<'add' | 'edit'>('add')
const currentData = ref<Entity.AiPromptTemplateVO | null>(null)
const modalRef = ref()

const formData = ref({
  templateCode: '',
  templateName: '',
  templateType: 'system_prompt' as string,
  templateContent: '',
  variablesJson: '',
  version: 1 as number,
  isEnabled: 1 as number,
  description: '',
})

const rules = {
  templateCode: { required: true, message: '请输入模板编码', trigger: 'blur' },
  templateName: { required: true, message: '请输入模板名称', trigger: 'blur' },
  templateType: { required: true, message: '请选择模板类型', trigger: 'change' },
  templateContent: { required: true, message: '请输入模板内容', trigger: 'blur' },
}

function open(type: 'add' | 'edit', data?: Entity.AiPromptTemplateVO) {
  modalType.value = type
  if (type === 'edit' && data) {
    currentData.value = data
    formData.value = {
      id: data.id,
      templateCode: data.templateCode,
      templateName: data.templateName,
      templateType: data.templateType,
      templateContent: data.templateContent || '',
      variablesJson: data.variablesJson || '',
      version: data.version,
      isEnabled: data.isEnabled,
      description: data.description || '',
    }
    loadDetail(data.id)
  } else {
    currentData.value = null
    formData.value = {
      templateCode: '',
      templateName: '',
      templateType: 'system_prompt',
      templateContent: '',
      variablesJson: '',
      version: 1,
      isEnabled: 1,
      description: '',
    }
  }
  openModal()
}

async function loadDetail(id: string) {
  await getAiPromptTemplateDetail(id).then((res) => {
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
        await createAiPromptTemplate(formData.value)
        window.$message.success('创建成功')
      } else {
        await modifyAiPromptTemplateInfo(formData.value)
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
    style="width: 700px"
    @close="handleClose"
  >
    <n-form
      ref="modalRef"
      :model="formData"
      :rules="rules"
      label-placement="left"
      label-width="120"
    >
      <n-form-item label="模板编码" path="templateCode">
        <n-input
          v-model:value="formData.templateCode"
          placeholder="如: chat_system, constitution_system"
          :disabled="modalType === 'edit'"
        />
      </n-form-item>
      <n-form-item label="模板名称" path="templateName">
        <n-input v-model:value="formData.templateName" placeholder="如: AI聊天系统提示词" />
      </n-form-item>
      <n-form-item label="模板类型" path="templateType">
        <n-select
          v-model:value="formData.templateType"
          :options="[
            { label: '系统提示词', value: 'system_prompt' },
            { label: '用户提示词', value: 'user_prompt' },
            { label: '助手提示词', value: 'assistant_prompt' },
            { label: '通用提示词', value: 'common_prompt' },
            { label: '全局上下文', value: 'global_context' },
          ]"
          placeholder="请选择"
        />
      </n-form-item>
      <n-form-item label="版本" path="version">
        <n-input-number v-model:value="formData.version" :min="1" :max="100" />
      </n-form-item>
      <n-form-item label="启用状态" path="isEnabled">
        <n-switch v-model:value="formData.isEnabled" :checked-value="1" :unchecked-value="0" />
      </n-form-item>
      <n-form-item label="模板内容" path="templateContent">
        <n-input
          v-model:value="formData.templateContent"
          type="textarea"
          placeholder="支持变量占位符 ${variable}"
          :autosize="{ minRows: 8, maxRows: 15 }"
        />
      </n-form-item>
      <n-form-item label="变量定义" path="variablesJson">
        <n-input
          v-model:value="formData.variablesJson"
          type="textarea"
          placeholder='JSON格式，如: ["userConstitution", "knowledgeContext"]'
          :autosize="{ minRows: 2, maxRows: 5 }"
        />
      </n-form-item>
      <n-form-item label="描述" path="description">
        <n-input v-model:value="formData.description" type="textarea" placeholder="请输入描述" />
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
