<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { createAiProvider, getAiProviderDetail, modifyAiProviderInfo } from '@/service/api/ai'
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
const currentData = ref<Entity.AiProviderVO | null>(null)
const modalRef = ref()

const formData = ref({
  id: '',
  providerCode: '',
  providerName: '',
  providerType: 'chat' as string,
  apiEndpoint: '',
  apiKey: '',
  isEncrypted: 1 as number,
  isDefault: 0 as number,
  priority: 0 as number,
  status: 1 as number,
  description: '',
  configJson: '',
})

const rules = {
  providerCode: { required: true, message: '请输入厂商编码', trigger: 'blur' },
  providerName: { required: true, message: '请输入厂商名称', trigger: 'blur' },
  providerType: { required: true, message: '请选择厂商类型', trigger: 'change' },
  apiEndpoint: { required: true, message: '请输入API Endpoint', trigger: 'blur' },
}

function open(type: 'add' | 'edit', data?: Entity.AiProviderVO) {
  modalType.value = type
  if (type === 'edit' && data) {
    currentData.value = data
    formData.value = {
      id: data.id,
      providerCode: data.providerCode,
      providerName: data.providerName,
      providerType: data.providerType,
      apiEndpoint: data.apiEndpoint,
      apiKey: '', // 编辑时不回填API Key
      isEncrypted: data.isEncrypted,
      isDefault: data.isDefault,
      priority: data.priority,
      status: data.status,
      description: data.description || '',
      configJson: data.configJson || '',
    }
    loadDetail(data.id)
  } else {
    currentData.value = null
    formData.value = {
      id: '',
      providerCode: '',
      providerName: '',
      providerType: 'chat',
      apiEndpoint: '',
      apiKey: '',
      isEncrypted: 1,
      isDefault: 0,
      priority: 0,
      status: 1,
      description: '',
      configJson: '',
    }
  }
  openModal()
}

async function loadDetail(id: string) {
  await getAiProviderDetail(id).then((res) => {
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
        await createAiProvider(formData.value)
        window.$message.success('创建成功')
      } else {
        await modifyAiProviderInfo(formData.value)
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
    style="width: 600px"
    @close="handleClose"
  >
    <n-form
      ref="modalRef"
      :model="formData"
      :rules="rules"
      label-placement="left"
      label-width="120"
    >
      <n-form-item label="厂商编码" path="providerCode">
        <n-input v-model:value="formData.providerCode" placeholder="如: glm/openai/claude/qwen" />
      </n-form-item>
      <n-form-item label="厂商名称" path="providerName">
        <n-input v-model:value="formData.providerName" placeholder="如: 智谱AI/OpenAI" />
      </n-form-item>
      <n-form-item label="厂商类型" path="providerType">
        <n-select
          v-model:value="formData.providerType"
          :options="[
            { label: '聊天 (chat)', value: 'chat' },
            { label: '向量 (embedding)', value: 'embedding' },
            { label: '图像 (image)', value: 'image' },
          ]"
          placeholder="请选择"
        />
      </n-form-item>
      <n-form-item label="API Endpoint" path="apiEndpoint">
        <n-input v-model:value="formData.apiEndpoint" placeholder="如: https://open.bigmodel.cn/api/paas/v4" />
      </n-form-item>
      <n-form-item label="API Key" path="apiKey">
        <n-input
          v-model:value="formData.apiKey"
          :placeholder="modalType === 'edit' ? '不填则保持原值' : '请输入API Key'"
          type="password"
          show-password-on="click"
        />
      </n-form-item>
      <n-form-item label="API Key加密" path="isEncrypted">
        <n-switch v-model:value="formData.isEncrypted" :checked-value="1" :unchecked-value="0" />
      </n-form-item>
      <n-form-item label="默认厂商" path="isDefault">
        <n-switch v-model:value="formData.isDefault" :checked-value="1" :unchecked-value="0" />
      </n-form-item>
      <n-form-item label="优先级" path="priority">
        <n-input-number v-model:value="formData.priority" :min="0" :max="100" />
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
