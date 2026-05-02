<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { createSysUser, getSysRoleList, modifySysUserInfo } from '@/service'

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

const formDefault: Entity.UserSaveParam = {
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  avatarUrl: '',
  roleId: undefined,
}
const formModel = ref<Entity.UserSaveParam>({ ...formDefault })

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

const roleOptions = ref<{ label: string, value: string }[]>([])
async function loadRoleList() {
  const { data } = await getSysRoleList()
  roleOptions.value = (data || []).map((item: Entity.Role) => ({ label: item.roleName ?? '', value: item.id ?? '' }))
}

async function openModal(type: ModalType = 'add', data?: Entity.User) {
  emit('open')
  modalType.value = type
  loadRoleList()
  showModal()
  const handlers = {
    async add() {
      formModel.value = { ...formDefault }
      editingId.value = ''
    },
    async view() {
      if (!data) return
      formModel.value = {
        username: data.username ?? '',
        realName: data.realName ?? '',
        phone: data.phone ?? '',
        email: data.email ?? '',
        avatarUrl: data.avatarUrl ?? '',
        roleId: data.roleId,
      }
      editingId.value = data.id ?? ''
    },
    async edit() {
      if (!data) return
      formModel.value = {
        username: data.username ?? '',
        realName: data.realName ?? '',
        phone: data.phone ?? '',
        email: data.email ?? '',
        avatarUrl: data.avatarUrl ?? '',
        roleId: data.roleId,
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
      await createSysUser(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifySysUserInfo(editingId.value, formModel.value)
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
  username: {
    required: true,
    message: '请输入用户名',
    trigger: 'blur',
  },
  password: {
    required: true,
    message: '请输入密码',
    trigger: 'blur',
  },
  roleId: {
    required: true,
    message: '请选择角色',
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
      :rules="modalType === 'add' ? rules : { username: rules.username, roleId: rules.roleId }"
      label-placement="left"
      :model="formModel"
      :label-width="100"
      :disabled="modalType === 'view'"
    >
      <n-grid :cols="2" :x-gap="18">
        <n-form-item-grid-item :span="1" label="用户名" path="username">
          <n-input v-model:value="formModel.username" placeholder="请输入用户名" />
        </n-form-item-grid-item>
        <n-form-item-grid-item v-if="modalType === 'add'" :span="1" label="密码" path="password">
          <n-input v-model:value="formModel.password" type="password" show-password-on="click" placeholder="请输入密码" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="真实姓名" path="realName">
          <n-input v-model:value="formModel.realName" placeholder="请输入真实姓名" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="手机号" path="phone">
          <n-input v-model:value="formModel.phone" placeholder="请输入手机号" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="邮箱" path="email">
          <n-input v-model:value="formModel.email" placeholder="请输入邮箱" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="角色" path="roleId">
          <n-select v-model:value="formModel.roleId" :options="roleOptions" clearable placeholder="请选择角色" />
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
