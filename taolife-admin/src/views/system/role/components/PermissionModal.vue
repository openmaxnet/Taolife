<script setup lang="ts">
import { useBoolean } from '@/hooks'
import { getPermissionTree, getSysRoleDetail, modifySysRolePermissions } from '@/service'
import { arrayToTree } from '@/utils'

const { bool: modalVisible, setTrue: showModal, setFalse: hiddenModal } = useBoolean(false)
const { bool: submitLoading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const roleId = ref<string>('')
const roleName = ref<string>('')
const permissionIds = ref<string[]>([])

const permissionTreeData = ref<Entity.Menu[]>([])

async function openModal(data: Entity.Role) {
  roleId.value = data.id ?? ''
  roleName.value = data.roleName ?? ''
  showModal()

  const [treeRes, detailRes] = await Promise.all([
    getPermissionTree(),
    getSysRoleDetail(roleId.value),
  ])
  permissionTreeData.value = arrayToTree(treeRes.data || [])
  permissionIds.value = (detailRes.data as any)?.permissionIds ?? []
}

function closeModal() {
  hiddenModal()
  endLoading()
}

defineExpose({
  openModal,
})

async function submitModal() {
  startLoading()
  await modifySysRolePermissions(roleId.value, permissionIds.value)
  window.$message.success('权限配置成功')
  closeModal()
}
</script>

<template>
  <n-modal
    v-model:show="modalVisible"
    :mask-closable="false"
    preset="card"
    :title="`权限管理 - ${roleName}`"
    class="w-700px"
    :segmented="{
      content: true,
      action: true,
    }"
  >
    <n-tree
      v-model:checked-keys="permissionIds"
      :data="permissionTreeData as any"
      key-field="id"
      label-field="permissionName"
      children-field="children"
      checkable
      cascade
      check-on-click
      selectable
      :default-expand-all="true"
      virtual-scroll
      style="max-height: 500px"
    />
    <template #action>
      <n-space justify="center">
        <n-button @click="closeModal"> 取消 </n-button>
        <n-button type="primary" :loading="submitLoading" @click="submitModal"> 保存 </n-button>
      </n-space>
    </template>
  </n-modal>
</template>
