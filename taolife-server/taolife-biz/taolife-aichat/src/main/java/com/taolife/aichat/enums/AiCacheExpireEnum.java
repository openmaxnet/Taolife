package com.taolife.aichat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI模块缓存过期时间常量
 * 统一管理AI模块各种缓存的过期时间配置
 *
 * @author 文二
 * @date 2026-04-14
 */
@Getter
@AllArgsConstructor
public enum AiCacheExpireEnum {

    /**
     * 默认缓存过期时间（小时），用于AI配置类缓存
     */
    DEFAULT_CACHE_HOURS(1, "默认缓存过期时间（小时）"),

    /**
     * 流式消息缓存过期时间（秒）
     */
    STREAM_CACHE_SECONDS(600, "流式消息缓存过期时间（秒）");

    /**
     * 过期时间数值
     */
    private final Integer value;

    /**
     * 描述
     */
    private final String name;
}