package com.taolife.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM 加解密工具（API传输加密用）
 * 支持外部传入 Key 和 IV，与字段加密的 AesEncryptionUtil 分离
 */
public class AesGcmApiUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding"; // AES-GCM 算法
    private static final int GCM_TAG_LENGTH = 128;  // GCM 认证标签长度（位）
    private static final int IV_LENGTH = 12;         // IV 长度（字节）
    private static final SecureRandom SECURE_RANDOM = new SecureRandom(); // 安全随机数生成器

    /**
     * 生成随机的 AES-256 密钥（32 字节）
     *
     * @return 随机密钥字节数组
     */
    public static byte[] generateKey() {
        byte[] key = new byte[32];
        SECURE_RANDOM.nextBytes(key);
        return key;
    }

    /**
     * 生成随机的 GCM IV（12 字节）
     *
     * @return 随机 IV 字节数组
     */
    public static byte[] generateIv() {
        byte[] iv = new byte[IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }

    /**
     * 加密，将 IV 拼接到密文前面
     * 输出格式：Base64(IV + ciphertext + GCM tag)
     *
     * @param plaintext 明文字节数组
     * @param key       AES 密钥（32 字节）
     * @param iv        初始向量（12 字节）
     * @return Base64 编码的密文（含 IV）
     */
    public static String encrypt(byte[] plaintext, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            byte[] ciphertext = cipher.doFinal(plaintext);

            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM加密失败", e);
        }
    }

    /**
     * 解密，从密文中提取 IV 后解密
     * 输入格式：Base64(IV + ciphertext + GCM tag)
     *
     * @param ciphertext Base64 编码的密文（含 IV）
     * @param key        AES 密钥（32 字节）
     * @return 解密后的明文字节数组
     */
    public static byte[] decrypt(String ciphertext, byte[] key) {
        try {
            byte[] combined = Base64.getDecoder().decode(ciphertext);

            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);

            byte[] encrypted = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM解密失败", e);
        }
    }

    /**
     * 使用指定 IV 加密（用于响应加密，IV 由调用方管理）
     *
     * @param plaintext 明文字节数组
     * @param key       AES 密钥（32 字节）
     * @param iv        初始向量（12 字节）
     * @return Base64 编码的密文（含 IV）
     */
    public static String encryptWithIv(byte[] plaintext, byte[] key, byte[] iv) {
        return encrypt(plaintext, key, iv);
    }

    /**
     * 仅加密密文部分（不拼接 IV），用于响应加密场景
     *
     * @param plaintext 明文字节数组
     * @param key       AES 密钥（32 字节）
     * @param iv        初始向量（12 字节）
     * @return Base64 编码的密文（不含 IV）
     */
    public static String encryptBody(byte[] plaintext, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            byte[] ciphertext = cipher.doFinal(plaintext);
            return Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM加密失败", e);
        }
    }

    /**
     * 仅解密密文部分（不含 IV），需由调用方提供 IV
     *
     * @param ciphertext Base64 编码的密文（不含 IV）
     * @param key        AES 密钥（32 字节）
     * @param iv         初始向量（12 字节）
     * @return 解密后的明文字节数组
     */
    public static byte[] decryptBody(String ciphertext, byte[] key, byte[] iv) {
        try {
            byte[] encrypted = Base64.getDecoder().decode(ciphertext);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM解密失败", e);
        }
    }
}
