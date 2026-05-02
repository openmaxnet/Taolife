package com.taolife.aichat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色枚举
 * 与智谱API保持一致的角色定义
 *
 * @author 文二
 * @date 2026-04-01
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    /**
     * 用户
     */
    USER(1, "user"),

    /**
     * 助手
     */
    ASSISTANT(2, "assistant"),

    /**
     * 系统
     */
    SYSTEM(3, "system"),

    /**
     * 工具
     */
    TOOL(4, "tool");

    /**
     * 角色值
     */
    private final Integer value;

    /**
     * 角色代码
     */
    private final String code;

    /**
     * 根据值获取枚举
     *
     * @param value 角色值
     * @return 枚举对象
     */
    public static RoleEnum getByValue(Integer value) {
        if (value == null) {
            return ASSISTANT;
        }
        for (RoleEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return ASSISTANT;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 角色代码
     * @return 枚举对象
     */
    public static RoleEnum getByCode(String code) {
        if (code == null) {
            return ASSISTANT;
        }
        for (RoleEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return ASSISTANT;
    }
}
