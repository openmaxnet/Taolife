package com.taolife.common.config;

import com.taolife.common.properties.ApiEncryptionProperties;
import com.taolife.common.security.ApiEncryptionFilter;
import com.taolife.common.security.RsaKeyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * API 加密加签自动配置
 * 通过 taolife.security.encrypt.enabled=true 开启
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ApiEncryptionProperties.class)
@ConditionalOnProperty(prefix = "taolife.security.encrypt", name = "enabled", havingValue = "true")
public class ApiEncryptionAutoConfig {

    /**
     * 初始化 RSA 密钥管理器
     *
     * @param properties 加密配置属性
     * @return RSA 密钥管理器实例
     */
    @Bean
    public RsaKeyManager rsaKeyManager(ApiEncryptionProperties properties) {
        log.info("初始化RSA密钥管理器");
        return new RsaKeyManager(properties);
    }

    /**
     * 初始化 API 加密过滤器
     * 在请求到达 Controller 之前完成解密和签名验证
     *
     * @param properties       加密配置属性
     * @param rsaKeyManager    RSA 密钥管理器
     * @param stringRedisTemplate Redis 模板（用于 nonce 防重放）
     * @param handlerMapping   请求映射处理器
     * @return API 加密过滤器实例
     */
    @Bean
    public ApiEncryptionFilter apiEncryptionFilter(ApiEncryptionProperties properties,
                                                    RsaKeyManager rsaKeyManager,
                                                    StringRedisTemplate stringRedisTemplate,
                                                    RequestMappingHandlerMapping handlerMapping) {
        log.info("初始化API加密过滤器 - signOnly: {}", properties.isSignOnly());
        return new ApiEncryptionFilter(properties, rsaKeyManager, stringRedisTemplate, handlerMapping);
    }
}
