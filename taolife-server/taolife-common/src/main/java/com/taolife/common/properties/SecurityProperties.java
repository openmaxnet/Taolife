package com.taolife.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全配置属性
 */
@ConfigurationProperties(prefix = "taolife.security")
public class SecurityProperties {
    /**
     * 是否启用安全配置（默认开启）
     */
    private boolean enabled = true;

    /**
     * 不需要严格校验token的接口白名单
     */
    private String[] tokenValidationWhitelist = new String[0];

    /**
     * JWT配置
     */
    private Jwt jwt = new Jwt();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String[] getTokenValidationWhitelist() {
        return tokenValidationWhitelist;
    }

    public void setTokenValidationWhitelist(String[] tokenValidationWhitelist) {
        this.tokenValidationWhitelist = tokenValidationWhitelist;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    /**
     * Token配置分组
     */
    private Token token = new Token();

    /**
     * 用户会话配置分组
     */
    private UserSession userSession = new UserSession();

    /**
     * 微信相关配置分组
     */
    private WxConfig wxConfig = new WxConfig();

    /**
     * 验证码配置分组
     */
    private CaptchaConfig captchaConfig = new CaptchaConfig();

    /**
     * 限流配置分组
     */
    private RateLimitConfig rateLimitConfig = new RateLimitConfig();

    /**
     * 分布式锁配置分组
     */
    private LockConfig lockConfig = new LockConfig();

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public WxConfig getWxConfig() {
        return wxConfig;
    }

    public void setWxConfig(WxConfig wxConfig) {
        this.wxConfig = wxConfig;
    }

    public CaptchaConfig getCaptchaConfig() {
        return captchaConfig;
    }

    public void setCaptchaConfig(CaptchaConfig captchaConfig) {
        this.captchaConfig = captchaConfig;
    }

    public RateLimitConfig getRateLimitConfig() {
        return rateLimitConfig;
    }

    public void setRateLimitConfig(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    public LockConfig getLockConfig() {
        return lockConfig;
    }

    public void setLockConfig(LockConfig lockConfig) {
        this.lockConfig = lockConfig;
    }

    /**
     * JWT配置类
     */
    public static class Jwt {
        /**
         * JWT密钥（必须在配置文件中指定）
         */
        private String secretKey;

        /**
         * JWT过期时间，单位：毫秒
         * 默认值：24小时 (24 * 60 * 60 * 1000)
         */
        private long expiration = 24 * 60 * 60 * 1000;

        /**
         * JWT黑名单前缀
         */
        private String jwtBlacklistPrefix = "jwt:blacklist:";

        /**
         * AccessToken前缀
         */
        private String accessTokenPrefix = "taolife:oauth:access_tokens:user:";

        /**
         * RefreshToken前缀
         */
        private String refreshTokenPrefix = "taolife:oauth:refresh_tokens:user:";

        /**
         * Token过期时间（秒）- 7天
         */
        private long tokenExpireSeconds = 7 * 24 * 60 * 60;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }

        public String getJwtBlacklistPrefix() {
            return jwtBlacklistPrefix;
        }

        public void setJwtBlacklistPrefix(String jwtBlacklistPrefix) {
            this.jwtBlacklistPrefix = jwtBlacklistPrefix;
        }

        public String getAccessTokenPrefix() {
            return accessTokenPrefix;
        }

        public void setAccessTokenPrefix(String accessTokenPrefix) {
            this.accessTokenPrefix = accessTokenPrefix;
        }

        public String getRefreshTokenPrefix() {
            return refreshTokenPrefix;
        }

        public void setRefreshTokenPrefix(String refreshTokenPrefix) {
            this.refreshTokenPrefix = refreshTokenPrefix;
        }

        public long getTokenExpireSeconds() {
            return tokenExpireSeconds;
        }

        public void setTokenExpireSeconds(long tokenExpireSeconds) {
            this.tokenExpireSeconds = tokenExpireSeconds;
        }
    }

    /**
     * Token配置分组
     */
    public static class Token {
        /**
         * AccessToken过期时间（秒）- 1小时
         */
        private long accessExpireSeconds = 60 * 60;

        /**
         * RefreshToken过期时间（秒）- 30天
         */
        private long refreshExpireSeconds = 30 * 24 * 60 * 60;

        public long getAccessExpireSeconds() {
            return accessExpireSeconds;
        }

        public void setAccessExpireSeconds(long accessExpireSeconds) {
            this.accessExpireSeconds = accessExpireSeconds;
        }

        public long getRefreshExpireSeconds() {
            return refreshExpireSeconds;
        }

        public void setRefreshExpireSeconds(long refreshExpireSeconds) {
            this.refreshExpireSeconds = refreshExpireSeconds;
        }
    }

    /**
     * 用户会话配置分组
     */
    public static class UserSession {
        /**
         * 用户信息过期时间（秒）- 1小时
         */
        private long infoExpireSeconds = 60 * 60;

        /**
         * 用户会话过期时间（秒）- 7天
         */
        private long sessionExpireSeconds = 7 * 24 * 60 * 60;

        /**
         * 用户积分过期时间（秒）- 1小时
         */
        private long pointsExpireSeconds = 60 * 60;

        public long getInfoExpireSeconds() {
            return infoExpireSeconds;
        }

        public void setInfoExpireSeconds(long infoExpireSeconds) {
            this.infoExpireSeconds = infoExpireSeconds;
        }

        public long getSessionExpireSeconds() {
            return sessionExpireSeconds;
        }

        public void setSessionExpireSeconds(long sessionExpireSeconds) {
            this.sessionExpireSeconds = sessionExpireSeconds;
        }

        public long getPointsExpireSeconds() {
            return pointsExpireSeconds;
        }

        public void setPointsExpireSeconds(long pointsExpireSeconds) {
            this.pointsExpireSeconds = pointsExpireSeconds;
        }
    }

    /**
     * 微信相关配置分组
     */
    public static class WxConfig {
        /**
         * 微信AccessToken过期时间（秒）- 2小时
         */
        private long accessTokenExpireSeconds = 7200;

        /**
         * 微信SessionKey过期时间（秒）- 30分钟
         */
        private long sessionKeyExpireSeconds = 30 * 60;

        public long getAccessTokenExpireSeconds() {
            return accessTokenExpireSeconds;
        }

        public void setAccessTokenExpireSeconds(long accessTokenExpireSeconds) {
            this.accessTokenExpireSeconds = accessTokenExpireSeconds;
        }

        public long getSessionKeyExpireSeconds() {
            return sessionKeyExpireSeconds;
        }

        public void setSessionKeyExpireSeconds(long sessionKeyExpireSeconds) {
            this.sessionKeyExpireSeconds = sessionKeyExpireSeconds;
        }
    }

    /**
     * 验证码配置分组
     */
    public static class CaptchaConfig {
        /**
         * 短信验证码过期时间（秒）- 5分钟
         */
        private long smsExpireSeconds = 5 * 60;

        /**
         * 图形验证码过期时间（秒）- 5分钟
         */
        private long codeExpireSeconds = 5 * 60;

        /**
         * 验证码发送频率限制时间（秒）- 60秒
         */
        private long sendLimitSeconds = 60;

        public long getSmsExpireSeconds() {
            return smsExpireSeconds;
        }

        public void setSmsExpireSeconds(long smsExpireSeconds) {
            this.smsExpireSeconds = smsExpireSeconds;
        }

        public long getCodeExpireSeconds() {
            return codeExpireSeconds;
        }

        public void setCodeExpireSeconds(long codeExpireSeconds) {
            this.codeExpireSeconds = codeExpireSeconds;
        }

        public long getSendLimitSeconds() {
            return sendLimitSeconds;
        }

        public void setSendLimitSeconds(long sendLimitSeconds) {
            this.sendLimitSeconds = sendLimitSeconds;
        }
    }

    /**
     * 限流配置分组
     */
    public static class RateLimitConfig {
        /**
         * 限流时间窗口（秒）
         */
        private long seconds = 60;

        /**
         * IP维度默认限流次数（未登录请求）
         */
        private int ipLimit = 60;

        /**
         * 用户维度默认限流次数（已登录请求）
         */
        private int userLimit = 120;

        /**
         * 认证接口限流次数（/auth/ 路径）
         */
        private int authLimit = 10;

        public long getSeconds() {
            return seconds;
        }

        public void setSeconds(long seconds) {
            this.seconds = seconds;
        }

        public int getIpLimit() {
            return ipLimit;
        }

        public void setIpLimit(int ipLimit) {
            this.ipLimit = ipLimit;
        }

        public int getUserLimit() {
            return userLimit;
        }

        public void setUserLimit(int userLimit) {
            this.userLimit = userLimit;
        }

        public int getAuthLimit() {
            return authLimit;
        }

        public void setAuthLimit(int authLimit) {
            this.authLimit = authLimit;
        }
    }

    /**
     * 分布式锁配置分组
     */
    public static class LockConfig {
        /**
         * 过期时间（秒）- 30秒
         */
        private long expireSeconds = 30;

        public long getExpireSeconds() {
            return expireSeconds;
        }

        public void setExpireSeconds(long expireSeconds) {
            this.expireSeconds = expireSeconds;
        }
    }
}