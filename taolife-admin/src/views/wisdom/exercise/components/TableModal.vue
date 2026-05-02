<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createExercise, modifyExerciseInfo } from '@/service'
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
  { label: '传统功法', value: 1 },
  { label: '有氧运动', value: 2 },
  { label: '力量训练', value: 3 },
  { label: '柔韧训练', value: 4 },
  { label: '休闲运动', value: 5 },
]

const intensityOptions = [
  { label: '温和', value: 1 },
  { label: '轻度', value: 2 },
  { label: '中度', value: 3 },
  { label: '重度', value: 4 },
]

const difficultyOptions = [
  { label: '简单', value: 1 },
  { label: '中等', value: 2 },
  { label: '困难', value: 3 },
]

const formDefault: Entity.ExerciseSaveParam = {
  name: '',
  category: undefined,
  intensity: undefined,
  targetConstitutionCodes: '',
  contraConstitutionCodes: '',
  targetSeason: '',
  targetAgeGroup: '',
  efficacy: '',
  indications: '',
  contraindications: '',
  description: '',
  steps: '',
  durationMin: undefined,
  caloriesConsumption: undefined,
  difficultyLevel: undefined,
  videoUrl: '',
  imageUrl: '',
  sortOrder: undefined,
}
const formModel = ref<Entity.ExerciseSaveParam>({ ...formDefault })

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

function rowToForm(row: Entity.Exercise): Entity.ExerciseSaveParam {
  return {
    name: row.name ?? '',
    namePinyin: row.namePinyin,
    category: row.category,
    intensity: row.intensity,
    targetConstitutionCodes: row.targetConstitutionCodes ?? '',
    contraConstitutionCodes: row.contraConstitutionCodes ?? '',
    targetSeason: row.targetSeason ?? '',
    targetAgeGroup: row.targetAgeGroup ?? '',
    efficacy: row.efficacy ?? '',
    indications: row.indications ?? '',
    contraindications: row.contraindications ?? '',
    description: row.description ?? '',
    steps: row.steps ?? '',
    durationMin: row.durationMin,
    caloriesConsumption: row.caloriesConsumption,
    difficultyLevel: row.difficultyLevel,
    videoUrl: row.videoUrl ?? '',
    imageUrl: row.imageUrl ?? '',
    sortOrder: row.sortOrder,
  }
}

async function openModal(type: ModalType = 'add', data?: Entity.Exercise) {
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
      await createExercise(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifyExerciseInfo(editingId.value, formModel.value)
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
        <n-form-item-grid-item :span="1" label="强度" path="intensity">
          <n-select v-model:value="formModel.intensity" :options="intensityOptions" clearable placeholder="请选择强度" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="难度" path="difficultyLevel">
          <n-select v-model:value="formModel.difficultyLevel" :options="difficultyOptions" clearable placeholder="请选择难度" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="时长(分钟)" path="durationMin">
          <n-input-number v-model:value="formModel.durationMin" placeholder="请输入" class="w-full" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="消耗卡路里" path="caloriesConsumption">
          <n-input-number v-model:value="formModel.caloriesConsumption" placeholder="请输入" class="w-full" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="功效" path="efficacy">
          <n-input v-model:value="formModel.efficacy" type="textarea" placeholder="请输入功效" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="动作步骤" path="steps">
          <n-input v-model:value="formModel.steps" type="textarea" placeholder="请输入动作步骤" />
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
              :file-list="formModel.imageUrl ? [{ url: formModel.imageUrl, name: 'exercise', type: 'image' }] : []"
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
              :file-list="formModel.videoUrl ? [{ url: formModel.videoUrl, name: 'exercise', type: 'video' }] : []"
              file-type="video"
              button-text="上传视频"
              @success="(url) => { formModel.videoUrl = url }"
              @remove="() => { formModel.videoUrl = '' }"
            />
          </div>
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
