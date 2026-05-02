<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createAcupointCombine, modifyAcupointCombineInfo } from '@/service'
import UploadFile from '@/components/common/UploadFile/index.vue'

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

const categoryOptions = [
  { label: '体质调理', value: 1 },
  { label: '症状调理', value: 2 },
  { label: '季节养生', value: 3 },
  { label: '日常保健', value: 4 },
]

const sequenceOptions = [
  { label: '依次', value: 1 },
  { label: '同时', value: 2 },
]

const formDefault: Entity.AcupointCombineSaveParam = {
  name: '',
  category: undefined,
  targetConstitutionCodes: '',
  targetSeason: '',
  targetSymptom: '',
  description: '',
  acupointIds: '',
  acupointNames: '',
  sequence: undefined,
  operationMethod: '',
  durationMin: undefined,
  frequencyPerDay: undefined,
  efficacy: '',
  indications: '',
  contraindications: '',
  imageUrl: '',
  videoUrl: '',
  sortOrder: undefined,
}
const formModel = ref<Entity.AcupointCombineSaveParam>({ ...formDefault })

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

function rowToForm(row: Entity.AcupointCombine): Entity.AcupointCombineSaveParam {
  return {
    name: row.name ?? '',
    category: row.category,
    targetConstitutionCodes: row.targetConstitutionCodes ?? '',
    targetSeason: row.targetSeason ?? '',
    targetSymptom: row.targetSymptom ?? '',
    description: row.description ?? '',
    acupointIds: row.acupointIds ?? '',
    acupointNames: row.acupointNames ?? '',
    sequence: row.sequence,
    operationMethod: row.operationMethod ?? '',
    durationMin: row.durationMin,
    frequencyPerDay: row.frequencyPerDay,
    efficacy: row.efficacy ?? '',
    indications: row.indications ?? '',
    contraindications: row.contraindications ?? '',
    imageUrl: row.imageUrl ?? '',
    videoUrl: row.videoUrl ?? '',
    sortOrder: row.sortOrder,
  }
}

async function openModal(type: ModalType = 'add', data?: Entity.AcupointCombine) {
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
      formModel.value = rowToForm(data)
      editingId.value = data.id ?? ''
    },
    async edit() {
      if (!data) return
      formModel.value = rowToForm(data)
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
      await createAcupointCombine(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifyAcupointCombineInfo(editingId.value, formModel.value)
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
        <n-form-item-grid-item :span="1" label="分类" path="category">
          <n-select v-model:value="formModel.category" :options="categoryOptions" clearable placeholder="请选择分类" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="按摩顺序" path="sequence">
          <n-select v-model:value="formModel.sequence" :options="sequenceOptions" clearable placeholder="请选择顺序" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="时长(分钟)" path="durationMin">
          <n-input-number v-model:value="formModel.durationMin" placeholder="请输入" class="w-full" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="每日次数" path="frequencyPerDay">
          <n-input-number v-model:value="formModel.frequencyPerDay" placeholder="请输入" class="w-full" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="排序" path="sortOrder">
          <n-input-number v-model:value="formModel.sortOrder" placeholder="请输入排序" class="w-full" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="目标穴位" path="acupointNames">
          <n-input v-model:value="formModel.acupointNames" placeholder="请输入穴位名称，逗号分隔" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="功效" path="efficacy">
          <n-input v-model:value="formModel.efficacy" type="textarea" placeholder="请输入功效" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="操作方法" path="operationMethod">
          <n-input v-model:value="formModel.operationMethod" type="textarea" placeholder="请输入操作方法" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="适宜症状" path="indications">
          <n-input v-model:value="formModel.indications" type="textarea" placeholder="请输入适宜症状" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="禁忌" path="contraindications">
          <n-input v-model:value="formModel.contraindications" type="textarea" placeholder="请输入禁忌" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="描述" path="description">
          <n-input v-model:value="formModel.description" type="textarea" placeholder="请输入描述" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="图片" path="imageUrl">
          <n-input v-model:value="formModel.imageUrl" placeholder="图片URL" />
          <div class="mt-8px">
            <UploadFile
              :file-list="formModel.imageUrl ? [{ url: formModel.imageUrl, name: 'acupoint', type: 'image' }] : []"
              file-type="image"
              button-text="上传图片"
              @success="(url) => { formModel.imageUrl = url }"
              @remove="() => { formModel.imageUrl = '' }"
            />
          </div>
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="视频" path="videoUrl">
          <n-input v-model:value="formModel.videoUrl" placeholder="视频URL" />
          <div class="mt-8px">
            <UploadFile
              :file-list="formModel.videoUrl ? [{ url: formModel.videoUrl, name: 'acupoint', type: 'video' }] : []"
              file-type="video"
              button-text="上传视频"
              @success="(url) => { formModel.videoUrl = url }"
              @remove="() => { formModel.videoUrl = '' }"
            />
          </div>
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
