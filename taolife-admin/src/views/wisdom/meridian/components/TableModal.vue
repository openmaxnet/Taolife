<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createMeridian, modifyMeridianInfo } from '@/service'

interface Props {
  modalName?: string
}

const { modalName = '' } = defineProps<Props>()

const emit = defineEmits<{
  open: []
  close: []
  saved: []
}>()

const { bool: modalVisible, setTrue: showModal, setFalse: hiddenModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const formDefault: Entity.MeridianSaveParam = {
  code: '',
  name: '',
  category: undefined,
  description: '',
  mainIndications: '',
  sortOrder: 0,
}
const formModel = ref<Entity.MeridianSaveParam>({ ...formDefault })

type ModalType = 'add' | 'view' | 'edit'
const modalType = shallowRef<ModalType>('add')
const editingId = ref<string>('')
const modalTitle = computed(() => {
  const titleMap: Record<ModalType, string> = {
    add: '添加',
    view: '查看',
    edit: '编辑',
  }
  return `${titleMap[modalType.value]}${modalName}`
})

const categoryOptions = [
  { label: '十二正经', value: 1 },
  { label: '奇经八脉', value: 2 },
]

async function openModal(type: ModalType = 'add', data?: Entity.Meridian) {
  emit('open')
  modalType.value = type
  showModal()
  const handlers = {
    async add() {
      formModel.value = { ...formDefault }
      editingId.value = ''
    },
    async view() {
      if (!data) return
      formModel.value = {
        code: data.code ?? '',
        name: data.name ?? '',
        category: data.category,
        description: data.description ?? '',
        mainIndications: data.mainIndications ?? '',
        sortOrder: data.sortOrder ?? 0,
      }
      editingId.value = data.id ?? ''
    },
    async edit() {
      if (!data) return
      formModel.value = {
        code: data.code ?? '',
        name: data.name ?? '',
        category: data.category,
        description: data.description ?? '',
        mainIndications: data.mainIndications ?? '',
        sortOrder: data.sortOrder ?? 0,
      }
      editingId.value = data.id ?? ''
    },
  }
  await handlers[type]()
}

function closeModal() {
  hiddenModal()
  endLoading()
  emit('close')
}

defineExpose({
  openModal,
})

const formRef = ref()
async function submitModal() {
  await formRef.value?.validate()
  startLoading()
  const handlers = {
    async add() {
      await createMeridian(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifyMeridianInfo(editingId.value, formModel.value)
      window.$message.success('编辑成功')
    },
    async view() {
      // no-op
    },
  }
  await handlers[modalType.value]()
  closeModal()
  emit('saved')
}

const rules = {
  code: {
    required: true,
    message: '请输入编码',
    trigger: 'blur',
  },
  name: {
    required: true,
    message: '请输入名称',
    trigger: 'blur',
  },
}
</script>

<template>
  <n-modal
    v-model:show="modalVisible"
    :mask-closable="false"
    preset="card"
    :title="modalTitle"
    class="w-700px"
    :segmented="{
      content: true,
      action: true,
    }"
  >
    <n-form
      ref="formRef"
      :rules="rules"
      label-placement="left"
      :model="formModel"
      :label-width="100"
      :disabled="modalType === 'view'"
    >
      <n-grid :cols="2" :x-gap="18">
        <n-form-item-grid-item :span="1" label="编码" path="code">
          <n-input v-model:value="formModel.code" placeholder="请输入编码" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="名称" path="name">
          <n-input v-model:value="formModel.name" placeholder="请输入名称" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="分类" path="category">
          <n-select v-model:value="formModel.category" :options="categoryOptions" clearable placeholder="请选择分类" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="排序" path="sortOrder">
          <n-input-number v-model:value="formModel.sortOrder" placeholder="请输入排序" class="w-full" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="描述" path="description">
          <n-input v-model:value="formModel.description" type="textarea" placeholder="请输入描述" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="主治" path="mainIndications">
          <n-input v-model:value="formModel.mainIndications" type="textarea" placeholder="请输入主治" />
        </n-form-item-grid-item>
      </n-grid>
    </n-form>
    <template v-if="modalType !== 'view'" #action>
      <n-space justify="center">
        <n-button @click="closeModal"> 取消 </n-button>
        <n-button type="primary" :loading="submitLoading" @click="submitModal"> 提交 </n-button>
      </n-space>
    </template>
  </n-modal>
</template>
