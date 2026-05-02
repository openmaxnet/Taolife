package com.taolife.common.properties;

import com.taolife.common.utils.AesEncryptionUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 加密配置属性
 * 用于配置AES-256-GCM加密密钥
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "taolife.encryption")
public class EncryptionProperties {

    /**
     * AES-256-GCM加密密钥（Base64编码，32字节）
     * 可以通过环境变量 TAOLIFE_ENCRYPTION_KEY 覆盖
     */
    private String aesKey;

    /**
     * 是否启用加密
     * 设为false时，加密和解密操作直接返回原文（用于测试）
     */
    private boolean enabled = true;

    @PostConstruct
    public void init() {
        if (enabled && aesKey != null && !aesKey.isEmpty()) {
            AesEncryptionUtil.initKey(aesKey);
            log.info("AES-256-GCM加密模块已初始化");
        } else if (enabled) {
            log.warn("AES加密已启用但未配置密钥，加密操作将失败，请配置 taolife.encryption.aes-key");
        }
    }
}