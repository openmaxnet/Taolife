package com.taolife.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 验证码配置属性
 */
@ConfigurationProperties(prefix = "taolife.captcha")
public class CaptchaProperties {

    /**
     * 图片验证码配置
     */
    private Image image = new Image();

    /**
     * 短信验证码配置
     */
    private Sms sms = new Sms();

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }

    /**
     * 图片验证码配置类
     */
    public static class Image {
        /**
         * 验证码长度
         */
        private int length = 5;

        /**
         * 验证码过期时间（秒）
         */
        private int expireSeconds = 300;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getExpireSeconds() {
            return expireSeconds;
        }

        public void setExpireSeconds(int expireSeconds) {
            this.expireSeconds = expireSeconds;
        }
    }

    /**
     * 短信验证码配置类
     */
    public static class Sms {
        /**
         * 验证码长度
         */
        private int length = 6;

        /**
         * 验证码过期时间（秒）
         */
        private int expireSeconds = 300;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getExpireSeconds() {
            return expireSeconds;
        }

        public void setExpireSeconds(int expireSeconds) {
            this.expireSeconds = expireSeconds;
        }
    }
}
