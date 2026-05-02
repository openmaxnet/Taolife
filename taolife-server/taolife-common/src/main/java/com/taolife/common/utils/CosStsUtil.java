package com.taolife.common.utils;

import java.time.LocalDate;
import java.util.Date;
import java.util.TreeMap;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.region.Region;
import com.taolife.common.properties.CosProperties;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;

import lombok.extern.slf4j.Slf4j;

/**
 * COS STS临时凭证工具类
 */
@Slf4j
public class CosStsUtil {

    /**
     * 获取STS临时凭证
     */
    public static StsCredential getStsCredential(CosProperties cosProperties, String cosKey, String userId) throws Exception {
        log.debug("获取STS临时凭证, cosKey: {}, userId: {}", cosKey, userId);

        try {
            TreeMap<String, Object> config = new TreeMap<String, Object>();

            // 云 api 密钥 SecretId
            config.put("secretId", cosProperties.getSecretId());
            // 云 api 密钥 SecretKey
            config.put("secretKey", cosProperties.getSecretKey());

            // 临时密钥有效时长，单位是秒
            config.put("durationSeconds", 1800);

            // 换成你的 bucket
            config.put("bucket", cosProperties.getBucketName());
            // 换成 bucket 所在地区
            config.put("region", cosProperties.getRegion());

            // 设置允许的前缀（目录下所有文件）
            String allowPrefix = cosKey.substring(0, cosKey.lastIndexOf('/') + 1) + "*";
            config.put("allowPrefixes", new String[] { allowPrefix });

            // 密钥的权限列表。简单上传需要以下权限
            String[] allowActions = new String[] {
                    // 简单上传
                    "name/cos:PutObject",
                    "name/cos:PostObject",
                    // 分块上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);

            // 调用STS API获取临时凭证
            Response response = CosStsClient.getCredential(config);

            // 转换为内部凭证对象
            StsCredential credential = new StsCredential();
            credential.setTmpSecretId(response.credentials.tmpSecretId);
            credential.setTmpSecretKey(response.credentials.tmpSecretKey);
            credential.setSessionToken(response.credentials.sessionToken);

            log.info("获取STS临时凭证成功, userId: {}", userId);
            return credential;

        } catch (Exception e) {
            log.error("调用腾讯云STS API失败, userId: {}, error: {}", userId, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    /**
     * 获取STS临时凭证（便捷方法，自动生成cosKey）
     */
    public static StsCredential getCredential(CosProperties cosProperties, String fileName, String userId) {
        log.debug("获取STS临时凭证, fileName: {}, userId: {}", fileName, userId);

        try {
            // 生成COS对象键
            String key = generateCosKey(cosProperties, fileName, userId);

            // 获取STS临时凭证
            return getStsCredential(cosProperties, key, userId);

        } catch (Exception e) {
            log.error("获取STS临时凭证失败, userId: {}, error: {}", userId, e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException("获取STS临时凭证失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成COS对象键
     */
    public static String generateCosKey(CosProperties cosProperties, String fileName, String userId) {
        // 按照用户/日期/文件名的结构生成路径
        String datePath = LocalDate.now().toString().replace("-", "/");
        String fileExtension = getFileExtension(fileName);
        String fileNameWithoutExt = getFileNameWithoutExtension(fileName);

        return String.format("%s/%s/%s/%s_%s.%s",
                cosProperties.getFilePath(), userId, datePath, fileNameWithoutExt,
                System.currentTimeMillis(), fileExtension);
    }

    /**
     * 将COS完整URL转为预签名URL，非COS URL原样返回
     *
     * @param cosProperties COS配置
     * @param url           原始URL
     * @return 预签名URL或原始URL
     */
    public static String signCosUrl(CosProperties cosProperties, String url) {
        if (url == null || url.isEmpty()) return url;
        String domain = cosProperties.getDomain();
        if (!url.startsWith(domain)) return url;
        String cosKey = url.startsWith(domain + "/") ? url.substring(domain.length() + 1) : url.substring(domain.length());
        return getPresignedUrl(cosProperties, cosKey, 3600);
    }

    /**
     * 生成预签名下载URL（用于私有bucket的文件访问）
     *
     * @param cosProperties COS配置
     * @param cosKey        对象键
     * @param expiresSeconds 过期时间（秒）
     * @return 预签名URL字符串
     */
    public static String getPresignedUrl(CosProperties cosProperties, String cosKey, int expiresSeconds) {
        BasicCOSCredentials credentials = new BasicCOSCredentials(cosProperties.getSecretId(), cosProperties.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(cosProperties.getRegion()));
        COSClient cosClient = new COSClient(credentials, clientConfig);

        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    cosProperties.getBucketName(), cosKey, HttpMethodName.GET);
            request.setExpiration(new Date(System.currentTimeMillis() + expiresSeconds * 1000L));
            return cosClient.generatePresignedUrl(request).toString();
        } finally {
            cosClient.shutdown();
        }
    }

    /**
     * 获取文件扩展名
     */
    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * 获取不带扩展名的文件名
     */
    private static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }

    /**
     * STS凭证内部类
     */
    public static class StsCredential {
        /** 临时SecretId */
        private String tmpSecretId;
        /** 临时SecretKey */
        private String tmpSecretKey;
        /** 会话Token */
        private String sessionToken;

        public String getTmpSecretId() {
            return tmpSecretId;
        }

        public void setTmpSecretId(String tmpSecretId) {
            this.tmpSecretId = tmpSecretId;
        }

        public String getTmpSecretKey() {
            return tmpSecretKey;
        }

        public void setTmpSecretKey(String tmpSecretKey) {
            this.tmpSecretKey = tmpSecretKey;
        }

        public String getSessionToken() {
            return sessionToken;
        }

        public void setSessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
        }
    }
}
