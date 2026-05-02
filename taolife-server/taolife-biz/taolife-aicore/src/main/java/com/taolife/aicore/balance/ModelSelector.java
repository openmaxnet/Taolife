package com.taolife.aicore.balance;


import java.util.List;

/**
 * 模型选择器接口
 * 从多个候选模型中选择一个可用的模型（负载均衡）
 */
public interface ModelSelector {

    /**
     * 从候选模型中选择一个可用的
     *
     * @param candidates 已按优先级排序的候选列表
     * @return 选中的模型配置，null表示全部满载
     */
    ModelCandidate select(List<ModelCandidate> candidates);
}
