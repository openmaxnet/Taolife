package com.taolife.common.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdScalarSerializer;

/**
 * Jackson全局配置
 *
 * 统一JSON序列化时的日期时间格式：
 * - LocalDateTime → yyyy-MM-dd HH:mm:ss（如 2026-04-12 19:30:00）
 * - LocalDate → yyyy-MM-dd（如 2026-04-12）
 *
 * 使用 {@link JacksonComponent} 注解自动注册到 JsonMapper，Spring Boot 启动时自动扫描，无需手动配置。
 */
public class JacksonConfig {

    /** LocalDateTime 格式：yyyy-MM-dd HH:mm:ss */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** LocalDate 格式：yyyy-MM-dd */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * LocalDateTime 序列化器
     * Jackson 默认输出 ISO-8601 格式（如 2026-04-12T19:30:00），替换为标准格式
     */
    @JacksonComponent
    public static class CustomLocalDateTimeSerializer extends StdScalarSerializer<LocalDateTime> {

        public CustomLocalDateTimeSerializer() {
            super(LocalDateTime.class);
        }

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
            gen.writeString(value.format(DATETIME_FORMATTER));
        }
    }

    /**
     * LocalDate 序列化器
     * Jackson 默认输出数组格式（如 [2026,4,12]），替换为标准格式
     */
    @JacksonComponent
    public static class CustomLocalDateSerializer extends StdScalarSerializer<LocalDate> {

        public CustomLocalDateSerializer() {
            super(LocalDate.class);
        }

        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
            gen.writeString(value.format(DATE_FORMATTER));
        }
    }
}
