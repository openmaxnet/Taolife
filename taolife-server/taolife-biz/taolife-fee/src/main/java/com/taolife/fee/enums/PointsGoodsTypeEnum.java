package com.taolife.fee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 积分商品类型枚举
 *
 * @author 文二
 * @date 2026-04-18
 */
@Getter
@AllArgsConstructor
public enum PointsGoodsTypeEnum {

    AI_QUOTA(1, "AI问答次数"),
    ASSESSMENT(2, "体质评估次数"),
    ARTICLE_UNLOCK(3, "文章解锁"),
    COUPON(4, "商城优惠券");

    private final Integer value;
    private final String name;

    public static PointsGoodsTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (PointsGoodsTypeEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
