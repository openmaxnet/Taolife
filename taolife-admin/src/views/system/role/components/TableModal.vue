<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createSysRole, modifySysRoleInfo } from '@/service'

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

const formDefault: Entity.RoleSaveParam = {
  roleName: '',
  roleCode: '',
  description: '',
}
const formModel = ref<Entity.RoleSaveParam>({ ...formDefault })

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

async function openModal(type: ModalType = 'add', data?: Entity.Role) {
  emit('open')
  modalType.value = type
  showModal()
  const handlers = {
    async add() {
      formModel.value = { ...formDefault }
      editingId.value = ''
    },
    async edit() {
      if (!data) return
      formModel.value = {
        roleName: data.roleName ?? '',
        roleCode: data.roleCode ?? '',
        description: data.description ?? '',
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
      await createSysRole(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifySysRoleInfo(editingId.value, formModel.value)
      window.$message.success('编辑成功')
    },
  }
  await handlers[modalType.value]()
  closeModal()
  emit('saved')
}

const rules = {
  roleName: {
    required: true,
    message: '请输入角色名称',
    trigger: 'blur',
  },
  roleCode: {
    required: true,
    message: '请输入角色编码',
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
      :label-width="100"
    >
      <n-grid :cols="2" :x-gap="18">
        <n-form-item-grid-item :span="1" label="角色名称" path="roleName">
          <n-input v-model:value="formModel.roleName" placeholder="请输入角色名称" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="角色编码" path="roleCode">
          <n-input v-model:value="formModel.roleCode" placeholder="请输入角色编码" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="描述" path="description">
          <n-input v-model:value="formModel.description" type="textarea" placeholder="请输入描述" />
        </n-form-item-grid-item>
      </n-grid>
    </n-form>
    <template #action>
      <n-space justify="center">
        <n-button @click="closeModal"> 取消 </n-button>
        <n-button type="primary" :loading="submitLoading" @click="submitModal"> 提交 </n-button>
      </n-space>
    </template>
  </n-modal>
</template>
