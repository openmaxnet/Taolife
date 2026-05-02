package com.taolife.common.security;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import tools.jackson.databind.ObjectMapper;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.exception.ExceptionResult;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT认证过滤器
 * 通过@AuthSkip注解标记的接口跳过认证，其余接口需验证JWT令牌
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtManager jwtManager;
    private final ObjectMapper objectMapper;
    private final AuthSkipHandler authSkipHandler;

    public JwtAuthenticationFilter(JwtManager jwtManager,
            ObjectMapper objectMapper,
            AuthSkipHandler authSkipHandler) {
        this.jwtManager = jwtManager;
        this.objectMapper = objectMapper;
        this.authSkipHandler = authSkipHandler;
    }

    /**
     * JWT认证过滤核心逻辑
     * 检查@AuthSkip注解放行路径，验证JWT令牌有效性，设置用户上下文
     *
     * @param request     HTTP请求
     * @param response    HTTP响应
     * @param filterChain 过滤链
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        log.debug("请求处理开始，请求路径: {}, 请求方法: {}", path, method);

        // 检查Controller方法或类上是否有@AuthSkip注解
        boolean hasAuthSkipAnnotation = authSkipHandler.shouldSkipAuth(request);

        if (hasAuthSkipAnnotation) {
            log.info("路径有@AuthSkip注解，直接放行: {}", path);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    null, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }

        // 检查是否已经有有效认证信息（由其他过滤器设置，且principal不为空）
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null && existingAuth.getPrincipal() != null) {
            log.info("已存在有效认证信息，跳过JwtAuthenticationFilter处理");
            filterChain.doFilter(request, response);
            return;
        }

        // 尝试从JWT token中获取认证信息
        String accessToken = extractAccessTokenFromRequest(request);
        boolean hasToken = StringUtils.hasText(accessToken);
        log.debug("Token状态: {}", hasToken ? "存在" : "不存在");

        if (!hasToken) {
            log.warn("未找到认证信息 - 路径: {}", path);
            writeExceptionResult(response, ExceptionResult.failed(ExceptionCode.NOT_LOGGED_IN));
            return;
        }

        log.info("路径不在跳过认证列表中，需要进行token验证: {}", path);

        // 处理登录请求 - 如果已有有效token则拒绝
        if (authSkipHandler.isLoginEndpoint(request) && hasToken) {
            try {
                if (jwtManager.isLoggedIn(accessToken)) {
                    writeExceptionResult(response, ExceptionResult.failed(
                            ExceptionCode.TOKEN_VALID, "用户已登录，请勿重复登录"));
                    return;
                }
            } catch (Exception e) {
                log.error("验证token失败：{} - {}", e.getClass().getSimpleName(), e.getMessage());
            }
        }

        // 验证访问令牌
        try {
            log.info("开始验证访问令牌");
            if (!jwtManager.validateToken(accessToken)) {
                if (jwtManager.isTokenExpired(accessToken)) {
                    log.warn("访问令牌已过期 - 路径: {}", path);
                    writeExceptionResult(response, ExceptionResult.failed(
                            ExceptionCode.TOKEN_EXPIRED, "登录已过期，请重新登录"));
                } else {
                    log.error("访问令牌无效 - 路径: {}", path);
                    writeExceptionResult(response, ExceptionResult.failed(
                            ExceptionCode.TOKEN_INVALID, "登录已失效，请重新登录"));
                }
                return;
            }
            log.info("访问令牌验证成功");

            UserContext userContext = jwtManager.createUserContextFromToken(accessToken);
            if (userContext == null) {
                log.error("无法从令牌创建用户上下文 - 路径: {}", path);
                writeExceptionResult(response, ExceptionResult.failed(ExceptionCode.TOKEN_INVALID));
                return;
            }
            log.info("成功创建用户上下文");

            UserContext.set(userContext);

            String accountId = UserContext.getAccountId();
            log.info("用户访问信息 - 路径: {}, 用户ID: {}, IP: {}",
                    path, accountId, request.getRemoteAddr());

            Collection<GrantedAuthority> authorities = userContext.getRoles() != null
                    ? userContext.getRoles().stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
                    : List.of();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userContext, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期 - 路径: {}, 过期时间={}, 当前时间={}",
                    path, e.getClaims().getExpiration(), new java.util.Date());
            writeExceptionResult(response, ExceptionResult.failed(
                    ExceptionCode.TOKEN_EXPIRED, "登录已过期，请重新登录"));
        } catch (Exception e) {
            log.error("访问令牌处理失败 - 路径: {}, 错误: {} - {}", path, e.getClass().getSimpleName(), e.getMessage());
            writeExceptionResult(response, ExceptionResult.failed(
                    ExceptionCode.TOKEN_INVALID, "登录已失效，请重新登录"));
        }
    }

    /**
     * 从请求中提取Bearer访问令牌
     *
     * @param request HTTP请求
     * @return 访问令牌，不存在时返回null
     */
    private String extractAccessTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length()).trim();
            // 过滤空值、"null"字符串、以及不含点号的非JWT格式字符串
            if (StringUtils.hasText(token) && token.contains(".")) {
                return token;
            }
        }
        return null;
    }

    /**
     * 将异常结果写入HTTP响应
     *
     * @param response HTTP响应
     * @param result   异常结果对象
     */
    private void writeExceptionResult(HttpServletResponse response, ExceptionResult<?> result) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        int statusCode = HttpServletResponse.SC_OK;
        if (result.getCode() == ExceptionCode.TOKEN_INVALID.getCode() ||
                result.getCode() == ExceptionCode.TOKEN_EXPIRED.getCode()) {
            statusCode = HttpServletResponse.SC_UNAUTHORIZED;
        } else if (result.getCode() == ExceptionCode.TOKEN_VALID.getCode()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
        } else if (result.getCode() == ExceptionCode.ACCESS_DENIED.getCode()) {
            statusCode = HttpServletResponse.SC_FORBIDDEN;
        }

        response.setStatus(statusCode);
        String json = objectMapper.writeValueAsString(result);
        log.info("拦截返回：HTTP状态码: {}, 响应内容: {}", statusCode, json);
        response.getWriter().write(json);
    }
}
