package com.taolife.common.security;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.config.http.SessionCreationPolicy;

import tools.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.taolife.common.properties.SecurityProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 安全配置类
 * 认证绕过统一通过@AuthSkip注解实现
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
@EnableMethodSecurity(prePostEnabled = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    private final SecurityProperties securityProperties;

    @Autowired(required = false)
    private ApiEncryptionFilter apiEncryptionFilter;

    public SecurityConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * JWT管理器
     *
     * @param stringRedisTemplate Redis模板
     * @return JWT管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtManager jwtManager(StringRedisTemplate stringRedisTemplate) {
        String secretKey = securityProperties.getJwt().getSecretKey();
        long expirationTime = securityProperties.getJwt().getExpiration();

        if (secretKey == null || secretKey.trim().isEmpty()) {
            log.error("JWT密钥未配置，请在application.yml中配置taolife.security.jwt.secret-key");
            throw new IllegalStateException("JWT密钥未配置");
        }

        log.debug("创建JwtManager，密钥长度: {}, 过期时间: {}ms", secretKey.length(), expirationTime);
        return new JwtManager(stringRedisTemplate, securityProperties);
    }

    /**
     * JWT认证过滤器
     *
     * @param jwtManager        JWT管理器
     * @param authSkipHandler   跳过认证处理器
     * @return JWT认证过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtManager jwtManager,
            AuthSkipHandler authSkipHandler) {
        log.debug("创建jwtAuthenticationFilter");
        return new JwtAuthenticationFilter(jwtManager, new ObjectMapper(), authSkipHandler);
    }

    /**
     * 安全过滤链
     * 禁用CSRF、CORS配置、会话管理、JWT认证过滤器注入
     *
     * @param http               HTTP安全配置
     * @param stringRedisTemplate Redis模板
     * @param authSkipHandler    跳过认证处理器
     * @return 安全过滤链
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, StringRedisTemplate stringRedisTemplate,
            AuthSkipHandler authSkipHandler) throws Exception {
        if (!securityProperties.isEnabled()) {
            log.info("安全配置未启用，允许所有请求");
            return http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).build();
        }

        log.debug("配置安全过滤链");

        JwtManager jwtManager = jwtManager(stringRedisTemplate);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .securityContext(context -> context
                        .securityContextRepository(new HttpSessionSecurityContextRepository()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new AsyncAccessDeniedHandler()))
                .addFilterBefore(jwtAuthenticationFilter(jwtManager, authSkipHandler),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiEncryptionFilter != null ? apiEncryptionFilter :
                        (request, response, chain) -> chain.doFilter(request, response),
                        JwtAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter(stringRedisTemplate),
                        JwtAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated());

        return http.build();
    }

    /**
     * HTTP会话事件发布器
     *
     * @return 会话事件发布器
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * JWT解码器（HMAC-SHA256）
     *
     * @return JWT解码器
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder jwtDecoder() {
        String secretKey = securityProperties.getJwt().getSecretKey();

        if (secretKey == null || secretKey.trim().isEmpty()) {
            log.error("JWT密钥未配置，请在application.yml中配置taolife.security.jwt.secret-key");
            throw new IllegalStateException("JWT密钥未配置");
        }

        log.debug("配置JWT解码器，密钥长度: {}", secretKey.length());

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).build();

        log.info("JWT解码器配置完成");
        return decoder;
    }

    /**
     * JWT编码器（HMAC-SHA256）
     *
     * @return JWT编码器
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtEncoder jwtEncoder() {
        String secretKey = securityProperties.getJwt().getSecretKey();

        if (secretKey == null || secretKey.trim().isEmpty()) {
            log.error("JWT密钥未配置，请在application.yml中配置taolife.security.jwt.secret-key");
            throw new IllegalStateException("JWT密钥未配置");
        }

        log.debug("配置JWT编码器，密钥长度: {}", secretKey.length());

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(secretKeySpec);
        NimbusJwtEncoder encoder = new NimbusJwtEncoder(jwkSource);

        log.info("JWT编码器配置完成");
        return encoder;
    }

    /**
     * 密码编码器（BCrypt算法）
     *
     * @return 密码编码器
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        log.debug("配置密码编码器，使用BCrypt算法");
        return new BCryptPasswordEncoder();
    }

    /**
     * 限流过滤器
     *
     * @param stringRedisTemplate Redis模板
     * @return 限流过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimitFilter rateLimitFilter(StringRedisTemplate stringRedisTemplate) {
        log.debug("创建RateLimitFilter，IP限流: {}, 用户限流: {}, 认证限流: {}",
                securityProperties.getRateLimitConfig().getIpLimit(),
                securityProperties.getRateLimitConfig().getUserLimit(),
                securityProperties.getRateLimitConfig().getAuthLimit());
        return new RateLimitFilter(stringRedisTemplate, securityProperties.getRateLimitConfig());
    }
}
