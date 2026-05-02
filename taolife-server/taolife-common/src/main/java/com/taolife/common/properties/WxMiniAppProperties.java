package com.taolife.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置属性
 * 用于配置微信小程序的相关参数
 *
 * @author 文二
 * @date 2026-03-16
 */
@Component
@ConfigurationProperties(prefix = "taolife.wx-miniprogram")
public class WxMiniAppProperties {

    /**
     * 是否启用微信小程序登录
     */
    private boolean enabled = true;

    /**
     * 小程序AppID
     */
    private String appId;

    /**
     * 小程序AppSecret
     */
    private String appSecret;

    /**
     * 消息模板ID
     */
    private String templateId;

    /**
     * token过期时间（毫秒），默认7天
     */
    private long tokenExpire = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 是否调试模式
     */
    private boolean debug = false;

    /**
     * 获取AppID
     *
     * @return 小程序AppID
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 设置AppID
     *
     * @param appId 小程序AppID
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 获取AppSecret
     *
     * @return 小程序AppSecret
     */
    public String getAppSecret() {
        return appSecret;
    }

    /**
     * 设置AppSecret
     *
     * @param appSecret 小程序AppSecret
     */
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    /**
     * 获取模板ID
     *
     * @return 消息模板ID
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * 设置模板ID
     *
     * @param templateId 消息模板ID
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * 获取Token过期时间
     *
     * @return Token过期时间（毫秒）
     */
    public long getTokenExpire() {
        return tokenExpire;
    }

    /**
     * 设置Token过期时间
     *
     * @param tokenExpire Token过期时间（毫秒）
     */
    public void setTokenExpire(long tokenExpire) {
        this.tokenExpire = tokenExpire;
    }

    /**
     * 是否启用
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置启用状态
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 是否调试模式
     *
     * @return 是否调试模式
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * 设置调试模式
     *
     * @param debug 是否调试模式
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public String toString() {
        return "WxMiniProgramProperties{" +
                "enabled=" + enabled +
                ", appId='" + appId + '\'' +
                ", tokenExpire=" + tokenExpire +
                ", debug=" + debug +
                '}';
    }
}
