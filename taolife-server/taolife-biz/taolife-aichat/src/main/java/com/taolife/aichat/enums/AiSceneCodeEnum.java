package com.taolife.aichat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI场景代码常量
 * 定义AI模块支持的场景类型
 *
 * @author 文二
 * @date 2026-04-14
 */
@Getter
@AllArgsConstructor
public enum AiSceneCodeEnum {

    /**
     * AI智能问答场景
     */
    AI_CHAT("ai_chat", "AI智能问答"),

    /**
     * 禁止分类模板代码
     */
    FORBIDDEN_CATEGORIES("forbidden_categories", "禁止分类模板");

    /**
     * 场景代码
     */
    private final String code;

    /**
     * 场景名称
     */
    private final String name;

    /**
     * 根据code获取枚举
     *
     * @param code 场景代码
     * @return 枚举对象，未找到返回null
     */
    public static AiSceneCodeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (AiSceneCodeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据code获取场景名称
     *
     * @param code 场景代码
     * @return 场景名称，未找到返回原始code
     */
    public static String getNameByCode(String code) {
        AiSceneCodeEnum e = getByCode(code);
        return e != null ? e.getName() : code;
    }
}