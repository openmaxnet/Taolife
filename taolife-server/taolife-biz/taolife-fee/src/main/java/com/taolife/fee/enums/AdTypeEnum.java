package com.taolife.fee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告类型枚举
 *
 * @author 文二
 * @date 2026-04-18
 */
@Getter
@AllArgsConstructor
public enum AdTypeEnum {

    BANNER(1, "Banner广告"),
    INTERSTITIAL(2, "插屏广告"),
    REWARDED_VIDEO(3, "激励视频广告"),
    SPLASH(4, "开屏广告");

    private final Integer value;
    private final String name;

    public static AdTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (AdTypeEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
