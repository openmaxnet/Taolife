package com.taolife.aicore.balance;

/**
 * 并发满载处理策略
 */
public enum ConcurrencyStrategy {

    /**
     * 立即拒绝，返回错误码 AI_CONCURRENCY_FULL
     */
    REJECT,

    /**
     * 排队等待（设超时）
     */
    QUEUE,

    /**
     * 降级到低优先级模型
     */
    FALLBACK
}
