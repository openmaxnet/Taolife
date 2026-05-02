/**
 * Apifox / Postman 加密加签脚本
 *
 * 使用方法：
 * 1. 将此脚本内容复制到 Apifox 的「前置脚本」中
 * 2. 在环境变量中设置：
 *    - BASE_URL: 服务端地址（如 http://localhost:35515）
 *    - HMAC_SECRET: HMAC签名密钥
 * 3. 正常编写请求，脚本会自动加密请求体并添加签名头
 *
 * 后置脚本放在底部的 decryptResponse() 调用处
 */

const crypto = require('crypto');

// ==================== 配置 ====================
const BASE_URL = apt.environment.get('BASE_URL') || 'http://localhost:35515';
const HMAC_SECRET = apt.environment.get('HMAC_SECRET') || '';

// ==================== 工具函数 ====================

/**
 * 获取服务端 RSA 公钥
 */
async function getServerPublicKey() {
  const cached = apt.variables.get('__PUBLIC_KEY__');
  const cachedExpiry = apt.variables.get('__PUBLIC_KEY_EXPIRY__');

  if (cached && cachedExpiry && Date.now() < Number(cachedExpiry)) {
    return cached;
  }

  const response = await fetch(`${BASE_URL}/api/common/security/publicKey`);
  const data = await response.json();
  apt.variables.set('__PUBLIC_KEY__', data.publicKey);
  apt.variables.set('__PUBLIC_KEY_EXPIRY__', String(Date.now() + 30 * 60 * 1000));
  return data.publicKey;
}

/**
 * RSA-OAEP 加密
 */
function rsaEncrypt(data, publicKeyBase64) {
  const publicKeyBuffer = Buffer.from(publicKeyBase64, 'base64');
  const publicKey = crypto.createPublicKey({
    key: publicKeyBuffer,
    format: 'der',
    type: 'spki',
  });

  return crypto.publicEncrypt(
    {
      key: publicKey,
      padding: crypto.constants.RSA_PKCS1_OAEP_PADDING,
      oaepHash: 'sha256',
    },
    Buffer.from(data),
  ).toString('base64');
}

/**
 * AES-256-GCM 加密
 */
function aesGcmEncrypt(plaintext, key, iv) {
  const cipher = crypto.createCipheriv('aes-256-gcm', key, iv);
  let encrypted = cipher.update(plaintext, 'utf8');
  encrypted = Buffer.concat([encrypted, cipher.final()]);
  const authTag = cipher.getAuthTag();

  // 拼接 IV + encrypted + authTag
  return Buffer.concat([iv, encrypted, authTag]).toString('base64');
}

/**
 * AES-256-GCM 解密
 */
function aesGcmDecrypt(combinedBase64, key) {
  const combined = Buffer.from(combinedBase64, 'base64');
  const iv = combined.subarray(0, 12);
  const authTag = combined.subarray(combined.length - 16);
  const encrypted = combined.subarray(12, combined.length - 16);

  const decipher = crypto.createDecipheriv('aes-256-gcm', key, iv);
  decipher.setAuthTag(authTag);
  let decrypted = decipher.update(encrypted);
  decrypted = Buffer.concat([decrypted, decipher.final()]);
  return decrypted.toString('utf8');
}

/**
 * HMAC-SHA256 签名
 */
function hmacSign(content) {
  return crypto.createHmac('sha256', HMAC_SECRET).update(content).digest('base64');
}

// ==================== 前置脚本（加密请求） ====================

async function encryptRequestBody() {
  const method = apt.request.method;
  const path = new URL(apt.request.url).pathname;

  // 生成随机 AES 密钥和 IV
  const aesKey = crypto.randomBytes(32);
  const iv = crypto.randomBytes(12);
  const nonce = crypto.randomUUID();
  const timestamp = String(Date.now());

  // 获取请求体
  const body = apt.request.body || '';

  // 加密请求体
  const encryptedBody = aesGcmEncrypt(body, aesKey, iv);

  // 获取公钥并加密 AES 密钥
  const publicKey = await getServerPublicKey();
  const encryptedKey = rsaEncrypt(aesKey, publicKey);

  // 构建签名
  const signContent = `${timestamp}\n${nonce}\n${method}\n${path}\n${encryptedBody}`;
  const sign = hmacSign(signContent);

  // 设置请求头和请求体
  apt.request.setHeader('X-Encrypt-Key', encryptedKey);
  apt.request.setHeader('X-Timestamp', timestamp);
  apt.request.setHeader('X-Nonce', nonce);
  apt.request.setHeader('X-Sign', sign);
  apt.request.body = encryptedBody;

  // 缓存 AES key 供后置脚本使用
  apt.variables.set('__CURRENT_AES_KEY__', aesKey.toString('base64'));

  console.log('[加密] 请求已加密:', method, path);
}

// ==================== 后置脚本（解密响应） ====================

function decryptResponseBody() {
  const responseData = apt.response.body;

  if (!responseData || !responseData.encrypted) {
    return;
  }

  const aesKeyBase64 = apt.variables.get('__CURRENT_AES_KEY__');
  if (!aesKeyBase64) {
    console.warn('[解密] 未找到 AES 密钥，跳过解密');
    return;
  }

  const aesKey = Buffer.from(aesKeyBase64, 'base64');
  const decryptedJson = aesGcmDecrypt(responseData.data, aesKey);

  // 替换响应体为解密后的内容
  apt.response.body = JSON.parse(decryptedJson);
  console.log('[解密] 响应已解密');
}

// 执行前置加密
encryptRequestBody().catch(e => console.error('[加密失败]', e.message));
