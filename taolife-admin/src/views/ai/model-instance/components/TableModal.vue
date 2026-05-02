<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { createAiModel, getAiModelDetail, getEnabledAiProviderList, modifyAiModelInfo } from '@/service/api/ai'
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
const currentData = ref<Entity.AiModelVO | null>(null)
const modalRef = ref()

const providerOptions = ref<{ label: string; value: string }[]>([])

const formData = ref({
  id: '',
  providerId: '',
  modelCode: '',
  modelName: '',
  modelType: 'chat' as string,
  temperature: 0.2 as number,
  maxTokens: 5000 as number,
  topP: undefined as number | undefined,
  supportsThinking: 0 as number,
  supportsImage: 0 as number,
  maxConcurrency: 0 as number,
  extraParamsJson: '' as string,
  capabilitiesJson: '' as string,
  isDefault: 0 as number,
  status: 1 as number,
})

const rules = {
  providerId: { required: true, message: '请选择厂商', trigger: 'change' },
  modelCode: { required: true, message: '请输入模型编码', trigger: 'blur' },
  modelName: { required: true, message: '请输入模型名称', trigger: 'blur' },
  modelType: { required: true, message: '请选择模型类型', trigger: 'change' },
}

async function loadProviderOptions() {
  const res = await getEnabledAiProviderList()
  providerOptions.value = res.data.map(item => ({
    label: `${item.providerName} (${item.providerCode})`,
    value: item.id,
  }))
}

function getFormDataDefault() {
  return {
    id: '',
    providerId: '',
    modelCode: '',
    modelName: '',
    modelType: 'chat' as string,
    temperature: 0.2,
    maxTokens: 5000,
    topP: undefined as number | undefined,
    supportsThinking: 0,
    supportsImage: 0,
    maxConcurrency: 0,
    extraParamsJson: '',
    capabilitiesJson: '',
    isDefault: 0,
    status: 1,
  }
}

function open(type: 'add' | 'edit', data?: Entity.AiModelVO) {
  modalType.value = type
  if (type === 'edit' && data) {
    currentData.value = data
    formData.value = {
      id: data.id,
      providerId: data.providerId,
      modelCode: data.modelCode,
      modelName: data.modelName,
      modelType: data.modelType,
      temperature: data.temperature ?? 0.2,
      maxTokens: data.maxTokens ?? 5000,
      topP: data.topP ?? undefined,
      supportsThinking: data.supportsThinking ?? 0,
      supportsImage: data.supportsImage ?? 0,
      maxConcurrency: data.maxConcurrency ?? 0,
      extraParamsJson: data.extraParamsJson ?? '',
      capabilitiesJson: data.capabilitiesJson ?? '',
      isDefault: data.isDefault,
      status: data.status,
    }
    loadDetail(data.id)
  } else {
    currentData.value = null
    formData.value = getFormDataDefault()
  }
  loadProviderOptions()
  openModal()
}

async function loadDetail(id: string) {
  await getAiModelDetail(id).then((res) => {
    currentData.value = res.data
    const d = res.data
    formData.value.temperature = d.temperature ?? 0.2
    formData.value.maxTokens = d.maxTokens ?? 5000
    formData.value.topP = d.topP ?? undefined
    formData.value.supportsThinking = d.supportsThinking ?? 0
    formData.value.supportsImage = d.supportsImage ?? 0
    formData.value.maxConcurrency = d.maxConcurrency ?? 0
    formData.value.extraParamsJson = d.extraParamsJson ?? ''
    formData.value.capabilitiesJson = d.capabilitiesJson ?? ''
  })
}

defineExpose({ openModal: open })

async function handleSubmit() {
  modalRef.value?.validate(async (errors: any) => {
    if (errors) return

    startLoading()
    try {
      const submitData = { ...formData.value }
      if (modalType.value === 'add') {
        await createAiModel(submitData)
        window.$message.success('创建成功')
      } else {
        await modifyAiModelInfo(submitData)
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
      <n-form-item label="所属厂商" path="providerId">
        <n-select
          v-model:value="formData.providerId"
          :options="providerOptions"
          placeholder="请选择厂商"
          filterable
        />
      </n-form-item>
      <n-form-item label="模型编码" path="modelCode">
        <n-input v-model:value="formData.modelCode" placeholder="如: glm-4-flash, gpt-4" />
      </n-form-item>
      <n-form-item label="模型名称" path="modelName">
        <n-input v-model:value="formData.modelName" placeholder="如: GLM-4-Flash" />
      </n-form-item>
      <n-form-item label="模型类型" path="modelType">
        <n-select
          v-model:value="formData.modelType"
          :options="[
            { label: '聊天 (chat)', value: 'chat' },
            { label: '向量 (embedding)', value: 'embedding' },
          ]"
          placeholder="请选择"
        />
      </n-form-item>
      <n-form-item label="默认模型" path="isDefault">
        <n-switch v-model:value="formData.isDefault" :checked-value="1" :unchecked-value="0" />
      </n-form-item>
      <n-form-item label="温度参数">
        <n-input-number v-model:value="formData.temperature" :min="0" :max="2" :step="0.1" placeholder="0-2，默认0.2" style="width: 100%" />
      </n-form-item>
      <n-form-item label="最大Token数">
        <n-input-number v-model:value="formData.maxTokens" :min="1" :step="1000" placeholder="默认5000" style="width: 100%" />
      </n-form-item>
      <n-form-item label="Top-P参数">
        <n-input-number v-model:value="formData.topP" :min="0" :max="1" :step="0.1" placeholder="可选" style="width: 100%" />
      </n-form-item>
      <n-form-item label="最大并发数">
        <n-input-number v-model:value="formData.maxConcurrency" :min="0" :step="1" placeholder="0=不限" style="width: 100%" />
      </n-form-item>
      <n-form-item label="支持思考模式">
        <n-switch v-model:value="formData.supportsThinking" :checked-value="1" :unchecked-value="0" />
      </n-form-item>
      <n-form-item label="支持图像输入">
        <n-switch v-model:value="formData.supportsImage" :checked-value="1" :unchecked-value="0" />
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
