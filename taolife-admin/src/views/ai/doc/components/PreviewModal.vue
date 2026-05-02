<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { NAlert, NAlertGroup, NDescriptions, NDescriptionsItem, NSpace } from 'naive-ui'

const { bool: modalVisible, setTrue: openModal, setFalse: closeModal } = useBoolean(false)

const currentDoc = ref<Entity.KnowledgeVO | null>(null)

function open(doc: Entity.KnowledgeVO) {
  currentDoc.value = doc
  openModal()
}

defineExpose({ openModal: open })
</script>

<template>
  <n-modal v-model:show="modalVisible" :mask-closable="false" preset="card" title="文档预览" style="width: 900px">
    <NSpace vertical :size="20" v-if="currentDoc">
      <NDescriptions :column="2" label-placement="left" bordered>
        <NDescriptionsItem label="标题">{{ currentDoc.title }}</NDescriptionsItem>
        <NDescriptionsItem label="分类">{{ currentDoc.categoryName || '-' }}</NDescriptionsItem>
        <NDescriptionsItem label="来源">{{ currentDoc.source || '-' }}</NDescriptionsItem>
        <NDescriptionsItem label="体质">{{ currentDoc.constitutionType || '-' }}</NDescriptionsItem>
      </NDescriptions>

      <n-divider>内容</n-divider>

      <div class="markdown-preview" v-html="currentDoc.content"></div>
    </NSpace>

    <template #footer>
      <NSpace justify="end">
        <NButton @click="closeModal">关闭</NButton>
      </NSpace>
    </template>
  </n-modal>
</template>

<style scoped>
.markdown-preview {
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  max-height: 500px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
