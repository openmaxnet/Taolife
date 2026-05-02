/**
 * API 加密加签工具（浏览器端）
 * 使用 Web Crypto API 实现 RSA-OAEP + AES-256-GCM + HMAC-SHA256
 */

const HMAC_SECRET = import.meta.env.VITE_ENCRYPT_HMAC_SECRET || '' // HMAC 签名密钥（与后端一致）
const ENCRYPT_ENABLED = import.meta.env.VITE_API_ENCRYPT === 'true' // 是否启用加密

let cachedPublicKey: CryptoKey | null = null // 缓存的服务端 RSA 公钥
let publicKeyExpiry = 0 // 公钥过期时间
const PUBLIC_KEY_REFRESH_INTERVAL = 30 * 60 * 1000 // 公钥刷新间隔（30分钟）

/**
 * 获取服务端 RSA 公钥并导入为 CryptoKey
 * 公钥会缓存在内存中，30 分钟后自动刷新
 *
 * @returns 导入后的 RSA 公钥
 */
async function getServerPublicKey(): Promise<CryptoKey> {
  if (cachedPublicKey && Date.now() < publicKeyExpiry) {
    return cachedPublicKey
  }

  const baseURL = (window as unknown as Record<string, { url: { path: string } }>).__URL_MAP__?.url?.path || ''
  const response = await fetch(`${baseURL}/api/common/security/publicKey`)
  const { publicKey } = await response.json()

  // Base64 解码为二进制
  const binaryStr = atob(publicKey)
  const bytes = new Uint8Array(binaryStr.length)
  for (let i = 0; i < binaryStr.length; i++) {
    bytes[i] = binaryStr.charCodeAt(i)
  }

  // 导入为 RSA-OAEP 公钥
  cachedPublicKey = await crypto.subtle.importKey(
    'spki',
    bytes.buffer,
    { name: 'RSA-OAEP', hash: 'SHA-256' },
    false,
    ['encrypt'],
  )
  publicKeyExpiry = Date.now() + PUBLIC_KEY_REFRESH_INTERVAL
  return cachedPublicKey
}

/**
 * 生成随机 AES-256 密钥（32 字节）
 *
 * @returns 随机密钥字节数组
 */
function generateAesKey(): Uint8Array {
  return crypto.getRandomValues(new Uint8Array(32))
}

/**
 * 生成随机 IV（12 字节）
 *
 * @returns 随机 IV 字节数组
 */
function generateIv(): Uint8Array {
  return crypto.getRandomValues(new Uint8Array(12))
}

/**
 * RSA-OAEP 加密
 * 使用服务端公钥加密 AES 密钥
 *
 * @param data      待加密数据
 * @param publicKey RSA 公钥
 * @returns Base64 编码的密文
 */
async function rsaEncrypt(data: Uint8Array, publicKey: CryptoKey): Promise<string> {
  const encrypted = await crypto.subtle.encrypt({ name: 'RSA-OAEP' }, publicKey, data.buffer as ArrayBuffer)
  return btoa(String.fromCharCode(...new Uint8Array(encrypted)))
}

/**
 * AES-256-GCM 加密
 * 将 IV 拼接到密文前面，返回 Base64 编码
 *
 * @param plaintext 明文字符串
 * @param key       AES 密钥（32 字节）
 * @param iv        初始向量（12 字节）
 * @returns Base64 编码的 IV + 密文
 */
async function aesGcmEncrypt(plaintext: string, key: Uint8Array, iv: Uint8Array): Promise<string> {
  const encoded = new TextEncoder().encode(plaintext)
  const cryptoKey = await crypto.subtle.importKey('raw', key.buffer as ArrayBuffer, { name: 'AES-GCM' }, false, ['encrypt'])
  const encrypted = await crypto.subtle.encrypt({ name: 'AES-GCM', iv: iv.buffer as ArrayBuffer }, cryptoKey, encoded)

  // 拼接 IV + ciphertext
  const combined = new Uint8Array(iv.length + encrypted.byteLength)
  combined.set(iv, 0)
  combined.set(new Uint8Array(encrypted), iv.length)
  return btoa(String.fromCharCode(...combined))
}

/**
 * AES-256-GCM 解密
 * 从密文中提取前 12 字节作为 IV，解密剩余部分
 *
 * @param combinedBase64 Base64 编码的 IV + 密文
 * @param key            AES 密钥（32 字节）
 * @returns 解密后的明文字符串
 */
async function aesGcmDecrypt(combinedBase64: string, key: Uint8Array): Promise<string> {
  const combined = Uint8Array.from(atob(combinedBase64), c => c.charCodeAt(0))
  const iv = combined.slice(0, 12)
  const ciphertext = combined.slice(12)

  const cryptoKey = await crypto.subtle.importKey('raw', key.buffer as ArrayBuffer, { name: 'AES-GCM' }, false, ['decrypt'])
  const decrypted = await crypto.subtle.decrypt({ name: 'AES-GCM', iv: iv.buffer as ArrayBuffer }, cryptoKey, ciphertext)
  return new TextDecoder().decode(decrypted)
}

/**
 * HMAC-SHA256 签名
 * 使用预共享的 HMAC 密钥对内容进行签名
 *
 * @param content 待签名内容
 * @returns Base64 编码的签名字符串
 */
async function hmacSign(content: string): Promise<string> {
  const keyData = new TextEncoder().encode(HMAC_SECRET)
  const key = await crypto.subtle.importKey('raw', keyData, { name: 'HMAC', hash: 'SHA-256' }, false, ['sign'])
  const signature = await crypto.subtle.sign('HMAC', key, new TextEncoder().encode(content))
  return btoa(String.fromCharCode(...new Uint8Array(signature)))
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
): Promise<{ headers: Record<string, string>; body: string; aesKey: Uint8Array }> {
  if (!ENCRYPT_ENABLED) {
    return { headers: {}, body, aesKey: new Uint8Array(0) }
  }

  const publicKey = await getServerPublicKey()
  const aesKey = generateAesKey()
  const iv = generateIv()
  const nonce = crypto.randomUUID()
  const timestamp = String(Date.now())

  // 加密请求体
  const encryptedBody = await aesGcmEncrypt(body, aesKey, iv)

  // RSA 加密 AES 密钥
  const encryptedKey = await rsaEncrypt(aesKey, publicKey)

  // 构建签名
  const signContent = buildSignContent(timestamp, nonce, method, path, encryptedBody)
  const sign = await hmacSign(signContent)

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
 * @param aesKey       本次请求使用的 AES 密钥
 * @returns 解密后的原始响应数据
 */
// eslint-disable-next-line ts/no-explicit-any
interface EncryptedResponse {
  encrypted?: boolean
  data?: string
  [key: string]: unknown
}

export async function decryptResponse(responseData: EncryptedResponse, aesKey: Uint8Array): Promise<unknown> {
  if (!ENCRYPT_ENABLED || !responseData?.encrypted || aesKey.length === 0) {
    return responseData
  }

  const decryptedJson = await aesGcmDecrypt(responseData.data!, aesKey)
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
