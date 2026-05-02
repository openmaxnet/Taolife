package com.taolife.fee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户等级枚举
 * 等级1：养生新手 0-999积分
 * 等级2：养生学徒 1000-2999积分
 * 等级3：养生达人 3000-6999积分
 * 等级4：养生专家 7000-14999积分
 * 等级5：养生大师 15000-29999积分
 * 等级6：养生宗师 30000+积分
 *
 * @author 文二
 * @date 2026-04-02
 */
@Getter
@AllArgsConstructor
public enum UserLevelEnum {

    /**
     * 养生新手
     */
    BEGINNER(1, "养生新手", 0, 999),

    /**
     * 养生学徒
     */
    APPRENTICE(2, "养生学徒", 1000, 2999),

    /**
     * 养生达人
     */
    EXPERT(3, "养生达人", 3000, 6999),

    /**
     * 养生专家
     */
    MASTER(4, "养生专家", 7000, 14999),

    /**
     * 养生大师
     */
    GRANDMASTER(5, "养生大师", 15000, 29999),

    /**
     * 养生宗师
     */
    SUPREME(6, "养生宗师", 30000, Integer.MAX_VALUE);

    /**
     * 等级值
     */
    private final Integer level;

    /**
     * 等级名称
     */
    private final String name;

    /**
     * 积分下限
     */
    private final Integer minPoints;

    /**
     * 积分上限
     */
    private final Integer maxPoints;

    /**
     * 根据总积分获取用户等级
     *
     * @param totalPoints 总积分
     * @return 用户等级枚举
     */
    public static UserLevelEnum getByTotalPoints(Integer totalPoints) {
        if (totalPoints == null) {
            return BEGINNER;
        }
        for (UserLevelEnum level : values()) {
            if (totalPoints >= level.getMinPoints() && totalPoints <= level.getMaxPoints()) {
                return level;
            }
        }
        return BEGINNER;
    }

    /**
     * 根据等级值获取枚举
     *
     * @param level 等级值
     * @return 用户等级枚举
     */
    public static UserLevelEnum getByLevel(Integer level) {
        if (level == null) {
            return BEGINNER;
        }
        for (UserLevelEnum e : values()) {
            if (e.getLevel().equals(level)) {
                return e;
            }
        }
        return BEGINNER;
    }
}
