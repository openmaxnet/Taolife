package com.taolife.aichat.service;

/**
 * 模型并发计数服务
 * 使用Redis原子计数器追踪每个模型的当前并发请求数
 */
public interface IModelConcurrencyService {

    /**
     * 获取模型当前并发数
     */
    int getCurrentConcurrency(String modelId);

    /**
     * 占用一个并发槽位（INCR）
     * @return 占用后的并发数
     */
    long acquire(String modelId);

    /**
     * 释放一个并发槽位（DECR）
     */
    void release(String modelId);
}
