package com.taolife.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * API 加密加签配置属性
 * 配置前缀：taolife.security.encrypt
 */
@ConfigurationProperties(prefix = "taolife.security.encrypt")
public class ApiEncryptionProperties {

    /** 是否启用 API 加密（默认关闭，开发环境可保持关闭） */
    private boolean enabled = false;

    /** 是否仅验签不加密（开启后请求体不加密，但仍需签名，方便调试） */
    private boolean signOnly = false;

    /** HMAC-SHA256 签名密钥（前后端必须一致） */
    private String hmacSecret;

    /** RSA 密钥大小（位），默认 2048 */
    private int rsaKeySize = 2048;

    /** Nonce 防重放过期时间（秒），默认 300 秒 */
    private long nonceExpireSeconds = 300;

    /** 时间戳容忍偏差（秒），默认 300 秒 */
    private long timestampTolerance = 300;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSignOnly() {
        return signOnly;
    }

    public void setSignOnly(boolean signOnly) {
        this.signOnly = signOnly;
    }

    public String getHmacSecret() {
        return hmacSecret;
    }

    public void setHmacSecret(String hmacSecret) {
        this.hmacSecret = hmacSecret;
    }

    public int getRsaKeySize() {
        return rsaKeySize;
    }

    public void setRsaKeySize(int rsaKeySize) {
        this.rsaKeySize = rsaKeySize;
    }

    public long getNonceExpireSeconds() {
        return nonceExpireSeconds;
    }

    public void setNonceExpireSeconds(long nonceExpireSeconds) {
        this.nonceExpireSeconds = nonceExpireSeconds;
    }

    public long getTimestampTolerance() {
        return timestampTolerance;
    }

    public void setTimestampTolerance(long timestampTolerance) {
        this.timestampTolerance = timestampTolerance;
    }
}
