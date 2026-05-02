package com.taolife.plan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 健康方案状态枚举
 *
 * @author 文二
 * @date 2026-04-08
 */
@Getter
@AllArgsConstructor
public enum PlanStatusEnum {

    /**
     * 进行中
     */
    ACTIVE(1, "进行中"),

    /**
     * 已完成：周期自然结束且所有任务已完成
     */
    COMPLETED(2, "已完成"),

    /**
     * 已过期：周期自然结束但存在未完成的任务
     */
    EXPIRED(3, "已过期"),

    /**
     * 已终止：用户手动重新生成，放弃当前方案
     */
    TERMINATED(4, "已终止");

    /**
     * 状态值
     */
    private final Integer value;

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 根据值获取枚举
     */
    public static PlanStatusEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (PlanStatusEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
