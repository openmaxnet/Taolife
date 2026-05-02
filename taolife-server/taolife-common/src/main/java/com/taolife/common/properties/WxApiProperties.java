package com.taolife.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信小程序API配置属性
 * 用于配置微信小程序API接口的相关参数
 */
@ConfigurationProperties(prefix = "taolife.wx-api")
public class WxApiProperties {

    /**
     * 微信接口基地址
     */
    private String apiUrl = "https://api.weixin.qq.com";

    /**
     * code2Session接口地址
     */
    private String code2SessionUrl;

    /**
     * 获取AccessToken接口地址
     */
    private String getAccessTokenUrl;

    /**
     * 获取用户手机号接口地址
     */
    private String getPhoneNumberUrl;

    /**
     * 获取小程序码接口地址
     */
    private String getWxaCodeUrl;

    /**
     * 获取小程序二维码接口地址
     */
    private String qrCodeUrl;

    /**
     * 发送模板消息接口地址
     */
    private String sendTemplateMessageUrl;

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getCode2SessionUrl() {
        return code2SessionUrl;
    }

    public void setCode2SessionUrl(String code2SessionUrl) {
        this.code2SessionUrl = code2SessionUrl;
    }

    public String getGetAccessTokenUrl() {
        return getAccessTokenUrl;
    }

    public void setGetAccessTokenUrl(String getAccessTokenUrl) {
        this.getAccessTokenUrl = getAccessTokenUrl;
    }

    public String getGetPhoneNumberUrl() {
        return getPhoneNumberUrl;
    }

    public void setGetPhoneNumberUrl(String getPhoneNumberUrl) {
        this.getPhoneNumberUrl = getPhoneNumberUrl;
    }

    public String getWxaCodeUrl() {
        return getWxaCodeUrl;
    }

    public void setWxaCodeUrl(String getWxaCodeUrl) {
        this.getWxaCodeUrl = getWxaCodeUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getSendTemplateMessageUrl() {
        return sendTemplateMessageUrl;
    }

    public void setSendTemplateMessageUrl(String sendTemplateMessageUrl) {
        this.sendTemplateMessageUrl = sendTemplateMessageUrl;
    }
}
