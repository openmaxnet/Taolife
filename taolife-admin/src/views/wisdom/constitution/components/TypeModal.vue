<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { createConstitutionType, getConstitutionTypeDetail, modifyConstitutionTypeInfo } from '@/service/api/wisdom'
import { NButton, NForm, NFormItem, NInput, NInputNumber, NSpace } from 'naive-ui'

const props = defineProps<{ modalName: string }>()
const emit = defineEmits<{ saved: [] }>()

const { bool: modalVisible, setTrue: openModal, setFalse: closeModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)
const modalType = ref<'add' | 'edit'>('add')
const currentData = ref<Entity.ConstitutionTypeVO | null>(null)
const modalRef = ref()

const formData = ref({
  code: '', name: '', nameEn: '', description: '',
  characteristics: '', formationReason: '', healthAdvice: '',
  dietGuidance: '', exerciseGuidance: '', emotionGuidance: '',
  acupointGuidance: '', sortOrder: 0, isDisabled: 0 as number,
})

function open(type: 'add' | 'edit', data?: Entity.ConstitutionTypeVO) {
  modalType.value = type
  if (type === 'edit' && data) {
    currentData.value = data
    formData.value = {
      id: data.id,
      code: data.code, name: data.name, nameEn: data.nameEn || '',
      description: data.description || '', characteristics: data.characteristics || '',
      formationReason: data.formationReason || '', healthAdvice: data.healthAdvice || '',
      dietGuidance: data.dietGuidance || '', exerciseGuidance: data.exerciseGuidance || '',
      emotionGuidance: data.emotionGuidance || '', acupointGuidance: data.acupointGuidance || '',
      sortOrder: data.sortOrder || 0, isDisabled: data.isDisabled,
    }
  } else {
    currentData.value = null
    formData.value = { code: '', name: '', nameEn: '', description: '', characteristics: '',
      formationReason: '', healthAdvice: '', dietGuidance: '', exerciseGuidance: '',
      emotionGuidance: '', acupointGuidance: '', sortOrder: 0, isDisabled: 0 }
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
        await createConstitutionType(formData.value)
        window.$message.success('创建成功')
      } else {
        await modifyConstitutionTypeInfo(formData.value)
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
  <n-modal v-model:show="modalVisible" :mask-closable="false" preset="card"
    :title="modalType === 'add' ? `新建${props.modalName}` : `编辑${props.modalName}`" style="width: 700px">
    <n-form ref="modalRef" :model="formData" label-placement="left" label-width="100">
      <n-grid :cols="2" :x-gap="12">
        <n-form-item-gi label="编码" path="code">
          <n-input v-model:value="formData.code" placeholder="体质编码" />
        </n-form-item-gi>
        <n-form-item-gi label="名称" path="name">
          <n-input v-model:value="formData.name" placeholder="体质名称" />
        </n-form-item-gi>
        <n-form-item-gi label="英文名" path="nameEn">
          <n-input v-model:value="formData.nameEn" placeholder="英文名" style="width: 100%" />
        </n-form-item-gi>
        <n-form-item-gi label="排序" path="sortOrder">
          <n-input-number v-model:value="formData.sortOrder" :min="0" style="width: 100%" />
        </n-form-item-gi>
      </n-grid>
      <n-form-item label="描述" path="description">
        <n-input v-model:value="formData.description" type="textarea" placeholder="体质描述" />
      </n-form-item>
      <n-form-item label="体质特征" path="characteristics">
        <n-input v-model:value="formData.characteristics" type="textarea" placeholder="体质特征" />
      </n-form-item>
      <n-form-item label="形成原因" path="formationReason">
        <n-input v-model:value="formData.formationReason" type="textarea" placeholder="形成原因" />
      </n-form-item>
      <n-form-item label="健康建议" path="healthAdvice">
        <n-input v-model:value="formData.healthAdvice" type="textarea" placeholder="健康建议" />
      </n-form-item>
      <n-grid :cols="2" :x-gap="12">
        <n-form-item-gi label="饮食指导" path="dietGuidance">
          <n-input v-model:value="formData.dietGuidance" type="textarea" placeholder="饮食指导" />
        </n-form-item-gi>
        <n-form-item-gi label="运动指导" path="exerciseGuidance">
          <n-input v-model:value="formData.exerciseGuidance" type="textarea" placeholder="运动指导" />
        </n-form-item-gi>
        <n-form-item-gi label="情志调节" path="emotionGuidance">
          <n-input v-model:value="formData.emotionGuidance" type="textarea" placeholder="情志调节" />
        </n-form-item-gi>
        <n-form-item-gi label="穴位保健" path="acupointGuidance">
          <n-input v-model:value="formData.acupointGuidance" type="textarea" placeholder="穴位保健" />
        </n-form-item-gi>
      </n-grid>
    </n-form>
    <template #footer>
      <NSpace justify="end">
        <NButton @click="closeModal">取消</NButton>
        <NButton type="primary" :loading="submitLoading" @click="handleSubmit">确定</NButton>
      </NSpace>
    </template>
  </n-modal>
</template>
