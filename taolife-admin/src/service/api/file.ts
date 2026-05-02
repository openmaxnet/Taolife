import { request } from '../http'

/**
 * COS临时凭证响应
 */
export interface CosCredentialVO {
  /** 临时SecretId */
  tmpSecretId: string
  /** 临时SecretKey */
  tmpSecretKey: string
  /** 安全令牌 */
  sessionToken: string
  /** COS对象路径 */
  cosKey: string
  /** COS访问域名 */
  domain: string
  /** Bucket名称 */
  bucketName: string
  /** 地域 */
  region: string
}

/**
 * 获取COS临时凭证
 * 用于前端直传文件到腾讯云COS
 *
 * @param fileName 文件名（带扩展名）
 * @returns COS临时凭证信息
 */
export function getCosCredential(fileName: string) {
  return request.Get<Service.ResponseResult<CosCredentialVO>>('/api/common/cos/credential', {
    params: { fileName }
  })
}
