package com.taolife.aichat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI提供商类型常量
 * 定义AI模型提供商的类型分类
 *
 * @author 文二
 * @date 2026-04-14
 */
@Getter
@AllArgsConstructor
public enum AiProviderTypeEnum {

    /**
     * 聊天类型提供商
     */
    CHAT("chat", "聊天"),

    /**
     * 向量嵌入类型提供商
     */
    EMBEDDING("embedding", "向量嵌入");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 根据code获取枚举
     *
     * @param code 类型代码
     * @return 枚举对象，未找到返回null
     */
    public static AiProviderTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (AiProviderTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据code获取类型名称
     *
     * @param code 类型代码
     * @return 类型名称，未找到返回原始code
     */
    public static String getNameByCode(String code) {
        AiProviderTypeEnum e = getByCode(code);
        return e != null ? e.getName() : code;
    }
}