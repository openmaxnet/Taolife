package com.taolife.fee.enums;

/**
 * 订单状态枚举
 *
 * @author 文二
 * @date 2026-04-18
 */
public enum OrderStatusEnum {

    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    CANCELLED(2, "已取消"),
    REFUNDED(3, "已退款"),
    CLOSED(4, "已关闭");

    private final Integer code;
    private final String name;

    OrderStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static OrderStatusEnum getByCode(Integer code) {
        for (OrderStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
