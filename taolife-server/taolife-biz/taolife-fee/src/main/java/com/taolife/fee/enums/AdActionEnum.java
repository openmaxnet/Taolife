package com.taolife.fee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告动作枚举
 *
 * @author 文二
 * @date 2026-04-18
 */
@Getter
@AllArgsConstructor
public enum AdActionEnum {

    SHOW(1, "展示"),
    CLICK(2, "点击"),
    CLOSE(3, "关闭"),
    REWARD(4, "获得奖励");

    private final Integer value;
    private final String name;

    public static AdActionEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (AdActionEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
