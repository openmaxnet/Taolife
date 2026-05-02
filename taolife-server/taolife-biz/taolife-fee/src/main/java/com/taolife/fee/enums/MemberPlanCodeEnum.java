package com.taolife.fee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会员套餐代码枚举
 *
 * @author 文二
 * @date 2026-04-18
 */
@Getter
@AllArgsConstructor
public enum MemberPlanCodeEnum {

    MONTHLY(1, "月卡会员", 30),
    YEARLY(2, "年卡会员", 365),
    LIFETIME(3, "终身会员", 0);

    private final Integer value;
    private final String name;
    private final Integer durationDays;

    public static MemberPlanCodeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (MemberPlanCodeEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
