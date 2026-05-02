<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createSolarTerm, modifySolarTermInfo } from '@/service/api/wisdom'
import UploadFile from '@/components/common/UploadFile/index.vue'

const props = defineProps<{ modalName: string }>()
const emit = defineEmits<{ saved: [] }>()

const { bool: modalVisible, setTrue: openModal, setFalse: closeModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

type ModalType = 'add' | 'edit'
const modalType = ref<ModalType>('add')
const editingId = ref('')

const seasonOptions = [
  { label: '春', value: 1 },
  { label: '夏', value: 2 },
  { label: '秋', value: 3 },
  { label: '冬', value: 4 },
]

const formDefault: Entity.SolarTermSaveParam = {
  termName: '',
  termOrder: 1,
  startMonth: 1,
  startDay: 1,
  endMonth: 1,
  endDay: 1,
  description: '',
  season: undefined,
  coverUrl: '',
  backgroundUrl: '',
  introduction: '',
  climate: '',
  healthPrinciples: '',
  customs: '',
  proverbs: '',
  dietSummary: '',
}

const formData = ref<Entity.SolarTermSaveParam>({ ...formDefault })

function open(type: ModalType, data?: Entity.SolarTermVO) {
  modalType.value = type
  if (type === 'edit' && data) {
    editingId.value = data.id
    formData.value = {
      id: data.id,
      termName: data.termName,
      termOrder: data.termOrder || 1,
      startMonth: data.startMonth || 1,
      startDay: data.startDay || 1,
      endMonth: data.endMonth || 1,
      endDay: data.endDay || 1,
      description: data.description || '',
      season: data.season,
      coverUrl: data.coverUrl || '',
      backgroundUrl: data.backgroundUrl || '',
      introduction: data.introduction || '',
      climate: data.climate || '',
      healthPrinciples: data.healthPrinciples || '',
      customs: data.customs || '',
      proverbs: data.proverbs || '',
      dietSummary: data.dietSummary || '',
    }
  } else {
    editingId.value = ''
    formData.value = { ...formDefault }
  }
  openModal()
}

defineExpose({ openModal: open })

async function handleSubmit() {
  startLoading()
  try {
    if (modalType.value === 'add') {
      await createSolarTerm(formData.value)
      window.$message.success('创建成功')
    } else {
      await modifySolarTermInfo(formData.value)
      window.$message.success('修改成功')
    }
    emit('saved')
    closeModal()
  } finally {
    endLoading()
  }
}
</script>

<template>
  <n-modal v-model:show="modalVisible" :mask-closable="false" preset="card"
    :title="modalType === 'add' ? `新建${props.modalName}` : `编辑${props.modalName}`" style="width: 700px">
    <n-form :model="formData" label-placement="left" label-width="100">
      <n-grid :cols="2" :x-gap="12">
        <n-form-item-gi label="节气名称" path="termName">
          <n-input v-model:value="formData.termName" placeholder="如：立春" />
        </n-form-item-gi>
        <n-form-item-gi label="序号" path="termOrder">
          <n-input-number v-model:value="formData.termOrder" :min="1" :max="24" style="width: 100%" />
        </n-form-item-gi>
        <n-form-item-gi label="开始日期" path="startMonth">
          <n-space>
            <n-input-number v-model:value="formData.startMonth" :min="1" :max="12" placeholder="月" />
            <n-input-number v-model:value="formData.startDay" :min="1" :max="31" placeholder="日" />
          </n-space>
        </n-form-item-gi>
        <n-form-item-gi label="结束日期" path="endMonth">
          <n-space>
            <n-input-number v-model:value="formData.endMonth" :min="1" :max="12" placeholder="月" />
            <n-input-number v-model:value="formData.endDay" :min="1" :max="31" placeholder="日" />
          </n-space>
        </n-form-item-gi>
        <n-form-item-gi label="季节" path="season">
          <n-select v-model:value="formData.season" :options="seasonOptions" clearable placeholder="请选择季节" />
        </n-form-item-gi>
      </n-grid>
      <n-form-item label="描述" path="description">
        <n-input v-model:value="formData.description" type="textarea" placeholder="节气描述" />
      </n-form-item>
      <n-form-item label="封面图" path="coverUrl">
        <n-input v-model:value="formData.coverUrl" placeholder="封面图URL" />
        <div class="mt-8px">
          <UploadFile
            :file-list="formData.coverUrl ? [{ url: formData.coverUrl, name: 'cover', type: 'image' }] : []"
            file-type="image"
            button-text="上传封面图"
            @success="(url: string) => { formData.coverUrl = url }"
            @remove="() => { formData.coverUrl = '' }"
          />
        </div>
      </n-form-item>
      <n-form-item label="背景大图" path="backgroundUrl">
        <n-input v-model:value="formData.backgroundUrl" placeholder="背景大图URL" />
        <div class="mt-8px">
          <UploadFile
            :file-list="formData.backgroundUrl ? [{ url: formData.backgroundUrl, name: 'bg', type: 'image' }] : []"
            file-type="image"
            button-text="上传背景图"
            @success="(url: string) => { formData.backgroundUrl = url }"
            @remove="() => { formData.backgroundUrl = '' }"
          />
        </div>
      </n-form-item>
      <n-form-item label="节气简介" path="introduction">
        <n-input v-model:value="formData.introduction" type="textarea" :rows="3" placeholder="节气的由来、历史" />
      </n-form-item>
      <n-form-item label="气候特点" path="climate">
        <n-input v-model:value="formData.climate" type="textarea" :rows="3" placeholder="气候特点描述" />
      </n-form-item>
      <n-form-item label="养生原则" path="healthPrinciples">
        <n-input v-model:value="formData.healthPrinciples" type="textarea" :rows="3" placeholder="JSON数组格式的养生原则" />
      </n-form-item>
      <n-form-item label="传统习俗" path="customs">
        <n-input v-model:value="formData.customs" type="textarea" :rows="3" placeholder="JSON数组格式的传统习俗" />
      </n-form-item>
      <n-form-item label="节气谚语" path="proverbs">
        <n-input v-model:value="formData.proverbs" type="textarea" :rows="3" placeholder="JSON数组格式的节气谚语" />
      </n-form-item>
      <n-form-item label="饮食概要" path="dietSummary">
        <n-input v-model:value="formData.dietSummary" type="textarea" :rows="3" placeholder="饮食概要描述" />
      </n-form-item>
    </n-form>
    <template #footer>
      <n-space justify="end">
        <n-button @click="closeModal">取消</n-button>
        <n-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</n-button>
      </n-space>
    </template>
  </n-modal>
</template>
