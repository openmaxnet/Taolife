<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { createDocument, getDocumentDetail, modifyDocumentInfo } from '@/service/api/ai'
import { NButton, NForm, NFormItem, NInput, NSelect, NSpace } from 'naive-ui'

const props = defineProps<{ modalName: string }>()
const emit = defineEmits<{ saved: [] }>()

const { bool: modalVisible, setTrue: openModal, setFalse: closeModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)
const modalType = ref<'add' | 'edit'>('add')
const currentData = ref<Entity.KnowledgeVO | null>(null)
const modalRef = ref()

const formData = ref({
  title: '', content: '', category: '', tags: [] as string[], source: '',
  constitutionType: '', season: '',
})

const categoryOptions = [
  { label: '体质', value: 'constitution' },
  { label: '食疗', value: 'food' },
  { label: '穴位', value: 'acupoint' },
  { label: '养生', value: 'health' },
]

function open(type: 'add' | 'edit', data?: Entity.KnowledgeVO) {
  modalType.value = type
  if (type === 'edit' && data) {
    currentData.value = data
    formData.value = {
      id: data.id,
      title: data.title, content: data.content || '',
      category: data.category || '', tags: data.tags || [],
      source: data.source || '', constitutionType: data.constitutionType || '',
      season: data.season || '',
    }
  } else {
    currentData.value = null
    formData.value = { title: '', content: '', category: '', tags: [], source: '', constitutionType: '', season: '' }
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
        await createDocument(formData.value)
        window.$message.success('创建成功')
      } else {
        await modifyDocumentInfo(formData.value)
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
        <n-form-item-gi label="标题" path="title" style="grid-column: span 2">
          <n-input v-model:value="formData.title" placeholder="文档标题" />
        </n-form-item-gi>
        <n-form-item-gi label="分类" path="category">
          <NSelect v-model:value="formData.category" :options="categoryOptions" style="width: 100%" />
        </n-form-item-gi>
        <n-form-item-gi label="来源" path="source">
          <n-input v-model:value="formData.source" placeholder="文档来源" />
        </n-form-item-gi>
        <n-form-item-gi label="关联体质" path="constitutionType">
          <n-input v-model:value="formData.constitutionType" placeholder="体质编码" />
        </n-form-item-gi>
        <n-form-item-gi label="季节" path="season">
          <n-input v-model:value="formData.season" placeholder="如：春季" />
        </n-form-item-gi>
      </n-grid>
      <n-form-item label="内容(Markdown)" path="content">
        <n-input v-model:value="formData.content" type="textarea" :rows="10" placeholder="Markdown内容" />
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
