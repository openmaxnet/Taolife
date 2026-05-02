<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createAgreement, modifyAgreementInfo, getAgreementDetail } from '@/service'
import RichTextEditor from '@/components/custom/Editor/RichTextEditor/index.vue'

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

const formDefault: Entity.SysAgreementSaveParam = {
  code: '',
  title: '',
  content: '',
}
const formModel = ref<Entity.SysAgreementSaveParam>({ ...formDefault })

type ModalType = 'add' | 'view' | 'edit'
const modalType = shallowRef<ModalType>('add')
const editingId = ref<string>('')
const modalTitle = computed(() => {
  const titleMap: Record<ModalType, string> = { add: '添加', view: '查看', edit: '编辑' }
  return `${titleMap[modalType.value]}${modalName}`
})

async function openModal(type: ModalType = 'add', data?: Entity.SysAgreement) {
  emit('open')
  modalType.value = type
  showModal()

  if (type === 'add') {
    formModel.value = { ...formDefault }
    editingId.value = ''
  } else if (data?.id) {
    const res = await getAgreementDetail(data.id)
    const detail = res.data
    formModel.value = {
      code: detail.code ?? '',
      title: detail.title ?? '',
      content: detail.content ?? '',
    }
    editingId.value = data.id
  }
}

function closeModal() {
  hiddenModal()
  endLoading()
  emit('close')
}

defineExpose({ openModal })

const formRef = ref()
async function submitModal() {
  await formRef.value?.validate()
  startLoading()
  const handlers = {
    async add() {
      await createAgreement(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifyAgreementInfo(editingId.value, formModel.value)
      window.$message.success('编辑成功')
    },
    async view() {},
  }
  await handlers[modalType.value]()
  closeModal()
  emit('saved')
}

const rules = {
  code: { required: true, message: '请输入标识', trigger: 'blur' },
  title: { required: true, message: '请输入标题', trigger: 'blur' },
}
</script>

<template>
  <n-modal
    v-model:show="modalVisible"
    :mask-closable="false"
    preset="card"
    :title="modalTitle"
    class="w-800px"
    :segmented="{ content: true, action: true }"
  >
    <n-form
      ref="formRef"
      :rules="rules"
      label-placement="left"
      :model="formModel"
      :label-width="80"
      :disabled="modalType === 'view'"
    >
      <n-grid :cols="2" :x-gap="18">
        <n-form-item-grid-item :span="1" label="标识" path="code">
          <n-input v-model:value="formModel.code" placeholder="如 about / agreement / privacy" :disabled="modalType === 'edit'" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="标题" path="title">
          <n-input v-model:value="formModel.title" placeholder="请输入标题" />
        </n-form-item-grid-item>
      </n-grid>
      <n-form-item label="内容" path="content" :show-feedback="false" class="mt-12px">
        <div class="w-full">
          <RichTextEditor v-model="formModel.content" :disabled="modalType === 'view'" />
        </div>
      </n-form-item>
    </n-form>
    <template v-if="modalType !== 'view'" #action>
      <n-space justify="center">
        <n-button @click="closeModal">取消</n-button>
        <n-button type="primary" :loading="submitLoading" @click="submitModal">提交</n-button>
      </n-space>
    </template>
  </n-modal>
</template>
