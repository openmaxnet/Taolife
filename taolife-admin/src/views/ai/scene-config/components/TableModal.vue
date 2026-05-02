<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import {
  createAiSceneConfig,
  getAiModelListByProviderId,
  getAiPromptTemplatePage,
  getAiSceneConfigDetail,
  getEnabledAiProviderList,
  modifyAiSceneConfigInfo,
} from '@/service/api/ai'
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
const currentData = ref<Entity.AiSceneConfigVO | null>(null)
const modalRef = ref()

const modelInstanceOptions = ref<{ label: string; value: string }[]>([])
const promptTemplateOptions = ref<{ label: string; value: string }[]>([])

const formData = ref({
  sceneCode: '',
  sceneName: '',
  modelInstanceId: '',
  promptTemplateId: '',
  parametersJson: '',
  extraConfigJson: '',
  isEnabled: 1 as number,
  description: '',
})

const rules = {
  sceneCode: { required: true, message: '请输入场景编码', trigger: 'blur' },
  sceneName: { required: true, message: '请输入场景名称', trigger: 'blur' },
  modelInstanceId: { required: true, message: '请选择模型实例', trigger: 'change' },
  promptTemplateId: { required: true, message: '请选择提示词模板', trigger: 'change' },
}

async function loadModelInstanceOptions() {
  const res = await getEnabledAiProviderList('chat')
  const allInstances: { label: string; value: string }[] = []
  for (const provider of res.data) {
    const instanceRes = await getAiModelListByProviderId(provider.id)
    for (const inst of instanceRes.data) {
      allInstances.push({
        label: `${inst.modelName} (${inst.modelCode})`,
        value: inst.id,
      })
    }
  }
  modelInstanceOptions.value = allInstances
}

async function loadPromptTemplateOptions() {
  const res = await getAiPromptTemplatePage({ pageNo: 1, pageSize: 100, templateType: 'system_prompt' })
  promptTemplateOptions.value = res.data.list.map(item => ({
    label: `${item.templateName} (${item.templateCode})`,
    value: item.id,
  }))
}

function open(type: 'add' | 'edit', data?: Entity.AiSceneConfigVO) {
  modalType.value = type
  if (type === 'edit' && data) {
    currentData.value = data
    formData.value = {
      id: data.id,
      sceneCode: data.sceneCode,
      sceneName: data.sceneName,
      modelInstanceId: data.modelInstanceId,
      promptTemplateId: data.promptTemplateId,
      parametersJson: data.parametersJson || '',
      extraConfigJson: data.extraConfigJson || '',
      isEnabled: data.isEnabled,
      description: data.description || '',
    }
    loadDetail(data.id)
  } else {
    currentData.value = null
    formData.value = {
      sceneCode: '',
      sceneName: '',
      modelInstanceId: '',
      promptTemplateId: '',
      parametersJson: '',
      extraConfigJson: '',
      isEnabled: 1,
      description: '',
    }
  }
  loadModelInstanceOptions()
  loadPromptTemplateOptions()
  openModal()
}

async function loadDetail(id: string) {
  await getAiSceneConfigDetail(id).then((res) => {
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
        await createAiSceneConfig(formData.value)
        window.$message.success('创建成功')
      } else {
        await modifyAiSceneConfigInfo(formData.value)
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
      <n-form-item label="场景编码" path="sceneCode">
        <n-input
          v-model:value="formData.sceneCode"
          placeholder="如: ai_chat, constitution_assessment"
          :disabled="modalType === 'edit'"
        />
      </n-form-item>
      <n-form-item label="场景名称" path="sceneName">
        <n-input v-model:value="formData.sceneName" placeholder="如: AI聊天, 体质评估" />
      </n-form-item>
      <n-form-item label="模型实例" path="modelInstanceId">
        <n-select
          v-model:value="formData.modelInstanceId"
          :options="modelInstanceOptions"
          placeholder="请选择模型实例"
          filterable
        />
      </n-form-item>
      <n-form-item label="提示词模板" path="promptTemplateId">
        <n-select
          v-model:value="formData.promptTemplateId"
          :options="promptTemplateOptions"
          placeholder="请选择提示词模板"
          filterable
        />
      </n-form-item>
      <n-form-item label="启用状态" path="isEnabled">
        <n-switch v-model:value="formData.isEnabled" :checked-value="1" :unchecked-value="0" />
      </n-form-item>
      <n-form-item label="额外配置" path="extraConfigJson">
        <n-input
          v-model:value="formData.extraConfigJson"
          type="textarea"
          placeholder='JSON格式，如: {"similarityThreshold": 0.7}'
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
