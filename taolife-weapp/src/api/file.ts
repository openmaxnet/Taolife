import { get } from '@/utils/request'
import COS from 'cos-wx-sdk-v5'

// ==================== 类型定义 ====================

/**
 * COS临时凭证响应
 */
export interface CosCredentialVO {
  tmpSecretId: string
  tmpSecretKey: string
  sessionToken: string
  cosKey: string
  domain: string
  bucketName: string
  region: string
}

/** 上传文件类型 */
export interface UploadFile {
  url: string
  name: string
  type: 'image' | 'video'
}

/** 上传类型限制 */
export type FileType = 'image' | 'video' | 'all'

// ==================== 常量 ====================

const ALLOWED_EXTENSIONS = {
  image: ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.bmp'],
  video: ['.mp4', '.mov', '.avi', '.webm', '.mkv'],
}

const IMAGE_MIMES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/bmp']
const VIDEO_MIMES = ['video/mp4', 'video/quicktime', 'video/x-msvideo', 'video/webm', 'video/x-matroska']

const MIME_MAP: Record<string, string> = {
  jpg: 'image/jpeg',
  jpeg: 'image/jpeg',
  png: 'image/png',
  gif: 'image/gif',
  webp: 'image/webp',
  bmp: 'image/bmp',
  mp4: 'video/mp4',
  mov: 'video/quicktime',
  avi: 'video/x-msvideo',
  webm: 'video/webm',
  mkv: 'video/x-matroska',
}

// ==================== 工具函数 ====================

const getMimeType = (ext: string): string => MIME_MAP[ext] || 'application/octet-stream'

/**
 * 根据文件名和MIME类型判断文件类型
 */
export const getFileType = (fileName: string, mimeType: string): 'image' | 'video' | null => {
  const ext = '.' + fileName.split('.').pop()?.toLowerCase()
  if (ALLOWED_EXTENSIONS.image.includes(ext) || IMAGE_MIMES.includes(mimeType)) return 'image'
  if (ALLOWED_EXTENSIONS.video.includes(ext) || VIDEO_MIMES.includes(mimeType)) return 'video'
  return null
}

/**
 * 校验文件是否允许上传
 */
export const validateFile = (fileName: string, mimeType: string, fileType: FileType = 'all'): string | null => {
  const ext = '.' + fileName.split('.').pop()?.toLowerCase()

  let allowed: string[]
  if (fileType === 'image') allowed = ALLOWED_EXTENSIONS.image
  else if (fileType === 'video') allowed = ALLOWED_EXTENSIONS.video
  else allowed = [...ALLOWED_EXTENSIONS.image, ...ALLOWED_EXTENSIONS.video]

  if (!allowed.includes(ext)) {
    return `不支持该文件格式，请上传 ${allowed.join('、')} 格式`
  }

  const type = getFileType(fileName, mimeType)
  if (!type) {
    return `不支持该文件格式`
  }

  return null
}

// ==================== API ====================

export const getCosCredential = (fileName: string): Promise<CosCredentialVO> =>
  get<CosCredentialVO>('/api/common/cos/credential', { fileName })

// ==================== 上传 ====================

export const uploadFile = (
  filePath: string,
  fileType: FileType = 'all',
  onProgress?: (percent: number) => void
): Promise<UploadFile> => {
  return new Promise(async (resolve, reject) => {
    try {
      const fileName = filePath.split('/').pop() || 'file'
      const fileExt = fileName.split('.').pop()?.toLowerCase() || ''
      const mimeType = getMimeType(fileExt)

      const error = validateFile(fileName, mimeType, fileType)
      if (error) {
        uni.showToast({ title: error, icon: 'none' })
        reject(new Error(error))
        return
      }

      const credential = await getCosCredential(fileName)

      const cos = new COS({
        getAuthorization: (_options: unknown, callback: (credentials: COS.Credentials) => void) => {
          callback({
            TmpSecretId: credential.tmpSecretId,
            TmpSecretKey: credential.tmpSecretKey,
            SecurityToken: credential.sessionToken
          } as COS.Credentials)
        }
      })

      cos.putObject(
        {
          Bucket: credential.bucketName,
          Region: credential.region,
          Key: credential.cosKey,
          FilePath: filePath,
          onProgress: (info: COS.ProgressInfo) => {
            const percent = Math.round(info.percent * 100)
            onProgress?.(percent)
          }
        },
        (err: COS.CosError | null, _data: COS.PutObjectResult) => {
          if (err) {
            uni.showToast({ title: '上传失败：' + err.message, icon: 'none' })
            reject(err)
          } else {
            const url = `https://${credential.domain}/${credential.cosKey}`
            const type = getFileType(fileName, mimeType) || 'image'
            uni.showToast({ title: '上传成功', icon: 'success' })
            resolve({ url, name: fileName, type })
          }
        }
      )
    } catch (e: unknown) {
      const message = e instanceof Error ? e.message : '未知错误'
      uni.showToast({ title: '上传失败：' + message, icon: 'none' })
      reject(e)
    }
  })
}

// ==================== 预览 ====================

export const previewImage = (urls: string | string[], current?: string) => {
  if (typeof urls === 'string') {
    uni.previewImage({ urls: [urls], current: urls })
  } else {
    uni.previewImage({ urls, current: current || urls[0] })
  }
}

export const previewVideo = (url: string) => {
  uni.showToast({ title: '正在加载视频...', icon: 'none' })
  const pages = getCurrentPages() as any[]
  const currentPage = pages[pages.length - 1] as any
  currentPage?.setData({ videoPreviewSrc: url, showVideoPreview: true })
  setTimeout(() => {
    const ctx = uni.createVideoContext('videoPreview') as { play: () => void }
    ctx?.play()
  }, 100)
}
