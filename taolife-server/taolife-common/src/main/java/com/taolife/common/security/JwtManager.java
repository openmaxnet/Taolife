package com.taolife.common.security;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.Date;

import com.taolife.common.properties.SecurityProperties;

import java.text.SimpleDateFormat;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT管理器
 * 统一处理JWT token的生成、验证和转换（支持双Token机制）
 */
@Slf4j
public class JwtManager {
    private String JWT_BLACKLIST_PREFIX;
    private String ACCESS_TOKEN_PREFIX;
    private String REFRESH_TOKEN_PREFIX;

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
    private final StringRedisTemplate redisTemplate;
    private final String secretKey;
    private final long accessExpireTime;   // AccessToken过期时间（毫秒）
    private final long refreshExpireTime;  // RefreshToken过期时间（毫秒）

    /**
     * 构造函数
     * 通过 SecurityProperties 读取配置中的过期时间
     *
     * @param redisTemplate      Redis模板
     * @param securityProperties 安全配置属性
     */
    public JwtManager(StringRedisTemplate redisTemplate, SecurityProperties securityProperties) {
        this.redisTemplate = redisTemplate;
        this.secretKey = securityProperties.getJwt().getSecretKey();
        this.JWT_BLACKLIST_PREFIX = securityProperties.getJwt().getJwtBlacklistPrefix();
        this.ACCESS_TOKEN_PREFIX = securityProperties.getJwt().getAccessTokenPrefix();
        this.REFRESH_TOKEN_PREFIX = securityProperties.getJwt().getRefreshTokenPrefix();
        // 从 Token 配置中读取双 Token 过期时间（秒转毫秒）
        this.accessExpireTime = securityProperties.getToken().getAccessExpireSeconds() * 1000;
        this.refreshExpireTime = securityProperties.getToken().getRefreshExpireSeconds() * 1000;
    }

    /**
     * 生成访问令牌
     *
     * @param userId 用户ID
     * @param roles  用户角色列表
     * @return JWT令牌
     */
    public String generateAccessToken(String userId, List<String> roles) {
        return generateToken(userId, roles, accessExpireTime, ACCESS_TOKEN_PREFIX);
    }

    /**
     * 生成刷新令牌
     *
     * @param userId 用户ID
     * @param roles  用户角色列表
     * @return JWT刷新令牌
     */
    public String generateRefreshToken(String userId, List<String> roles) {
        return generateToken(userId, roles, refreshExpireTime, REFRESH_TOKEN_PREFIX);
    }

    private String generateToken(String userId, List<String> roles, long expireMs, String redisPrefix) {
        byte[] keyBytes = secretKey.getBytes();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireMs);

        String token = Jwts.builder()
                .subject(userId)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(keyBytes))
                .compact();

        // 存储token到Redis
        String redisKey = redisPrefix + userId;
        redisTemplate.opsForValue().set(redisKey, token, expireMs, TimeUnit.MILLISECONDS);

        log.debug("生成令牌: userId={}, redisPrefix={}, tokenId={}, expireMs={}ms",
                userId, redisPrefix, extractTokenIdFromJjwt(token), expireMs);
        return token;
    }

    /**
     * 验证访问令牌
     */
    public boolean validateToken(String token) {
        return validateTokenWithPrefix(token, ACCESS_TOKEN_PREFIX);
    }

    /**
     * 验证刷新令牌
     */
    public boolean validateRefreshToken(String token) {
        return validateTokenWithPrefix(token, REFRESH_TOKEN_PREFIX);
    }

    private boolean validateTokenWithPrefix(String token, String redisPrefix) {
        try {
            String tokenId = extractTokenIdFromJjwt(token);
            if (isBlacklisted(tokenId)) {
                log.warn("令牌已在黑名单中 - TokenID: {}", tokenId);
                return false;
            }

            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (claims.getExpiration().before(new Date())) {
                log.warn("令牌已过期 - TokenID: {}", tokenId);
                return false;
            }

            String userId = claims.getSubject();
            if (!validateTokenInRedis(userId, token, redisPrefix)) {
                return false;
            }

            log.debug("JWT令牌验证成功 - TokenID: {}", tokenId);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期：过期时间={}, 当前时间={}",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(e.getClaims().getExpiration()),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
            return false;
        } catch (Exception e) {
            log.error("JWT令牌验证失败", e);
            return false;
        }
    }

    /**
     * 检查令牌是否已过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中提取用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期，无法提取用户ID：过期时间={}, 当前时间={}",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(e.getClaims().getExpiration()),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
            return null;
        } catch (Exception e) {
            log.error("从令牌中提取用户ID失败", e);
            return null;
        }
    }

    /**
     * 从令牌中提取角色信息
     */
    public List<String> getRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof List) {
                List<?> rawRoles = (List<?>) rolesObj;
                return rawRoles.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .collect(Collectors.toList());
            }
            return List.of();
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期，无法提取角色信息");
            return List.of();
        } catch (Exception e) {
            log.error("从令牌中提取角色信息失败", e);
            return List.of();
        }
    }

    /**
     * 将Spring Security JWT对象转换为认证令牌
     */
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        if (jwt == null) {
            log.error("JWT对象为null，无法进行认证转换");
            throw new BadCredentialsException("JWT对象为null");
        }

        String tokenValue = jwt.getTokenValue();

        try {
            String tokenId = extractTokenIdFromJjwt(tokenValue);

            if (isBlacklisted(tokenId)) {
                log.warn("令牌已在黑名单中 - TokenID: {}", tokenId);
                throw new BadCredentialsException("Token已失效，请重新登录");
            }

            String subject = getUserIdFromToken(tokenValue);
            if (!validateTokenInRedis(subject, tokenValue, ACCESS_TOKEN_PREFIX)) {
                throw new BadCredentialsException("Token已失效，请重新登录");
            }

            List<String> roles = getRolesFromToken(tokenValue);

            Collection<GrantedAuthority> authorities;
            if (roles != null && !roles.isEmpty()) {
                authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            } else {
                authorities = List.of();
            }

            Collection<GrantedAuthority> combinedAuthorities = defaultConverter.convert(jwt);
            if (combinedAuthorities != null) {
                authorities.addAll(combinedAuthorities);
            }

            if (subject == null) {
                log.error("JWT中的subject为null - TokenID: {}", tokenId);
                return null;
            }

            return new JwtAuthenticationToken(jwt, authorities);
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("JWT令牌验证失败 - 错误: {}", e.getMessage());
            throw new BadCredentialsException("JWT令牌验证失败: " + e.getMessage());
        }
    }

    /**
     * 将token加入黑名单
     */
    public void addToBlacklist(String tokenId, long expireIn) {
        String key = JWT_BLACKLIST_PREFIX + tokenId;
        redisTemplate.opsForValue().set(key, "1", expireIn, TimeUnit.SECONDS);
        log.info("将令牌加入黑名单 - TokenID: {}, 过期时间: {}秒", tokenId, expireIn);
    }

    /**
     * 检查token是否在黑名单中
     */
    public boolean isBlacklisted(String tokenId) {
        boolean isBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(JWT_BLACKLIST_PREFIX + tokenId));
        log.debug("检查令牌是否在黑名单 - TokenID: {}, 结果: {}", tokenId, isBlacklisted);
        return isBlacklisted;
    }

    /**
     * 从令牌中提取令牌ID
     */
    public String extractTokenIdFromJjwt(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getId();
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期，无法提取令牌ID");
            return "";
        } catch (Exception e) {
            log.error("从令牌中提取令牌ID失败", e);
            return "";
        }
    }

    /**
     * 从令牌中创建 UserContext 对象
     */
    public UserContext createUserContextFromToken(String token) {
        try {
            if (!validateToken(token)) {
                log.warn("令牌验证失败，无法创建UserContext对象");
                return null;
            }

            String userId = getUserIdFromToken(token);
            List<String> roles = getRolesFromToken(token);
            UserContext userContext = new UserContext();
            userContext.setAccountId(userId);
            userContext.setRoles(roles);

            log.debug("从令牌创建UserContext对象成功 - 用户ID: {}, 角色: {}", userId, roles);
            return userContext;
        } catch (Exception e) {
            log.error("从令牌创建UserContext对象失败 - 错误: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 检查登录状态
     */
    public boolean isLoggedIn(String accessToken) {
        try {
            return validateToken(accessToken);
        } catch (Exception e) {
            log.error("检查登录状态失败", e);
            return false;
        }
    }

    /**
     * 删除用户的所有token（access + refresh）
     *
     * @param userId 用户ID
     */
    public void removeToken(String userId) {
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + userId);
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
        log.debug("删除用户所有token: userId={}", userId);
    }

    /**
     * 仅删除刷新令牌
     *
     * @param userId 用户ID
     */
    public void removeRefreshToken(String userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
        log.debug("删除用户刷新令牌: userId={}", userId);
    }

    /**
     * 检查Redis白名单（token是否与存储的匹配）
     */
    public boolean validateTokenInRedis(String userId, String token) {
        return validateTokenInRedis(userId, token, ACCESS_TOKEN_PREFIX);
    }

    private boolean validateTokenInRedis(String userId, String token, String redisPrefix) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }

        String redisKey = redisPrefix + userId;
        String storedToken = redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null || storedToken.isEmpty()) {
            log.warn("Redis中不存在token - UserID: {}, prefix: {}", userId, redisPrefix);
            return false;
        }

        if (!token.equals(storedToken)) {
            log.warn("Token与Redis中存储的token不匹配 - UserID: {}", userId);
            return false;
        }

        log.debug("Redis白名单验证通过 - UserID: {}", userId);
        return true;
    }
}
