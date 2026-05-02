package com.taolife.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Nonce 防重放管理器
 * 基于 Redis 的 SET NX 机制存储已使用的 nonce，配合 TTL 自动过期
 * 每个 nonce 只能使用一次，有效防止请求重放攻击
 */
@Slf4j
public class NonceManager {

    private static final String NONCE_PREFIX = "api:nonce:"; // Redis key 前缀
    private final StringRedisTemplate redisTemplate;          // Redis 模板
    private final long expireSeconds;                         // nonce 过期时间（秒）

    /**
     * 构造方法
     *
     * @param redisTemplate Redis 模板
     * @param expireSeconds nonce 过期时间（秒）
     */
    public NonceManager(StringRedisTemplate redisTemplate, long expireSeconds) {
        this.redisTemplate = redisTemplate;
        this.expireSeconds = expireSeconds;
    }

    /**
     * 检查 nonce 是否未被使用，并将其标记为已使用
     * 使用 Redis SET NX 原子操作保证并发安全
     *
     * @param nonce 随机字符串
     * @return true 如果 nonce 有效（未被使用），false 如果 nonce 已存在
     */
    public boolean checkAndStore(String nonce) {
        String key = NONCE_PREFIX + nonce;
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, "1", expireSeconds, TimeUnit.SECONDS);
        if (isNew == null || !isNew) {
            log.warn("Nonce已使用或无效: {}", nonce);
            return false;
        }
        return true;
    }
}
