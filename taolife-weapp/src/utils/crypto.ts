/**
 * API 加密加签工具（微信小程序端）
 * 使用 crypto-js + jsencrypt 实现（小程序不支持 Web Crypto API）
 */
import CryptoJS from 'crypto-js'
import JSEncrypt from 'jsencrypt'
import { BASE_URL } from './config'

const HMAC_SECRET = import.meta.env.VITE_ENCRYPT_HMAC_SECRET || '' // HMAC 签名密钥（与后端一致）
const ENCRYPT_ENABLED = import.meta.env.VITE_API_ENCRYPT === 'true' // 是否启用加密

let cachedPublicKey: string | null = null // 缓存的服务端 RSA 公钥
let publicKeyExpiry = 0 // 公钥过期时间
const PUBLIC_KEY_REFRESH_INTERVAL = 30 * 60 * 1000 // 公钥刷新间隔（30分钟）

/**
 * 获取服务端 RSA 公钥
 * 公钥会缓存在内存中，30 分钟后自动刷新
 *
 * @returns Base64 编码的 RSA 公钥字符串
 */
async function getServerPublicKey(): Promise<string> {
  if (cachedPublicKey && Date.now() < publicKeyExpiry) {
    return cachedPublicKey
  }

  const [error, res] = await uni.request({
    url: `${BASE_URL}/api/common/security/publicKey`,
    method: 'GET',
  }) as any

  if (!error && res?.data?.publicKey) {
    cachedPublicKey = res.data.publicKey
    publicKeyExpiry = Date.now() + PUBLIC_KEY_REFRESH_INTERVAL
    return cachedPublicKey!
  }

  throw new Error('获取服务端公钥失败')
}

/**
 * 生成随机 AES-256 密钥
 * 返回 Base64 编码的 32 字节随机密钥
 *
 * @returns Base64 编码的 AES 密钥
 */
function generateAesKey(): string {
  return CryptoJS.lib.WordArray.random(32).toString(CryptoJS.enc.Base64)
}

/**
 * 生成随机 IV
 * 返回 Base64 编码的 12 字节随机初始向量
 *
 * @returns Base64 编码的 IV
 */
function generateIv(): string {
  return CryptoJS.lib.WordArray.random(12).toString(CryptoJS.enc.Base64)
}

/**
 * RSA 加密
 * 使用服务端公钥加密 AES 密钥
 * 注意：jsencrypt 使用 PKCS1v15 填充，后端对应使用 RSA/ECB/PKCS1Padding 解密
 *
 * @param data         待加密的数据
 * @param publicKeyPem RSA 公钥（PEM 或 Base64 格式）
 * @returns Base64 编码的 RSA 密文
 */
function rsaEncrypt(data: string, publicKeyPem: string): string {
  const encrypt = new JSEncrypt()
  encrypt.setPublicKey(publicKeyPem)
  const result = encrypt.encrypt(data)
  if (!result) throw new Error('RSA加密失败')
  return result
}

/**
 * AES-256-CTR 加密
 * 小程序环境下使用 CTR 模式（crypto-js 不支持 GCM 模式）
 * 将 IV 拼接到密文前面，返回 Base64 编码
 *
 * @param plaintext  明文字符串
 * @param keyBase64  Base64 编码的 AES 密钥
 * @returns Base64 编码的 IV + 密文
 */
function aesGcmEncrypt(plaintext: string, keyBase64: string): string {
  const key = CryptoJS.enc.Base64.parse(keyBase64)
  const iv = CryptoJS.lib.WordArray.random(12)

  const encrypted = CryptoJS.AES.encrypt(plaintext, key, {
    iv,
    mode: CryptoJS.mode.CTR,
    padding: CryptoJS.pad.NoPadding,
  })

  // 拼接 IV + ciphertext
  const combined = iv.clone().concat(encrypted.ciphertext)
  return CryptoJS.enc.Base64.stringify(combined)
}

/**
 * AES-256-CTR 解密
 * 从密文中提取前 12 字节作为 IV，解密剩余部分
 *
 * @param combinedBase64 Base64 编码的 IV + 密文
 * @param keyBase64      Base64 编码的 AES 密钥
 * @returns 解密后的明文字符串
 */
function aesGcmDecrypt(combinedBase64: string, keyBase64: string): string {
  const combined = CryptoJS.enc.Base64.parse(combinedBase64)

  // 提取 IV（前12字节）和密文
  const iv = CryptoJS.lib.WordArray.create(combined.words.slice(0, 3), 12)
  const ciphertext = CryptoJS.lib.WordArray.create(
    combined.words.slice(3),
    combined.sigBytes - 12,
  )

  const key = CryptoJS.enc.Base64.parse(keyBase64)
  const decrypted = CryptoJS.AES.decrypt(
    { ciphertext } as any,
    key,
    {
      iv,
      mode: CryptoJS.mode.CTR,
      padding: CryptoJS.pad.NoPadding,
    },
  )

  return decrypted.toString(CryptoJS.enc.Utf8)
}

/**
 * HMAC-SHA256 签名
 * 使用预共享的 HMAC 密钥对内容进行签名
 *
 * @param content 待签名内容
 * @returns Base64 编码的签名字符串
 */
function hmacSign(content: string): string {
  return CryptoJS.HmacSHA256(content, HMAC_SECRET).toString(CryptoJS.enc.Base64)
}

/**
 * 构建签名内容
 * 格式：timestamp + "\n" + nonce + "\n" + method + "\n" + path + "\n" + body
 *
 * @param timestamp 时间戳
 * @param nonce     随机字符串
 * @param method    HTTP 方法
 * @param path      请求路径
 * @param body      请求体（加密后）
 * @returns 拼接后的签名内容
 */
function buildSignContent(timestamp: string, nonce: string, method: string, path: string, body: string): string {
  return `${timestamp}\n${nonce}\n${method}\n${path}\n${body}`
}

/**
 * 加密请求数据
 * 执行完整的加密流程：生成 AES 密钥 → 加密请求体 → RSA 加密 AES 密钥 → HMAC 签名
 *
 * @param method HTTP 方法
 * @param path   请求路径
 * @param body   原始请求体（JSON 字符串）
 * @returns 加密后的请求配置（headers + body + aesKey）
 */
export async function encryptRequest(
  method: string,
  path: string,
  body: string,
): Promise<{ headers: Record<string, string>; body: string; aesKey: string }> {
  if (!ENCRYPT_ENABLED) {
    return { headers: {}, body, aesKey: '' }
  }

  const publicKey = await getServerPublicKey()
  const aesKey = generateAesKey()
  const nonce = CryptoJS.lib.WordArray.random(16).toString(CryptoJS.enc.Hex)
  const timestamp = String(Date.now())

  // 加密请求体
  const encryptedBody = aesGcmEncrypt(body, aesKey)

  // RSA 加密 AES 密钥
  const encryptedKey = rsaEncrypt(aesKey, publicKey)

  // 构建签名
  const signContent = buildSignContent(timestamp, nonce, method, path, encryptedBody)
  const sign = hmacSign(signContent)

  return {
    headers: {
      'X-Encrypt-Key': encryptedKey,
      'X-Timestamp': timestamp,
      'X-Nonce': nonce,
      'X-Sign': sign,
    },
    body: encryptedBody,
    aesKey,
  }
}

/**
 * 解密响应数据
 * 检测响应中的 encrypted 标记，如果为 true 则使用 AES 密钥解密
 *
 * @param responseData 服务端响应数据
 * @param aesKey       本次请求使用的 AES 密钥（Base64 编码）
 * @returns 解密后的原始响应数据
 */
export function decryptResponse(responseData: any, aesKey: string): any {
  if (!ENCRYPT_ENABLED || !responseData?.encrypted || !aesKey) {
    return responseData
  }

  const decryptedJson = aesGcmDecrypt(responseData.data, aesKey)
  return JSON.parse(decryptedJson)
}

/**
 * 是否启用加密
 *
 * @returns 加密功能是否启用
 */
export function isEncryptionEnabled(): boolean {
  return ENCRYPT_ENABLED
}
