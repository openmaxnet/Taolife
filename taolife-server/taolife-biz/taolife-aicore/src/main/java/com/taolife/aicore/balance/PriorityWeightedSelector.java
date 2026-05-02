package com.taolife.aicore.balance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 优先级+权重模型选择器
 * 按priority降序分组，同组内按weight加权随机选择
 */
@Slf4j
@Component
public class PriorityWeightedSelector implements ModelSelector {

    /**
     * 选择最优模型
     * 按优先级降序分组，同组内按权重加权随机选择，高优先级组无可用模型时降级到下一组
     *
     * @param candidates 候选模型列表
     * @return 选中的模型，全部满载时返回null
     */
    @Override
    public ModelCandidate select(List<ModelCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        if (candidates.size() == 1) {
            ModelCandidate c = candidates.get(0);
            return c.isAvailable() ? c : null;
        }

        // 按优先级降序分组
        Map<Integer, List<ModelCandidate>> grouped = candidates.stream()
                .collect(Collectors.groupingBy(
                        ModelCandidate::priority,
                        () -> new TreeMap<>(Comparator.reverseOrder()),
                        Collectors.toList()
                ));

        for (List<ModelCandidate> group : grouped.values()) {
            // 过滤可用模型
            List<ModelCandidate> available = group.stream()
                    .filter(ModelCandidate::isAvailable)
                    .toList();

            if (available.isEmpty()) {
                log.debug("优先级组 {} 无可用模型，降级到下一组", group.get(0).priority());
                continue;
            }

            if (available.size() == 1) {
                return available.get(0);
            }

            // 加权随机选择
            return weightedRandomSelect(available);
        }

        log.warn("所有候选模型均已满载");
        return null;
    }

    /**
     * 加权随机选择
     *
     * @param candidates 候选模型列表
     * @return 选中的模型
     */
    private ModelCandidate weightedRandomSelect(List<ModelCandidate> candidates) {
        int totalWeight = candidates.stream()
                .mapToInt(ModelCandidate::weight)
                .sum();

        if (totalWeight <= 0) {
            return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        }

        int random = ThreadLocalRandom.current().nextInt(totalWeight);
        int cumulative = 0;
        for (ModelCandidate c : candidates) {
            cumulative += c.weight();
            if (random < cumulative) {
                return c;
            }
        }

        return candidates.get(candidates.size() - 1);
    }
}
