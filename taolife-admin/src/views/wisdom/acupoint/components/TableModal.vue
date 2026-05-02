<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createAcupoint, modifyAcupointInfo } from '@/service'

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

const formDefault: Entity.AcupointSaveParam = {
  name: '',
  meridianCode: '',
  meridianName: '',
  locationDescription: '',
  efficacy: '',
  operationMethod: '',
  massageTips: '',
  markerType: undefined,
  sortOrder: 0,
}
const formModel = ref<Entity.AcupointSaveParam>({ ...formDefault })

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

const markerTypeOptions = [
  { label: '普通', value: 1 },
  { label: '重要', value: 2 },
  { label: '关键', value: 3 },
]

async function openModal(type: ModalType = 'add', data?: Entity.Acupoint) {
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
        name: data.name ?? '',
        meridianCode: data.meridianCode ?? '',
        meridianName: data.meridianName ?? '',
        locationDescription: data.locationDescription ?? '',
        efficacy: data.efficacy ?? '',
        operationMethod: data.operationMethod ?? '',
        massageTips: data.massageTips ?? '',
        markerType: data.markerType,
        sortOrder: data.sortOrder ?? 0,
      }
      editingId.value = data.id ?? ''
    },
    async edit() {
      if (!data) return
      formModel.value = {
        name: data.name ?? '',
        meridianCode: data.meridianCode ?? '',
        meridianName: data.meridianName ?? '',
        locationDescription: data.locationDescription ?? '',
        efficacy: data.efficacy ?? '',
        operationMethod: data.operationMethod ?? '',
        massageTips: data.massageTips ?? '',
        markerType: data.markerType,
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
      await createAcupoint(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifyAcupointInfo(editingId.value, formModel.value)
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
  name: {
    required: true,
    message: '请输入名称',
    trigger: 'blur',
  },
  meridianCode: {
    required: true,
    message: '请输入经络编码',
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
        <n-form-item-grid-item :span="1" label="名称" path="name">
          <n-input v-model:value="formModel.name" placeholder="请输入名称" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="经络编码" path="meridianCode">
          <n-input v-model:value="formModel.meridianCode" placeholder="请输入经络编码" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="经络名称" path="meridianName">
          <n-input v-model:value="formModel.meridianName" placeholder="请输入经络名称" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="标记类型" path="markerType">
          <n-select v-model:value="formModel.markerType" :options="markerTypeOptions" clearable placeholder="请选择标记类型" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="定位" path="locationDescription">
          <n-input v-model:value="formModel.locationDescription" type="textarea" placeholder="请输入定位" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="功效" path="efficacy">
          <n-input v-model:value="formModel.efficacy" type="textarea" placeholder="请输入功效" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="操作方法" path="operationMethod">
          <n-input v-model:value="formModel.operationMethod" type="textarea" placeholder="请输入操作方法" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="按摩提示" path="massageTips">
          <n-input v-model:value="formModel.massageTips" type="textarea" placeholder="请输入按摩提示" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="排序" path="sortOrder">
          <n-input-number v-model:value="formModel.sortOrder" placeholder="请输入排序" class="w-full" />
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
