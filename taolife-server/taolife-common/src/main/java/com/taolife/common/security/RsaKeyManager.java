package com.taolife.common.security;

import com.taolife.common.properties.ApiEncryptionProperties;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

/**
 * RSA 密钥对管理器
 * 服务启动时生成 RSA 密钥对，提供公钥分发和私钥解密能力
 */
@Slf4j
public class RsaKeyManager {

    /** RSA 密钥对 */
    private final KeyPair keyPair;

    /**
     * 构造方法，初始化时生成 RSA 密钥对
     *
     * @param properties 加密配置属性
     */
    public RsaKeyManager(ApiEncryptionProperties properties) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(properties.getRsaKeySize());
            this.keyPair = generator.generateKeyPair();
            log.info("RSA密钥对已生成，密钥大小: {}位", properties.getRsaKeySize());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA密钥对生成失败", e);
        }
    }

    /**
     * 获取 Base64 编码的公钥
     *
     * @return Base64 编码的公钥字符串
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * 使用 RSA-OAEP (SHA-256) 解密数据
     *
     * @param encrypted 加密后的字节数组
     * @return 解密后的原始字节数组
     */
    public byte[] decryptWithPrivateKey(byte[] encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }

    /**
     * 从 Base64 编码的加密数据解密
     *
     * @param encryptedBase64 Base64 编码的加密数据
     * @return 解密后的原始字节数组
     */
    public byte[] decryptBase64WithPrivateKey(String encryptedBase64) {
        return decryptWithPrivateKey(Base64.getDecoder().decode(encryptedBase64));
    }
}
