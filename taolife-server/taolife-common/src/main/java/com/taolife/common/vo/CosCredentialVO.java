package com.taolife.common.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * COS临时凭证视图对象
 * 用于前端直传COS时获取STS临时凭证
 *
 * @author 文二
 * @date 2026-04-13
 */
@Data
@NoArgsConstructor
public class CosCredentialVO {

    /**
     * 临时SecretId
     */
    private String tmpSecretId;

    /**
     * 临时SecretKey
     */
    private String tmpSecretKey;

    /**
     * 安全令牌
     */
    private String sessionToken;

    /**
     * COS对象路径
     */
    private String cosKey;

    /**
     * COS访问域名
     */
    private String domain;

    /**
     * Bucket名称
     */
    private String bucketName;

    /**
     * 地域
     */
    private String region;
}