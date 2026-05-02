package com.taolife.common.utils;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.taolife.common.properties.CosProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.region.Region;

import lombok.extern.slf4j.Slf4j;

/**
 * COS直传签名工具类
 */
@Slf4j
public class CosSignUtil {

    /**
     * 获取COS直传签名
     */
    public static CosSignResult getCosSign(CosProperties cosProperties, String cosKey, String sessionToken) throws Exception {
        log.debug("获取COS直传签名, cosKey: {}", cosKey);

        try {
            // 使用临时凭证创建COS客户端
            COSCredentials cred = new BasicSessionCredentials(
                    cosProperties.getSecretId(),
                    cosProperties.getSecretKey(),
                    sessionToken);
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setRegion(new Region(cosProperties.getRegion()));
            COSClient cosClient = new COSClient(cred, clientConfig);

            // 设置过期时间为30分钟后
            Date expiredTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000L);

            // 生成预签名URL
            Map<String, String> headers = new HashMap<>();
            Map<String, String> params = new HashMap<>();
            params.put("x-cos-security-token", sessionToken);

            URL url = cosClient.generatePresignedUrl(
                    cosProperties.getBucketName(),
                    cosKey,
                    expiredTime,
                    HttpMethodName.PUT,
                    headers,
                    params);

            // 解析签名信息
            String host = "https://" + url.getHost();
            String query = url.toString().split("\\?")[1];
            String sign = query.split("&x-cos-security-token")[0];

            CosSignResult result = new CosSignResult();
            result.setCosHost(host);
            result.setCosKey(cosKey);
            result.setAuthorization(sign);
            result.setSecurityToken(sessionToken);
            result.setExpiredTime(expiredTime);

            log.info("获取COS直传签名成功, cosKey: {}", cosKey);
            return result;

        } catch (Exception e) {
            log.error("获取COS直传签名失败, cosKey: {}, error: {}", cosKey, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    /**
     * COS签名结果类
     */
    public static class CosSignResult {
        /** COS域名 */
        private String cosHost;
        /** COS对象键 */
        private String cosKey;
        /** 签名值 */
        private String authorization;
        /** 安全令牌 */
        private String securityToken;
        /** 过期时间 */
        private Date expiredTime;

        public String getCosHost() {
            return cosHost;
        }

        public void setCosHost(String cosHost) {
            this.cosHost = cosHost;
        }

        public String getCosKey() {
            return cosKey;
        }

        public void setCosKey(String cosKey) {
            this.cosKey = cosKey;
        }

        public String getAuthorization() {
            return authorization;
        }

        public void setAuthorization(String authorization) {
            this.authorization = authorization;
        }

        public String getSecurityToken() {
            return securityToken;
        }

        public void setSecurityToken(String securityToken) {
            this.securityToken = securityToken;
        }

        public Date getExpiredTime() {
            return expiredTime;
        }

        public void setExpiredTime(Date expiredTime) {
            this.expiredTime = expiredTime;
        }
    }
}