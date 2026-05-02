package com.taolife.aichat.service.impl;

import com.taolife.aichat.service.IModelConcurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 模型并发控制服务实现
 * 基于 Redis 计数器实现模型级别的并发限制，含 TTL 自动释放与异常修复
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelConcurrencyServiceImpl implements IModelConcurrencyService {

    private static final String KEY_PREFIX = "ai:model:concurrency:";
    private static final long TTL_MINUTES = 5;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 获取模型当前并发数
     * 从Redis读取指定模型的当前并发计数
     *
     * @param modelId 模型ID
     * @return 当前并发数
     */
    @Override
    public int getCurrentConcurrency(String modelId) {
        String key = KEY_PREFIX + modelId;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 占用模型并发槽位
     * 原子递增Redis计数器并刷新TTL，防止槽位泄漏
     *
     * @param modelId 模型ID
     * @return 递增后的并发数
     */
    @Override
    public long acquire(String modelId) {
        String key = KEY_PREFIX + modelId;
        Long count = stringRedisTemplate.opsForValue().increment(key);
        // 设置/刷新TTL，防止泄漏
        stringRedisTemplate.expire(key, TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("模型 {} 占用并发槽位，当前并发: {}", modelId, count);
        return count != null ? count : 1;
    }

    /**
     * 释放模型并发槽位
     * 原子递减Redis计数器，若计数为负则自动修复为0
     *
     * @param modelId 模型ID
     */
    @Override
    public void release(String modelId) {
        String key = KEY_PREFIX + modelId;
        Long count = stringRedisTemplate.opsForValue().decrement(key);
        if (count != null && count < 0) {
            // 修复异常情况：并发计数不应为负
            stringRedisTemplate.opsForValue().set(key, "0", TTL_MINUTES, TimeUnit.MINUTES);
            log.warn("模型 {} 并发计数异常为负，已修复为0", modelId);
        }
        log.debug("模型 {} 释放并发槽位，当前并发: {}", modelId, count);
    }
}
