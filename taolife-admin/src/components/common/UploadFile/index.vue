<script setup lang="ts">
import { getCosCredential } from '@/service/api/file'
import COS from 'cos-js-sdk-v5'

// ==================== 类型定义 ====================

interface UploadFile {
  url: string
  name: string
  type: 'image' | 'video'
}

interface Props {
  /** 上传类型 */
  fileType?: 'image' | 'video' | 'all'
  /** 文件大小限制（MB） */
  maxSize?: number
  /** 最多文件数 */
  maxCount?: number
  /** 是否禁用 */
  disabled?: boolean
  /** 按钮文字 */
  buttonText?: string
  /** 初始文件列表 */
  fileList?: UploadFile[]
}

const props = withDefaults(defineProps<Props>(), {
  fileType: 'all',
  maxSize: 10,
  maxCount: 1,
  disabled: false,
  buttonText: '上传文件',
  fileList: () => []
})

const emit = defineEmits<{
  success: [url: string, file: File]
  error: [message: string]
  uploading: [progress: number]
  remove: [index: number]
}>()

// ==================== 常量 ====================

const ALLOWED_EXTENSIONS = {
  image: ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.bmp'],
  video: ['.mp4', '.mov', '.avi', '.webm', '.mkv'],
}

const IMAGE_MIMES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/bmp']
const VIDEO_MIMES = ['video/mp4', 'video/quicktime', 'video/x-msvideo', 'video/webm', 'video/x-matroska']

// ==================== 状态 ====================

const uploading = ref(false)
const progress = ref(0)
const fileList = ref<UploadFile[]>([...props.fileList])
const previewVisible = ref(false)
const previewUrl = ref('')
const previewType = ref<'image' | 'video'>('image')

// 计算当前类型允许的扩展名
const allowedExtensions = computed(() => {
  if (props.fileType === 'all') {
    return [...ALLOWED_EXTENSIONS.image, ...ALLOWED_EXTENSIONS.video]
  }
  return ALLOWED_EXTENSIONS[props.fileType]
})

// 计算 accept 属性值
const accept = computed(() => {
  if (props.fileType === 'image') return ALLOWED_EXTENSIONS.image.join(',')
  if (props.fileType === 'video') return ALLOWED_EXTENSIONS.video.join(',')
  return [...ALLOWED_EXTENSIONS.image, ...ALLOWED_EXTENSIONS.video].join(',')
})

// ==================== 工具函数 ====================

function getFileType(fileName: string, mimeType: string): 'image' | 'video' | null {
  const ext = '.' + fileName.split('.').pop()?.toLowerCase()
  if (ALLOWED_EXTENSIONS.image.includes(ext) || IMAGE_MIMES.includes(mimeType)) return 'image'
  if (ALLOWED_EXTENSIONS.video.includes(ext) || VIDEO_MIMES.includes(mimeType)) return 'video'
  return null
}

function validateFile(file: File): string | null {
  const ext = '.' + file.name.split('.').pop()?.toLowerCase()
  const mimeType = file.type

  if (!allowedExtensions.value.includes(ext)) {
    return `不支持该文件格式，请上传 ${allowedExtensions.value.join('、')} 格式`
  }

  const fileType = getFileType(file.name, mimeType)
  if (!fileType) {
    return `不支持该文件格式，请上传 ${allowedExtensions.value.join('、')} 格式`
  }

  if (file.size > props.maxSize * 1024 * 1024) {
    return `文件大小不能超过 ${props.maxSize}MB`
  }

  return null
}

// ==================== 初始化已有文件的签名URL ====================

watch(
  () => props.fileList,
  (list) => {
    fileList.value = [...list]
  },
  { immediate: true }
)

// ==================== 上传 ====================

async function uploadFile(file: File) {
  const error = validateFile(file)
  if (error) {
    window.$message.error(error)
    emit('error', error)
    return
  }

  uploading.value = true
  progress.value = 0

  try {
    const res = await getCosCredential(file.name)
    const credential = res.data
    if (!credential) {
      window.$message.error('获取上传凭证失败')
      emit('error', '获取上传凭证失败')
      return
    }

    const cos = new COS({
      Protocol: 'https:',
      getAuthorization: (_options: unknown, callback: (credentials: COS.Credentials) => void) => {
        callback({
          TmpSecretId: credential.tmpSecretId,
          TmpSecretKey: credential.tmpSecretKey,
          SecurityToken: credential.sessionToken,
          StartTime: Math.floor(Date.now() / 1000),
          ExpiredTime: Math.floor(Date.now() / 1000) + 1800
        } as COS.Credentials)
      }
    })

    cos.putObject(
      {
        Bucket: credential.bucketName,
        Region: credential.region,
        Key: credential.cosKey,
        Body: file,
        onProgress: (info: COS.ProgressInfo) => {
          progress.value = Math.round(info.percent * 100)
          emit('uploading', progress.value)
        }
      },
      (err: COS.CosError | null, _data: COS.PutObjectResult) => {
        uploading.value = false
        if (err) {
          console.error('[COS Upload Error]', JSON.stringify({ statusCode: err.statusCode, errorCode: (err as any).error, message: err.message }, null, 2))
          window.$message.error('上传失败：' + err.message)
          emit('error', err.message)
        } else {
          const originalUrl = `${credential.domain}/${credential.cosKey}`
          const fileType = getFileType(file.name, file.type) || 'image'

          if (fileList.value.length >= props.maxCount) {
            fileList.value.splice(0, fileList.value.length)
          }
          fileList.value.push({ url: originalUrl, name: file.name, type: fileType })

          window.$message.success('上传成功')
          emit('success', originalUrl, file)
        }
      }
    )
  } catch (e: unknown) {
    uploading.value = false
    const message = e instanceof Error ? e.message : '未知错误'
    window.$message.error('上传失败：' + message)
    emit('error', message)
  }
}

// ==================== 预览 ====================

function handlePreview(item: UploadFile) {
  previewUrl.value = item.url
  previewType.value = item.type
  previewVisible.value = true
}

function handleRemove(index: number) {
  fileList.value.splice(index, 1)
  emit('remove', index)
}

function handleChange(options: { file: unknown }) {
  const file = (options.file as { file?: File }).file
  if (file) {
    uploadFile(file)
  }
}
</script>

<template>
  <div class="upload-file">
    <n-upload
      :accept="accept"
      :disabled="disabled || uploading"
      :show-file-list="false"
      :custom-request="handleChange"
    >
      <n-button :loading="uploading" :disabled="disabled" type="primary">
        <template v-if="uploading">
          上传中 {{ progress }}%
        </template>
        <template v-else>
          {{ buttonText }}
        </template>
      </n-button>
    </n-upload>

    <div v-if="fileList.length > 0" class="file-list">
      <div
        v-for="(item, index) in fileList"
        :key="item.url"
        class="file-item"
      >
        <div v-if="item.type === 'image'" class="file-thumbnail" @click="handlePreview(item)">
          <img :src="item.url" :alt="item.name" />
        </div>

        <div v-else class="file-thumbnail video" @click="handlePreview(item)">
          <video :src="item.url" />
          <div class="video-play-icon">
            <icon-park-outline-play-one />
          </div>
        </div>

        <div class="file-actions">
          <n-button size="tiny" text @click="handleRemove(index)">
            <template #icon>
              <icon-park-outline-close style="color: #fff; font-size: 14px" />
            </template>
          </n-button>
        </div>
      </div>
    </div>

    <n-modal
      v-model:show="previewVisible"
      preset="card"
      :title="previewType === 'image' ? '图片预览' : '视频预览'"
      style="max-width: 90vw"
      @close="previewVisible = false"
    >
      <img v-if="previewType === 'image'" :src="previewUrl" style="max-width: 100%" />
      <video
        v-else
        :src="previewUrl"
        controls
        style="max-width: 100%"
      />
    </n-modal>
  </div>
</template>

<style scoped>
.upload-file {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.file-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.file-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.file-thumbnail {
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.file-thumbnail img,
.file-thumbnail video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-thumbnail.video {
  position: relative;
}

.video-play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 24px;
  color: white;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.5);
}

.file-actions {
  position: absolute;
  top: 4px;
  right: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.45);
  cursor: pointer;
}

.file-actions:hover {
  background: rgba(0, 0, 0, 0.7);
}
</style>
