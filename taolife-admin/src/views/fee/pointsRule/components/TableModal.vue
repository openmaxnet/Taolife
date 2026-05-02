<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createPointsRule, modifyPointsRule } from '@/service'

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

const pointsTypeOptions = [
  { label: '签到', value: 1 },
  { label: '健康计划', value: 2 },
  { label: '消费', value: 3 },
  { label: '广告奖励', value: 4 },
]

const conditionTypeOptions = [
  { label: '无条件', value: 0 },
  { label: '连续天数', value: 1 },
  { label: '累计天数', value: 2 },
]

const formDefault: Entity.PointsRuleSaveParam = {
  ruleCode: '',
  ruleName: '',
  pointsType: undefined,
  points: undefined,
  conditionType: 0,
  conditionValue: 0,
  dailyLimit: 0,
  isEnabled: 1,
  sortOrder: 0,
  remark: '',
}
const formModel = ref<Entity.PointsRuleSaveParam>({ ...formDefault })

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

async function openModal(type: ModalType = 'add', data?: Entity.PointsRule) {
  emit('open')
  modalType.value = type
  showModal()
  if (type === 'add') {
    formModel.value = { ...formDefault }
    editingId.value = ''
  }
  else if (data) {
    formModel.value = {
      ruleCode: data.ruleCode ?? '',
      ruleName: data.ruleName ?? '',
      pointsType: data.pointsType,
      points: data.points,
      conditionType: data.conditionType ?? 0,
      conditionValue: data.conditionValue ?? 0,
      dailyLimit: data.dailyLimit ?? 0,
      isEnabled: data.isEnabled ?? 1,
      sortOrder: data.sortOrder ?? 0,
      remark: data.remark ?? '',
    }
    editingId.value = data.id ?? ''
  }
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
      await createPointsRule(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifyPointsRule(editingId.value, formModel.value)
      window.$message.success('编辑成功')
    },
  }
  await handlers[modalType.value]()
  closeModal()
  emit('saved')
}

const rules = {
  ruleCode: {
    required: true,
    message: '请输入规则编码',
    trigger: 'blur',
  },
  ruleName: {
    required: true,
    message: '请输入规则名称',
    trigger: 'blur',
  },
  pointsType: {
    required: true,
    type: 'number' as const,
    message: '请选择积分类型',
    trigger: 'change',
  },
  points: {
    required: true,
    type: 'number' as const,
    message: '请输入积分',
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
    class="w-600px"
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
      :label-width="80"
    >
      <n-form-item label="规则编码" path="ruleCode">
        <n-input v-model:value="formModel.ruleCode" placeholder="如 DAILY_CHECKIN" :disabled="modalType === 'edit'" />
      </n-form-item>
      <n-form-item label="规则名称" path="ruleName">
        <n-input v-model:value="formModel.ruleName" placeholder="如 每日签到" />
      </n-form-item>
      <n-form-item label="积分类型" path="pointsType">
        <n-select v-model:value="formModel.pointsType" :options="pointsTypeOptions" placeholder="请选择积分类型" />
      </n-form-item>
      <n-form-item label="积分" path="points">
        <n-input-number v-model:value="formModel.points" placeholder="请输入积分" class="w-full" />
      </n-form-item>
      <n-form-item label="条件类型" path="conditionType">
        <n-select v-model:value="formModel.conditionType" :options="conditionTypeOptions" placeholder="请选择条件类型" />
      </n-form-item>
      <n-form-item v-if="formModel.conditionType !== 0" label="条件值" path="conditionValue">
        <n-input-number v-model:value="formModel.conditionValue" placeholder="天数" class="w-full" />
      </n-form-item>
      <n-form-item label="每日上限" path="dailyLimit">
        <n-input-number v-model:value="formModel.dailyLimit" placeholder="0表示不限" class="w-full" />
      </n-form-item>
      <n-form-item label="排序" path="sortOrder">
        <n-input-number v-model:value="formModel.sortOrder" placeholder="排序权重" class="w-full" />
      </n-form-item>
      <n-form-item label="备注" path="remark">
        <n-input v-model:value="formModel.remark" type="textarea" placeholder="备注信息" />
      </n-form-item>
    </n-form>
    <template #action>
      <n-space justify="center">
        <n-button @click="closeModal"> 取消 </n-button>
        <n-button type="primary" :loading="submitLoading" @click="submitModal"> 提交 </n-button>
      </n-space>
    </template>
  </n-modal>
</template>
