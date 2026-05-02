<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { getAccountDetail, modifyMemberLevel } from '@/service'

const emit = defineEmits<{ saved: [] }>()

const { bool: modalVisible, setTrue: showModal, setFalse: hideModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const currentId = ref('')
const currentMemberLevel = ref(0)

const memberLevelOptions = [
  { label: '普通用户', value: 0 },
  { label: '月卡会员', value: 1 },
  { label: '年卡会员', value: 2 },
  { label: '终身会员', value: 3 },
]

const formData = ref({
  memberLevel: 0,
  memberExpireTime: null as string | null,
})

const rules = {
  memberLevel: { required: true, type: 'number', message: '请选择会员等级', trigger: 'change' },
}

async function open(id: string) {
  currentId.value = id
  const res = await getAccountDetail(id)
  const data = res.data
  if (data) {
    currentMemberLevel.value = data.memberLevel ?? 0
    formData.value = {
      memberLevel: data.memberLevel ?? 0,
      memberExpireTime: data.memberExpireTime || null,
    }
  }
  showModal()
}

function closeModal() {
  hideModal()
  endLoading()
}

async function submit() {
  startLoading()
  try {
    await modifyMemberLevel({
      id: currentId.value,
      memberLevel: formData.value.memberLevel,
      memberExpireTime: formData.value.memberLevel === 0 || formData.value.memberLevel === 3
        ? undefined
        : formData.value.memberExpireTime,
    })
    window.$message.success('修改成功')
    closeModal()
    emit('saved')
  } finally {
    endLoading()
  }
}

defineExpose({ openModal: open })
</script>

<template>
  <n-modal
    v-model:show="modalVisible"
    :mask-closable="false"
    preset="card"
    title="修改会员等级"
    class="w-480px"
    :segmented="{ content: true, action: true }"
  >
    <n-form :model="formData" :rules="rules" label-placement="left" label-width="90">
      <n-form-item label="会员等级" path="memberLevel">
        <n-select v-model:value="formData.memberLevel" :options="memberLevelOptions" placeholder="请选择" />
      </n-form-item>
      <n-form-item
        v-if="formData.memberLevel !== 0 && formData.memberLevel !== 3"
        label="到期时间"
        path="memberExpireTime"
      >
        <n-date-picker
          v-model:value="formData.memberExpireTime"
          type="datetime"
          clearable
          placeholder="请选择到期时间"
          style="width: 100%"
        />
      </n-form-item>
    </n-form>
    <template #action>
      <n-space justify="center">
        <n-button @click="closeModal">取消</n-button>
        <n-button type="primary" :loading="submitLoading" @click="submit">保存</n-button>
      </n-space>
    </template>
  </n-modal>
</template>
