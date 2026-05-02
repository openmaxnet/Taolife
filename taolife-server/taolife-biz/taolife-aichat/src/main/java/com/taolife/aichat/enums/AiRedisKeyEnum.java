package com.taolife.aichat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI模块 Redis Key 常量
 * 统一管理AI模块所有Redis缓存的key
 *
 * @author 文二
 * @date 2026-04-14
 */
@Getter
@AllArgsConstructor
public enum AiRedisKeyEnum {

    /**
     * Chat配置缓存key前缀（完整key = prefix + sceneCode）
     */
    CHAT_CONFIG_PREFIX("ai:chat:config:"),

    /**
     * Embedding配置缓存key
     */
    EMBEDDING_CONFIG("ai:embedding:config"),

    /**
     * 提示词模板缓存key前缀（完整key = prefix + templateCode）
     */
    TEMPLATE_PREFIX("ai:template:"),

    /**
     * 场景配置缓存key前缀（完整key = prefix + sceneCode）
     */
    SCENE_PREFIX("ai:scene:"),

    /**
     * 禁止分类列表缓存key
     */
    FORBIDDEN_CATEGORIES("ai:forbidden:categories"),

    /**
     * 流式消息缓存key前缀（完整key = prefix + sessionId:messageId）
     */
    STREAM_CACHE_PREFIX("taolife:stream:"),

    /**
     * 流式思考内容缓存key前缀（完整key = prefix + sessionId:messageId）
     */
    STREAM_THINKING_CACHE_PREFIX("taolife:stream:thinking:");

    /**
     * Redis key值
     */
    private final String code;

    /**
     * 构建带后缀的完整key
     *
     * @param suffix 后缀
     * @return 完整key
     */
    public String buildKey(String suffix) {
        return this.code + suffix;
    }

    /**
     * 根据code值获取枚举
     *
     * @param code Redis key
     * @return 枚举对象，未找到返回null
     */
    public static AiRedisKeyEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (AiRedisKeyEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}