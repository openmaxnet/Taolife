<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createGrowthLevel, modifyGrowthLevelInfo } from '@/service'

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

const isEnabledOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 },
]

const formDefault: Entity.GrowthLevelSaveParam = {
  level: undefined,
  levelName: '',
  minGrowthValue: undefined,
  bonusAiQuota: undefined,
  bonusPointsMultiplier: undefined,
  bonusStoreDiscount: undefined,
  privilege: '',
  isEnabled: 1,
  sortOrder: 0,
}
const formModel = ref<Entity.GrowthLevelSaveParam>({ ...formDefault })

type ModalType = 'add' | 'edit'
const modalType = shallowRef<ModalType>('add')
const editingId = ref<string>('')
const modalTitle = computed(() => {
  const titleMap: Record<ModalType, string> = {
    add: '添加',
    edit: '编辑',
  }
  return `${titleMap[modalType.value]}${modalName}`
})

async function openModal(type: ModalType = 'add', data?: Entity.GrowthLevelAdminVO) {
  emit('open')
  modalType.value = type
  showModal()
  if (type === 'add') {
    formModel.value = { ...formDefault }
    editingId.value = ''
  }
  else if (data) {
    formModel.value = {
      level: data.level,
      levelName: data.levelName ?? '',
      minGrowthValue: data.minGrowthValue,
      bonusAiQuota: data.bonusAiQuota,
      bonusPointsMultiplier: data.bonusPointsMultiplier,
      bonusStoreDiscount: data.bonusStoreDiscount,
      privilege: data.privilege ?? '',
      isEnabled: data.isEnabled ?? 1,
      sortOrder: data.sortOrder ?? 0,
    }
    editingId.value = data.id ?? ''
  }
}

function closeModal() {
  hiddenModal()
  emit('close')
}

async function handleSubmit() {
  if (!formModel.value.level || !formModel.value.levelName || formModel.value.minGrowthValue === undefined) {
    window.$message.warning('请填写必填项')
    return
  }
  startLoading()
  try {
    if (modalType.value === 'add') {
      await createGrowthLevel(formModel.value)
      window.$message.success('创建成功')
    }
    else {
      await modifyGrowthLevelInfo(editingId.value, formModel.value)
      window.$message.success('修改成功')
    }
    emit('saved')
    closeModal()
  }
  catch {
    // error handled by request
  }
  finally {
    endLoading()
  }
}

defineExpose({ openModal })
</script>

<template>
  <n-modal v-model:show="modalVisible" preset="card" :title="modalTitle" class="w-600px" :segmented="{ content: true, action: true }" @after-leave="formModel = { ...formDefault }">
    <n-form label-placement="left" :label-width="100">
      <n-form-item label="等级序号" required>
        <n-input-number v-model:value="formModel.level" :min="1" placeholder="请输入等级序号，如 1" class="w-full" />
      </n-form-item>
      <n-form-item label="等级名称" required>
        <n-input v-model:value="formModel.levelName" placeholder="如：青铜会员" />
      </n-form-item>
      <n-form-item label="最低成长值" required>
        <n-input-number v-model:value="formModel.minGrowthValue" :min="0" placeholder="如：0" class="w-full" />
      </n-form-item>
      <n-form-item label="AI额外配额">
        <n-input-number v-model:value="formModel.bonusAiQuota" :min="0" placeholder="每日额外次数" class="w-full" />
      </n-form-item>
      <n-form-item label="积分倍率">
        <n-input-number v-model:value="formModel.bonusPointsMultiplier" :min="0" :precision="1" placeholder="如：1.0" class="w-full" />
      </n-form-item>
      <n-form-item label="商城折扣">
        <n-input-number v-model:value="formModel.bonusStoreDiscount" :min="0" :max="1" :precision="2" placeholder="0.00 ~ 1.00" class="w-full" />
      </n-form-item>
      <n-form-item label="权益说明">
        <n-input v-model:value="formModel.privilege" type="textarea" placeholder="选填，如：AI配额+2,积分1.2倍" />
      </n-form-item>
      <n-form-item label="排序">
        <n-input-number v-model:value="formModel.sortOrder" :min="0" class="w-full" />
      </n-form-item>
      <n-form-item label="状态">
        <n-select v-model:value="formModel.isEnabled" :options="isEnabledOptions" placeholder="选择状态" />
      </n-form-item>
    </n-form>
    <template #action>
      <NSpace justify="center">
        <NButton @click="closeModal">取消</NButton>
        <NButton type="primary" :loading="submitLoading" @click="handleSubmit">提交</NButton>
      </NSpace>
    </template>
  </n-modal>
</template>
