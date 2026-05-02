package com.taolife.common.security;

import com.taolife.common.properties.ApiEncryptionProperties;
import com.taolife.common.utils.AesGcmApiUtil;
import com.taolife.common.utils.HmacSignUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 响应加密 Advice
 * 当请求经过加密处理时（request attribute 中存在 AES key），
 * 自动将响应 JSON 使用 AES-256-GCM 加密后返回，并附加 HMAC 签名
 * 仅在 taolife.security.encrypt.enabled=true 时生效
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.taolife")
@Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "taolife.security.encrypt", name = "enabled", havingValue = "true")
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ApiEncryptionProperties properties; // 加密配置属性
    private final ObjectMapper objectMapper;           // JSON 序列化工具

    /**
     * 构造方法
     *
     * @param properties   加密配置属性
     * @param objectMapper JSON 序列化工具
     */
    public EncryptResponseBodyAdvice(ApiEncryptionProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * 判断是否支持当前响应的加密处理
     * 仅在加密功能启用且非仅验签模式时生效
     *
     * @param returnType    返回类型
     * @param converterType 消息转换器类型
     * @return 是否支持加密
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return properties.isEnabled() && !properties.isSignOnly();
    }

    /**
     * 在响应体写入前进行加密处理
     * 使用与请求相同的 AES 密钥加密响应，并生成 HMAC 签名
     *
     * @param body                    原始响应体
     * @param returnType              返回类型
     * @param selectedContentType     内容类型
     * @param selectedConverterType   消息转换器类型
     * @param request                 服务端请求
     * @param response                服务端响应
     * @return 加密后的响应体或原始响应体
     */
    @Override
    public Object beforeBodyWrite(@Nullable Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return body;
        }

        HttpServletRequest httpRequest = servletRequest.getServletRequest();
        // 从 request attribute 中获取 AES 密钥（由 ApiEncryptionFilter 设置）
        byte[] aesKey = (byte[]) httpRequest.getAttribute(ApiEncryptionFilter.ATTR_AES_KEY);

        // 无 AES 密钥说明该请求未经过加密处理，直接返回原始响应
        if (aesKey == null) {
            return body;
        }

        try {
            // 序列化响应为 JSON
            String json = objectMapper.writeValueAsString(body);

            // 生成新的 IV 并加密响应体
            byte[] iv = AesGcmApiUtil.generateIv();
            String encryptedData = AesGcmApiUtil.encryptWithIv(
                    json.getBytes(StandardCharsets.UTF_8), aesKey, iv);

            // 构建 HMAC 签名并写入响应头
            String path = httpRequest.getRequestURI();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String signContent = timestamp + "\n" + path + "\n" + encryptedData;
            String sign = HmacSignUtil.sign(properties.getHmacSecret(), signContent);

            response.getHeaders().set("X-Sign", sign);
            response.getHeaders().set("X-Timestamp", timestamp);

            // 构建加密响应体
            Map<String, Object> encryptedResponse = new LinkedHashMap<>();
            encryptedResponse.put("encrypted", true);
            encryptedResponse.put("data", encryptedData);

            log.debug("响应加密成功 - 路径: {}", path);
            return encryptedResponse;

        } catch (Exception e) {
            log.error("响应加密失败", e);
            return body;
        }
    }
}
