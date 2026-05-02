<script setup lang="ts">
import type { FormItemRule } from 'naive-ui'
import HelpInfo from '@/components/common/HelpInfo.vue'
import { Regex } from '@/constants'
import { useBoolean } from '@/hooks'
import { createPermission, modifyPermissionInfo } from '@/service'

interface Props {
  modalName?: string
  allRoutes: Entity.Menu[]
}

const { modalName = '', allRoutes } = defineProps<Props>()

const emit = defineEmits<{
  open: []
  close: []
  saved: []
}>()

const { bool: modalVisible, setTrue: showModal, setFalse: hiddenModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const formDefault: Entity.MenuSaveParam = {
  permissionCode: '',
  path: '',
  parentId: null,
  permissionName: '',
  permissionType: 1,
  component: '',
  icon: '',
  sortOrder: 0,
}
const formModel = ref<Entity.MenuSaveParam>({ ...formDefault })

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

async function openModal(type: ModalType = 'add', data?: Entity.Menu) {
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
        parentId: data.parentId ?? null,
        permissionCode: data.permissionCode ?? '',
        permissionName: data.permissionName ?? '',
        path: data.path ?? '',
        component: data.component ?? '',
        icon: data.icon ?? '',
        sortOrder: data.sortOrder ?? 0,
        permissionType: data.permissionType ?? 1,
      }
      editingId.value = data.id ?? ''
    },
    async edit() {
      if (!data) return
      formModel.value = {
        parentId: data.parentId ?? null,
        permissionCode: data.permissionCode ?? '',
        permissionName: data.permissionName ?? '',
        path: data.path ?? '',
        component: data.component ?? '',
        icon: data.icon ?? '',
        sortOrder: data.sortOrder ?? 0,
        permissionType: data.permissionType ?? 1,
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
      await createPermission(formModel.value)
      window.$message.success('新增成功')
    },
    async edit() {
      await modifyPermissionInfo(editingId.value, formModel.value)
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

const dirTreeOptions = computed(() => {
  return filterDirectory(JSON.parse(JSON.stringify(allRoutes)))
})

function filterDirectory(node: Entity.Menu[]) {
  return node.filter((item) => {
    if (item.children) {
      const childDir = filterDirectory(item.children)
      if (childDir.length > 0) item.children = childDir
      else Reflect.deleteProperty(item, 'children')
    }

    // 目录类型：没有组件路径
    return !item.component
  })
}

const rules = {
  permissionCode: {
    required: true,
    validator(rule: FormItemRule, value: string) {
      if (!value) return new Error('请输入权限编码')
      if (!new RegExp(Regex.RouteName).test(value)) return new Error('只能包含英文数字_!@#$%^&*~-')
      return true
    },
    trigger: 'blur',
  },
  path: {
    required: true,
    message: '请输入路由路径',
    trigger: 'blur',
  },
  permissionName: {
    required: true,
    message: '请输入权限名称',
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
      :label-width="100"
      :model="formModel"
      :disabled="modalType === 'view'"
    >
      <n-grid :cols="2" :x-gap="18">
        <n-form-item-grid-item :span="2" path="parentId">
          <template #label>
            父级目录
            <HelpInfo message="不填写则为顶层菜单" />
          </template>
          <n-tree-select
            v-model:value="formModel.parentId"
            filterable
            clearable
            :options="dirTreeOptions as any"
            key-field="id"
            label-field="permissionName"
            children-field="children"
            placeholder="请选择父级目录"
          />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="权限编码" path="permissionCode">
          <n-input v-model:value="formModel.permissionCode" placeholder="Eg: system" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="权限名称" path="permissionName">
          <n-input v-model:value="formModel.permissionName" placeholder="Eg: 系统管理" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="2" label="路由路径" path="path">
          <n-input v-model:value="formModel.path" placeholder="Eg: /system/user" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="权限类型" path="permissionType">
          <n-radio-group v-model:value="formModel.permissionType" name="radiogroup">
            <n-space>
              <n-radio :value="1"> 菜单 </n-radio>
              <n-radio :value="2"> 按钮 </n-radio>
            </n-space>
          </n-radio-group>
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" label="图标" path="icon">
          <icon-select v-model:value="formModel.icon" :disabled="modalType === 'view'" />
        </n-form-item-grid-item>
        <n-form-item-grid-item v-if="formModel.permissionType === 1" :span="2" label="组件路径" path="component">
          <n-input v-model:value="formModel.component" placeholder="Eg: /system/user/index.vue，不填则为目录" />
        </n-form-item-grid-item>
        <n-form-item-grid-item :span="1" path="sortOrder">
          <template #label>
            排序序号
            <HelpInfo message="数字越小，同级中越靠前" />
          </template>
          <n-input-number v-model:value="formModel.sortOrder" />
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
