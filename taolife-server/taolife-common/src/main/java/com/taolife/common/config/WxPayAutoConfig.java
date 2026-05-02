package com.taolife.common.config;

import com.taolife.common.properties.WxPayProperties;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 *
 * @author 文二
 * @date 2026-04-18
 */
@Configuration
@EnableConfigurationProperties(WxPayProperties.class)
public class WxPayAutoConfig {

    @Bean
    @ConditionalOnProperty(prefix = "taolife.wx-pay", name = "enabled", havingValue = "true")
    public RSAAutoCertificateConfig rsaAutoCertificateConfig(WxPayProperties wxPayProperties) throws Exception {
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(wxPayProperties.getMchId())
                .privateKeyFromPath(wxPayProperties.getPrivateKeyPath())
                .merchantSerialNumber(wxPayProperties.getMerchantSerialNumber())
                .apiV3Key(wxPayProperties.getApiV3Key())
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "taolife.wx-pay", name = "enabled", havingValue = "true")
    public JsapiService jsapiService(RSAAutoCertificateConfig config) {
        return new JsapiService.Builder().config(config).build();
    }
}
