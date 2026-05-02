package com.taolife.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM 加密解密工具
 * <p>
 * AES-256-GCM 是现代认证加密标准：
 * - 256位密钥，安全性高
 * - GCM模式提供密文完整性验证
 * - 每次加密使用随机IV，防止密文分析
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
public class AesEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits recommended for GCM
    private static final int GCM_TAG_LENGTH = 128; // authentication tag length in bits

    /** 加密密钥（从环境变量或配置获取） */
    private static volatile byte[] ENCRYPTION_KEY;

    /**
     * 初始化加密密钥
     * 从环境变量 TAOLIFE_ENCRYPTION_KEY 获取，32字节Base64解码后为256位
     *
     * @param encodedKey Base64编码的32字节密钥
     */
    public static void initKey(String encodedKey) {
        if (encodedKey == null || encodedKey.isEmpty()) {
            throw new IllegalStateException("加密密钥未配置，请设置环境变量 TAOLIFE_ENCRYPTION_KEY");
        }
        ENCRYPTION_KEY = Base64.getDecoder().decode(encodedKey);
        if (ENCRYPTION_KEY.length != 32) {
            throw new IllegalStateException("加密密钥长度必须为32字节（256位），当前: " + ENCRYPTION_KEY.length);
        }
    }

    /**
     * 获取加密密钥字节数组
     */
    private static byte[] getKey() {
        if (ENCRYPTION_KEY == null) {
            // 尝试从环境变量加载
            String envKey = System.getenv("TAOLIFE_ENCRYPTION_KEY");
            if (envKey != null && !envKey.isEmpty()) {
                initKey(envKey);
            } else {
                throw new IllegalStateException("加密密钥未初始化，请先调用 initKey() 或设置环境变量 TAOLIFE_ENCRYPTION_KEY");
            }
        }
        return ENCRYPTION_KEY;
    }

    /**
     * 加密明文
     * <p>
     * 返回格式：Base64(IV || 密文 || 认证标签)
     * 每次调用使用随机IV，相同明文产生不同密文
     *
     * @param plaintext 待加密的明文
     * @return Base64编码的密文（包含IV）
     */
    public static String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }

        try {
            byte[] iv = generateIv();
            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            byte[] ciphertext = cipher.doFinal(plaintextBytes);

            // 拼接 IV + 密文
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error("AES加密失败", e);
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * 解密密文
     *
     * @param ciphertext Base64编码的密文（包含IV）
     * @return 解密后的明文
     */
    public static String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            return ciphertext;
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(ciphertext);

            // 分离 IV 和密文
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] encryptedBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedBytes);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            byte[] plaintext = cipher.doFinal(encryptedBytes);

            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败，可能原因：密钥不匹配、密文被篡改或编码错误", e);
            throw new RuntimeException("解密失败", e);
        }
    }

    /**
     * 检查是否已配置加密密钥
     */
    public static boolean isKeyConfigured() {
        return ENCRYPTION_KEY != null;
    }

    /**
     * 生成随机IV
     */
    private static byte[] generateIv() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * 生成一个新的加密密钥（用于初始化配置）
     * <p>
     * 使用示例：
     * String newKey = Base64.getEncoder().encodeToString(AesEncryptionUtil.generateKey());
     * // 将输出的密钥设置为环境变量 TAOLIFE_ENCRYPTION_KEY
     *
     * @return 32字节的随机密钥
     */
    public static byte[] generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256, new SecureRandom());
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("生成密钥失败", e);
        }
    }

    /**
     * 生成带Base64编码的新密钥（字符串形式，方便复制）
     */
    public static String generateKeyAsBase64() {
        return Base64.getEncoder().encodeToString(generateKey());
    }
}