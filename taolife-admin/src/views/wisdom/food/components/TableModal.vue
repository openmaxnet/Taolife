<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createFood, modifyFoodInfo } from '@/service'
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
  { label: '谷物', value: 1 },
  { label: '蔬菜', value: 2 },
  { label: '水果', value: 3 },
  { label: '肉类', value: 4 },
  { label: '药材', value: 5 },
]

const natureOptions = [
  { label: '寒', value: 1 },
  { label: '凉', value: 2 },
  { label: '平', value: 3 },
  { label: '温', value: 4 },
  { label: '热', value: 5 },
]

const formDefault: Entity.FoodSaveParam = {
  name: '',
  category: undefined,
  nature: undefined,
  flavor: '',
  meridianEntry: '',
  efficacy: '',
  indications: '',
  contraindications: '',
  usage: '',
  recipes: '',
  imageUrl: '',
  sortOrder: undefined,
}
const formModel = ref<Entity.FoodSaveParam>({ ...formDefault })

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

async function openModal(type: ModalType = 'add', data?: Entity.Food) {
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
        category: data.category,
        nature: data.nature,
        flavor: data.flavor ?? '',
        meridianEntry: data.meridianEntry ?? '',
        efficacy: data.efficacy ?? '',
        indications: data.indications ?? '',
        contraindications: data.contraindications ?? '',
        usage: data.usage ?? '',
        recipes: data.recipes ?? '',
        imageUrl: data.imageUrl ?? '',
        sortOrder: data.sortOrder,
      }
      editingId.value = data.id ?? ''
    },
    async edit() {
      if (!data) return
      formModel.value = {
        name: data.name ?? '',
        category: data.category,
        nature: data.nature,
        flavor: data.flavor ?? '',
        meridianEntry: data.meridianEntry ?? '',
        efficacy: data.efficacy ?? '',
        indications: data.indications ?? '',
        contraindications: data.contraindications ?? '',
        usage: data.usage ?? '',
        recipes: data.recipes ?? '',
        imageUrl: data.imageUrl ?? '',
        sortOrder: data.sortOrder,
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
      await createFood(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifyFoodInfo(editingId.value, formModel.value)
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
        <n-form-item-grid-item :span="1" label="属性" path="nature">
          <n-select v-model:value="formModel.nature" :options="natureOptions" clearable placeholder="请选择属性" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="味道" path="flavor">
          <n-input v-model:value="formModel.flavor" placeholder="请输入味道" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="归经" path="meridianEntry">
          <n-input v-model:value="formModel.meridianEntry" placeholder="请输入归经" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="功效" path="efficacy">
          <n-input v-model:value="formModel.efficacy" type="textarea" placeholder="请输入功效" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="适宜人群" path="indications">
          <n-input v-model:value="formModel.indications" type="textarea" placeholder="请输入适宜人群" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="禁忌人群" path="contraindications">
          <n-input v-model:value="formModel.contraindications" type="textarea" placeholder="请输入禁忌人群" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="用法用量" path="usage">
          <n-input v-model:value="formModel.usage" type="textarea" placeholder="请输入用法用量" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="食谱" path="recipes">
          <n-input v-model:value="formModel.recipes" type="textarea" placeholder="请输入食谱" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="图片" path="imageUrl">
          <n-input v-model:value="formModel.imageUrl" placeholder="图片URL" />
          <div class="mt-8px">
            <UploadFile
              :file-list="formModel.imageUrl ? [{ url: formModel.imageUrl, name: 'food', type: 'image' }] : []"
              file-type="image"
              button-text="上传图片"
              @success="(url) => { formModel.imageUrl = url }"
              @remove="() => { formModel.imageUrl = '' }"
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
