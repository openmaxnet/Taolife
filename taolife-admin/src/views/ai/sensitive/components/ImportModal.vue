<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { importSensitiveWords } from '@/service/api/ai'
import { NAlert, NButton, NUpload, NSpace, NSpin, NUploadDragger } from 'naive-ui'

const emit = defineEmits<{
  success: []
}>()

const { bool: modalVisible, setTrue: openModal, setFalse: closeModal } = useBoolean(false)
const { bool: uploading, setTrue: startUpload, setFalse: endUpload } = useBoolean(false)

const result = ref<Entity.ImportResultVO | null>(null)

function open() {
  result.value = null
  openModal()
}

defineExpose({ openModal: open })

async function handleUpload(options: { file: any }) {
  const file = options.file.file
  if (!file) return

  startUpload()
  result.value = null

  try {
    const res = await importSensitiveWords(file)
    result.value = res.data
    if (res.data.failCount > 0) {
      window.$message.warning(`导入完成：成功 ${res.data.successCount} 条，失败 ${res.data.failCount} 条`)
    } else {
      window.$message.success(`导入成功：${res.data.successCount} 条`)
    }
    emit('success')
  } catch {
    window.$message.error('导入失败')
  } finally {
    endUpload()
  }
}

function handleClose() {
  closeModal()
}
</script>

<template>
  <n-modal
    v-model:show="modalVisible"
    :mask-closable="false"
    preset="card"
    title="批量导入敏感词"
    style="width: 500px"
    @close="handleClose"
  >
    <NSpin :show="uploading">
      <n-upload
        :max="1"
        accept=".xlsx,.xls"
        :custom-request="handleUpload"
        :show-file-list="false"
      >
        <NUploadDragger>
          <NSpace vertical align="center" :size="20">
            <icon-park-outline-upload-one style="font-size: 48px; color: #999" />
            <div>点击或拖拽上传 Excel 文件</div>
            <div style="font-size: 12px; color: #999">
              支持 .xlsx, .xls 格式
            </div>
          </NSpace>
        </NUploadDragger>
      </n-upload>

      <div v-if="result" style="margin-top: 20px">
        <NAlert type="success" title="导入结果" style="margin-bottom: 12px">
          <NSpace vertical>
            <div>总数：{{ result.totalCount }}</div>
            <div>成功：{{ result.successCount }}</div>
            <div v-if="result.failCount > 0" type="error">失败：{{ result.failCount }}</div>
          </NSpace>
        </NAlert>

        <div v-if="result.failList.length > 0">
          <div style="font-weight: bold; margin-bottom: 8px">失败原因：</div>
          <div style="max-height: 200px; overflow-y: auto">
            <div v-for="(item, index) in result.failList" :key="index" style="font-size: 12px; color: #999">
              {{ item }}
            </div>
          </div>
        </div>
      </div>
    </NSpin>

    <template #footer>
      <NSpace justify="end">
        <NButton @click="handleClose">关闭</NButton>
      </NSpace>
    </template>
  </n-modal>
</template>
