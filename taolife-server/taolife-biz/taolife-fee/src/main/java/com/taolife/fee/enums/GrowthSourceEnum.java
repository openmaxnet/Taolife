package com.taolife.fee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 成长值来源枚举
 *
 * @author 文二
 * @date 2026-04-18
 */
@Getter
@AllArgsConstructor
public enum GrowthSourceEnum {

    DAILY_LOGIN(1, "每日登录"),
    CHECKIN(2, "签到"),
    TASK_COMPLETE(3, "完成任务"),
    SUBSCRIPTION(4, "开通续费"),
    ADMIN_ADJUST(5, "手动调整");

    private final Integer value;
    private final String name;

    public static GrowthSourceEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (GrowthSourceEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
