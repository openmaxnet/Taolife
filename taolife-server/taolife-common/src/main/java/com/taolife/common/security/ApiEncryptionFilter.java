package com.taolife.common.security;

import com.taolife.common.annotation.Encrypted;
import com.taolife.common.properties.ApiEncryptionProperties;
import com.taolife.common.utils.AesGcmApiUtil;
import com.taolife.common.utils.HmacSignUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * API 加密加签过滤器
 *
 * 处理流程：
 * 1. 检查是否启用加密 + 端点是否有 @Encrypted 注解
 * 2. 验证时间戳有效性
 * 3. 验证 nonce（防重放攻击）
 * 4. RSA 解密 AES 密钥，AES-GCM 解密请求体
 * 5. 验证 HMAC 签名
 * 6. 将解密后的请求体和 AES 密钥存入 request attribute，供后续使用
 */
@Slf4j
public class ApiEncryptionFilter extends OncePerRequestFilter {

    private static final String HEADER_ENCRYPT_KEY = "X-Encrypt-Key";   // RSA 加密的 AES 密钥头
    private static final String HEADER_TIMESTAMP = "X-Timestamp";       // 请求时间戳头
    private static final String HEADER_NONCE = "X-Nonce";               // 防重放随机字符串头
    private static final String HEADER_SIGN = "X-Sign";                 // HMAC 签名头

    /** Request attribute: 解密后的请求体 */
    public static final String ATTR_DECRYPTED_BODY = "api.encrypted.decryptedBody";
    /** Request attribute: AES 密钥（供响应加密用） */
    public static final String ATTR_AES_KEY = "api.encrypted.aesKey";

    private final ApiEncryptionProperties properties;
    private final RsaKeyManager rsaKeyManager;
    private final NonceManager nonceManager;
    private final RequestMappingHandlerMapping handlerMapping;

    /**
     * 构造方法
     *
     * @param properties      加密配置属性
     * @param rsaKeyManager   RSA 密钥管理器
     * @param redisTemplate   Redis 模板（用于 nonce 防重放）
     * @param handlerMapping  请求映射处理器（用于检查 @Encrypted 注解）
     */
    public ApiEncryptionFilter(ApiEncryptionProperties properties,
                               RsaKeyManager rsaKeyManager,
                               StringRedisTemplate redisTemplate,
                               RequestMappingHandlerMapping handlerMapping) {
        this.properties = properties;
        this.rsaKeyManager = rsaKeyManager;
        this.nonceManager = new NonceManager(redisTemplate, properties.getNonceExpireSeconds());
        this.handlerMapping = handlerMapping;
    }

    /**
     * 过滤器核心逻辑
     * 按顺序执行：注解检查 → 时间戳验证 → nonce 防重放 → 解密 → 签名验证
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 检查是否启用
        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 检查端点是否有 @Encrypted 注解
        if (!isEncryptedEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 1. 读取签名相关头
            String timestamp = request.getHeader(HEADER_TIMESTAMP);
            String nonce = request.getHeader(HEADER_NONCE);
            String sign = request.getHeader(HEADER_SIGN);

            if (timestamp == null || nonce == null || sign == null) {
                writeError(response, 400, "缺少签名参数");
                return;
            }

            // 2. 验证时间戳
            long requestTime = Long.parseLong(timestamp);
            long now = System.currentTimeMillis();
            if (Math.abs(now - requestTime) > properties.getTimestampTolerance() * 1000) {
                writeError(response, 400, "请求时间戳无效");
                return;
            }

            // 3. 验证 nonce（防重放）
            if (!nonceManager.checkAndStore(nonce)) {
                writeError(response, 400, "请求已过期或重复");
                return;
            }

            String method = request.getMethod();
            String body = "";

            // 4. 根据模式处理加密体
            if (properties.isSignOnly()) {
                // 仅验签模式：请求体不加密，直接读取
                body = readBody(request);
            } else {
                // 完整加密模式
                String encryptedKey = request.getHeader(HEADER_ENCRYPT_KEY);

                if (encryptedKey == null) {
                    writeError(response, 400, "缺少加密参数");
                    return;
                }

                // RSA 解密 AES 密钥
                byte[] aesKey = rsaKeyManager.decryptBase64WithPrivateKey(encryptedKey);

                // 读取加密请求体并解密
                String encryptedBody = readBody(request);
                if (encryptedBody != null && !encryptedBody.isEmpty()) {
                    byte[] decryptedBytes = AesGcmApiUtil.decrypt(encryptedBody, aesKey);
                    body = new String(decryptedBytes, StandardCharsets.UTF_8);

                    // 存储解密后的 body 和 AES key 供响应加密使用
                    request.setAttribute(ATTR_DECRYPTED_BODY, body);
                    request.setAttribute(ATTR_AES_KEY, aesKey);
                }

                log.debug("请求解密成功 - 路径: {}, 方法: {}", path, method);
            }

            // 5. 验证 HMAC 签名
            String signContent = HmacSignUtil.buildSignContent(timestamp, nonce, method, path,
                    properties.isSignOnly() ? body : readBody(request));
            if (!HmacSignUtil.verify(properties.getHmacSecret(), signContent, sign)) {
                log.warn("签名验证失败 - 路径: {}, IP: {}", path, request.getRemoteAddr());
                writeError(response, 400, "签名验证失败");
                return;
            }

            // 6. 包装请求，用解密后的 body 替换原始 body
            if (request.getAttribute(ATTR_DECRYPTED_BODY) != null) {
                DecryptedBodyHttpServletRequest wrappedRequest = new DecryptedBodyHttpServletRequest(
                        request, body.getBytes(StandardCharsets.UTF_8));
                filterChain.doFilter(wrappedRequest, response);
            } else {
                filterChain.doFilter(request, response);
            }

        } catch (NumberFormatException e) {
            writeError(response, 400, "时间戳格式无效");
        } catch (Exception e) {
            log.error("请求解密处理失败 - 路径: {}, 错误: {}", path, e.getMessage());
            writeError(response, 400, "请求解密失败");
        }
    }

    /**
     * 检查当前请求的端点是否有 @Encrypted 注解
     * 方法级别注解优先于类级别注解
     *
     * @param request HTTP 请求
     * @return 是否为加密端点
     */
    private boolean isEncryptedEndpoint(HttpServletRequest request) {
        try {
            HandlerExecutionChain chain = handlerMapping.getHandler(request);
            if (chain != null && chain.getHandler() instanceof HandlerMethod handlerMethod) {
                // 方法级别注解优先
                if (handlerMethod.getMethodAnnotation(Encrypted.class) != null) {
                    return true;
                }
                // 类级别注解
                return handlerMethod.getBeanType().getAnnotation(Encrypted.class) != null;
            }
        } catch (Exception e) {
            log.debug("无法获取handler: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 读取请求体内容
     *
     * @param request HTTP 请求
     * @return 请求体字符串，为空时返回空字符串
     */
    private String readBody(HttpServletRequest request) throws IOException {
        byte[] body = request.getInputStream().readAllBytes();
        return body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "";
    }

    /**
     * 写入错误响应
     *
     * @param response HTTP 响应
     * @param status   HTTP 状态码
     * @param message  错误信息
     */
    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write("{\"code\":\"F10005\",\"msg\":\"" + message + "\"}");
    }

    /**
     * 解密请求体包装器
     * 将解密后的内容替换原始请求体，使后续 Filter 和 Controller 能正常读取
     */
    private static class DecryptedBodyHttpServletRequest extends jakarta.servlet.http.HttpServletRequestWrapper {
        private final byte[] body; // 解密后的请求体

        DecryptedBodyHttpServletRequest(HttpServletRequest request, byte[] body) {
            super(request);
            this.body = body;
        }

        @Override
        public jakarta.servlet.ServletInputStream getInputStream() {
            return new jakarta.servlet.ServletInputStream() {
                private final java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(body);

                @Override
                public boolean isFinished() {
                    return bais.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(jakarta.servlet.ReadListener readListener) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int read() {
                    return bais.read();
                }
            };
        }

        @Override
        public int getContentLength() {
            return body.length;
        }

        @Override
        public long getContentLengthLong() {
            return body.length;
        }
    }
}
