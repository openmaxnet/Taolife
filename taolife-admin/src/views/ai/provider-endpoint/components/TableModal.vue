<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import {
  createAiProviderEndpoint,
  getAiProviderDetail,
  getAiProviderEndpointDetail,
  getEnabledAiProviderList,
  modifyAiProviderEndpointInfo,
} from '@/service/api/ai'
import { NButton, NForm, NFormItem, NInput, NInputNumber, NSelect, NSpace, NSwitch } from 'naive-ui'

const props = defineProps<{
  modalName: string
}>()

const emit = defineEmits<{
  saved: []
}>()

const { bool: modalVisible, setTrue: openModal, setFalse: closeModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const modalType = ref<'add' | 'edit'>('add')
const currentData = ref<Entity.AiProviderEndpointVO | null>(null)
const modalRef = ref()

const providerOptions = ref<{ label: string; value: string }[]>([])

const ENDPOINT_TYPE_OPTIONS = [
  { label: '模型API', value: 'model_api' },
  { label: '工具API', value: 'tool_api' },
  { label: 'Agent API', value: 'agent_api' },
  { label: '文件API', value: 'file_api' },
  { label: '批处理API', value: 'batch_api' },
  { label: '知识库API', value: 'knowledge_api' },
  { label: '实时API', value: 'realtime_api' },
]

const REQUEST_TYPE_OPTIONS = [
  { label: 'GET', value: 'GET' },
  { label: 'POST', value: 'POST' },
  { label: 'PUT', value: 'PUT' },
  { label: 'DELETE', value: 'DELETE' },
  { label: 'WSS', value: 'WSS' },
]

const formData = ref({
  providerId: '',
  endpointType: 'model_api' as string,
  endpointUri: '',
  requestType: 'POST' as string,
  timeoutMs: 30000 as number,
  retryTimes: 3 as number,
  isEnabled: 1 as number,
  description: '',
  configJson: '',
})

const rules = {
  providerId: { required: true, message: '请选择厂商', trigger: 'change' },
  endpointType: { required: true, message: '请选择端点类型', trigger: 'change' },
  endpointUri: { required: true, message: '请输入端点URI', trigger: 'blur' },
  requestType: { required: true, message: '请选择请求类型', trigger: 'change' },
}

async function loadProviderOptions() {
  const res = await getEnabledAiProviderList()
  providerOptions.value = res.data.map(item => ({
    label: item.providerName,
    value: item.id,
  }))
}

function open(type: 'add' | 'edit', data?: Entity.AiProviderEndpointVO) {
  modalType.value = type
  loadProviderOptions()
  if (type === 'edit' && data) {
    currentData.value = data
    formData.value = {
      id: data.id,
      providerId: data.providerId,
      endpointType: data.endpointType,
      endpointUri: data.endpointUri,
      requestType: data.requestType,
      timeoutMs: data.timeoutMs || 30000,
      retryTimes: data.retryTimes || 3,
      isEnabled: data.isEnabled,
      description: data.description || '',
      configJson: data.configJson || '',
    }
    loadDetail(data.id)
  } else {
    currentData.value = null
    formData.value = {
      providerId: '',
      endpointType: 'model_api',
      endpointUri: '',
      requestType: 'POST',
      timeoutMs: 30000,
      retryTimes: 3,
      isEnabled: 1,
      description: '',
      configJson: '',
    }
  }
  openModal()
}

async function loadDetail(id: string) {
  await getAiProviderEndpointDetail(id).then((res) => {
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
        await createAiProviderEndpoint(formData.value)
        window.$message.success('创建成功')
      } else {
        await modifyAiProviderEndpointInfo(formData.value)
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
      <n-form-item label="厂商" path="providerId">
        <NSelect
          v-model:value="formData.providerId"
          :options="providerOptions"
          placeholder="请选择厂商"
          filterable
        />
      </n-form-item>
      <n-form-item label="端点类型" path="endpointType">
        <NSelect
          v-model:value="formData.endpointType"
          :options="ENDPOINT_TYPE_OPTIONS"
          placeholder="请选择"
        />
      </n-form-item>
      <n-form-item label="端点URI" path="endpointUri">
        <NInput v-model:value="formData.endpointUri" placeholder="如: /chat/completions" />
      </n-form-item>
      <n-form-item label="请求类型" path="requestType">
        <NSelect
          v-model:value="formData.requestType"
          :options="REQUEST_TYPE_OPTIONS"
          placeholder="请选择"
        />
      </n-form-item>
      <n-form-item label="超时时间(ms)" path="timeoutMs">
        <NInputNumber v-model:value="formData.timeoutMs" :min="1000" :max="300000" />
      </n-form-item>
      <n-form-item label="重试次数" path="retryTimes">
        <NInputNumber v-model:value="formData.retryTimes" :min="0" :max="10" />
      </n-form-item>
      <n-form-item label="启用状态" path="isEnabled">
        <NSwitch v-model:value="formData.isEnabled" :checked-value="1" :unchecked-value="0" />
      </n-form-item>
      <n-form-item label="描述" path="description">
        <NInput v-model:value="formData.description" type="textarea" placeholder="请输入描述" />
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
