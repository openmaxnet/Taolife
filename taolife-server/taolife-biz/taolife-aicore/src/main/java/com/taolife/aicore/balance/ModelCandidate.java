package com.taolife.aicore.balance;

import com.taolife.aicore.config.ChatConfigVO;

/**
 * 候选模型（用于负载均衡选择）
 */
public record ModelCandidate(
        String modelId,
        ChatConfigVO config,
        int priority,
        int weight,
        int maxConcurrency,
        int currentConcurrency
) {
    /**
     * 是否可用（并发未满）
     * maxConcurrency == 0 表示不限并发
     */
    public boolean isAvailable() {
        return maxConcurrency == 0 || currentConcurrency < maxConcurrency;
    }
}
