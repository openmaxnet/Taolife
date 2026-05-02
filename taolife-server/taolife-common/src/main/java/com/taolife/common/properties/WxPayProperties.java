package com.taolife.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信支付配置属性
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
@ConfigurationProperties(prefix = "taolife.wx-pay")
public class WxPayProperties {

    /** 是否启用微信支付 */
    private Boolean enabled = false;

    /** 商户号 */
    private String mchId;

    /** 商户APIv3密钥 */
    private String apiV3Key;

    /** 商户私钥路径 */
    private String privateKeyPath;

    /** 商户证书序列号 */
    private String merchantSerialNumber;

    /** 微信支付回调地址 */
    private String notifyUrl;

    /** 小程序AppID */
    private String appId;
}
