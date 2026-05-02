<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createBanner, modifyBannerInfo } from '@/service/api/fee'
import UploadFile from '@/components/common/UploadFile/index.vue'

const props = defineProps<{ modalName: string }>()
const emit = defineEmits<{ saved: [] }>()

const { bool: modalVisible, setTrue: openModal, setFalse: closeModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

type ModalType = 'add' | 'edit'
const modalType = ref<ModalType>('add')
const editingId = ref('')

type ImageSource = 'upload' | 'custom' | null
const imageSource = ref<ImageSource>(null)

const formDefault: Entity.BannerSaveParam = {
  title: '',
  subtitle: '',
  imageUrl: '',
  linkType: undefined,
  linkUrl: '',
  sortOrder: undefined,
  status: 1,
  startDate: undefined,
  endDate: undefined,
}

const formData = ref<Entity.BannerSaveParam>({ ...formDefault })

const uploadDisabled = computed(() => imageSource.value === 'custom')
const inputDisabled = computed(() => imageSource.value === 'upload')

function open(type: ModalType, data?: Entity.BannerVO) {
  modalType.value = type
  if (type === 'edit' && data) {
    editingId.value = data.id
    formData.value = {
      id: data.id,
      title: data.title || '',
      subtitle: data.subtitle || '',
      imageUrl: data.imageUrl || '',
      linkType: data.linkType,
      linkUrl: data.linkUrl || '',
      sortOrder: data.sortOrder,
      status: data.status,
      startDate: data.startDate || undefined,
      endDate: data.endDate || undefined,
    }
    // 编辑时根据 imageUrl 判断来源
    imageSource.value = data.imageUrl ? 'upload' : null
  } else {
    editingId.value = ''
    formData.value = { ...formDefault }
    imageSource.value = null
  }
  openModal()
}

defineExpose({ openModal: open })

function handleUploadSuccess(url: string) {
  formData.value.imageUrl = url
  imageSource.value = 'upload'
}

function handleUploadRemove() {
  formData.value.imageUrl = ''
  imageSource.value = null
}

function handleInputChange(val: string) {
  formData.value.imageUrl = val
  if (val) {
    imageSource.value = 'custom'
  } else {
    imageSource.value = null
  }
}

async function handleSubmit() {
  startLoading()
  try {
    if (modalType.value === 'add') {
      await createBanner(formData.value)
      window.$message.success('创建成功')
    } else {
      await modifyBannerInfo(formData.value)
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
    :title="modalType === 'add' ? `新建${props.modalName}` : `编辑${props.modalName}`" style="width: 600px">
    <n-form :model="formData" label-placement="left" label-width="100">
      <n-grid :cols="2" :x-gap="12">
        <n-form-item-gi label="标题" path="title">
          <n-input v-model:value="formData.title" placeholder="轮播标题" />
        </n-form-item-gi>
        <n-form-item-gi label="副标题" path="subtitle">
          <n-input v-model:value="formData.subtitle" placeholder="副标题" />
        </n-form-item-gi>
        <n-form-item-gi label="排序" path="sortOrder">
          <n-input-number v-model:value="formData.sortOrder" :min="0" placeholder="排序" style="width: 100%" />
        </n-form-item-gi>
        <n-form-item-gi label="状态" path="status">
          <n-select v-model:value="formData.status" :options="[{ label: '启用', value: 1 }, { label: '禁用', value: 0 }]" />
        </n-form-item-gi>
      </n-grid>
      <n-form-item label="轮播图片" path="imageUrl">
        <div style="display: flex; flex-direction: column; gap: 8px; width: 100%">
          <n-input
            :value="formData.imageUrl"
            :disabled="inputDisabled"
            placeholder="输入图片链接或上传图片（二选一）"
            clearable
            @update:value="handleInputChange"
          />
          <UploadFile
            :disabled="uploadDisabled"
            :file-list="imageSource === 'upload' && formData.imageUrl ? [{ url: formData.imageUrl, name: 'banner', type: 'image' }] : []"
            file-type="image"
            button-text="上传图片"
            @success="handleUploadSuccess"
            @remove="handleUploadRemove"
          />
        </div>
      </n-form-item>
      <n-form-item label="链接地址" path="linkUrl">
        <n-input v-model:value="formData.linkUrl" placeholder="点击跳转地址" />
      </n-form-item>
      <n-grid :cols="2" :x-gap="12">
        <n-form-item-gi label="开始日期" path="startDate">
          <n-date-picker v-model:formatted-value="formData.startDate" type="date" value-format="yyyy-MM-dd" clearable style="width: 100%" />
        </n-form-item-gi>
        <n-form-item-gi label="结束日期" path="endDate">
          <n-date-picker v-model:formatted-value="formData.endDate" type="date" value-format="yyyy-MM-dd" clearable style="width: 100%" />
        </n-form-item-gi>
      </n-grid>
    </n-form>
    <template #footer>
      <n-space justify="end">
        <n-button @click="closeModal">取消</n-button>
        <n-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</n-button>
      </n-space>
    </template>
  </n-modal>
</template>
