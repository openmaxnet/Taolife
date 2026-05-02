package com.taolife.plan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 健康计划任务类型枚举
 * 1-饮食任务，2-运动任务，3-穴位任务，4-经络任务，5-生活任务
 *
 * @author 文二
 * @date 2026-04-02
 */
@Getter
@AllArgsConstructor
public enum PlanTaskTypeEnum {

    /**
     * 饮食任务
     */
    DIET(1, "饮食任务", 15),

    /**
     * 运动任务
     */
    EXERCISE(2, "运动任务", 20),

    /**
     * 穴位任务
     */
    ACUPOINT(3, "穴位任务", 15),

    /**
     * 经络任务
     */
    MERIDIAN(4, "经络任务", 15),

    /**
     * 生活任务
     */
    LIFESTYLE(5, "生活任务", 10),

    /**
     * 其他任务
     */
    OTHER(9, "其他任务", 10);

    /**
     * 类型值
     */
    private final Integer value;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 任务积分
     */
    private final Integer points;

    /**
     * 全部完成奖励积分
     */
    public static final int ALL_COMPLETED_BONUS = 30;

    /**
     * 根据值获取枚举
     *
     * @param value 类型值
     * @return 任务类型枚举
     */
    public static PlanTaskTypeEnum getByValue(Integer value) {
        if (value == null) {
            return OTHER;
        }
        for (PlanTaskTypeEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return OTHER;
    }

}
