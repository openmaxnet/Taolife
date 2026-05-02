<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createArticle, modifyArticleInfo } from '@/service'
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
  { label: '养生知识', value: 1 },
  { label: '食疗养生', value: 2 },
  { label: '运动养生', value: 3 },
  { label: '穴位养生', value: 4 },
  { label: '经络养生', value: 5 },
]

const contentTypeOptions = [
  { label: 'Markdown', value: 1 },
  { label: 'HTML', value: 2 },
]

const formDefault: Entity.ArticleSaveParam = {
  title: '',
  subtitle: '',
  category: undefined,
  author: '',
  source: '',
  summary: '',
  content: '',
  contentType: 1,
  isRecommended: 0,
  isFeatured: 0,
  sortOrder: 0,
  coverImageUrl: '',
}
const formModel = ref<Entity.ArticleSaveParam>({ ...formDefault })

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

async function openModal(type: ModalType = 'add', data?: Entity.Article) {
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
        title: data.title ?? '',
        subtitle: data.subtitle ?? '',
        category: data.category,
        author: data.author ?? '',
        source: data.source ?? '',
        summary: data.summary ?? '',
        content: data.content ?? '',
        contentType: data.contentType ?? 1,
        isRecommended: data.isRecommended ?? 0,
        isFeatured: data.isFeatured ?? 0,
        sortOrder: data.sortOrder ?? 0,
        coverImageUrl: data.coverImageUrl ?? '',
      }
      editingId.value = data.id ?? ''
    },
    async edit() {
      if (!data) return
      formModel.value = {
        title: data.title ?? '',
        subtitle: data.subtitle ?? '',
        category: data.category,
        author: data.author ?? '',
        source: data.source ?? '',
        summary: data.summary ?? '',
        content: data.content ?? '',
        contentType: data.contentType ?? 1,
        isRecommended: data.isRecommended ?? 0,
        isFeatured: data.isFeatured ?? 0,
        sortOrder: data.sortOrder ?? 0,
        coverImageUrl: data.coverImageUrl ?? '',
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
      await createArticle(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifyArticleInfo(editingId.value, formModel.value)
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
  title: {
    required: true,
    message: '请输入标题',
    trigger: 'blur',
  },
  category: {
    required: true,
    message: '请选择分类',
    trigger: 'change',
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
        <n-form-item-grid-item :span="1" label="标题" path="title">
          <n-input v-model:value="formModel.title" placeholder="请输入标题" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="副标题" path="subtitle">
          <n-input v-model:value="formModel.subtitle" placeholder="请输入副标题" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="分类" path="category">
          <n-select v-model:value="formModel.category" :options="categoryOptions" clearable placeholder="请选择分类" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="作者" path="author">
          <n-input v-model:value="formModel.author" placeholder="请输入作者" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="来源" path="source">
          <n-input v-model:value="formModel.source" placeholder="请输入来源" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="排序" path="sortOrder">
          <n-input-number v-model:value="formModel.sortOrder" placeholder="请输入排序" class="w-full" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="封面图" path="coverImageUrl">
          <n-input v-model:value="formModel.coverImageUrl" placeholder="封面图URL" />
          <div class="mt-8px">
            <UploadFile
              :file-list="formModel.coverImageUrl ? [{ url: formModel.coverImageUrl, name: 'cover', type: 'image' }] : []"
              file-type="image"
              button-text="上传封面图"
              @success="(url) => { formModel.coverImageUrl = url }"
              @remove="() => { formModel.coverImageUrl = '' }"
            />
          </div>
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="摘要" path="summary">
          <n-input v-model:value="formModel.summary" type="textarea" placeholder="请输入摘要" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="内容" path="content">
          <n-input v-model:value="formModel.content" type="textarea" :rows="8" placeholder="请输入内容" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="内容类型" path="contentType">
          <n-radio-group v-model:value="formModel.contentType">
            <n-radio v-for="item in contentTypeOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </n-radio>
          </n-radio-group>
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="是否推荐" path="isRecommended">
          <n-switch v-model:value="formModel.isRecommended" :checked-value="1" :unchecked-value="0">
            <template #checked>推荐</template>
            <template #unchecked>未推荐</template>
          </n-switch>
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="是否精选" path="isFeatured">
          <n-switch v-model:value="formModel.isFeatured" :checked-value="1" :unchecked-value="0">
            <template #checked>精选</template>
            <template #unchecked>未精选</template>
          </n-switch>
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
