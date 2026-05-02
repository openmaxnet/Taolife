package com.taolife.common.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * HMAC-SHA256 签名工具
 * 提供签名生成、签名验证和签名内容构建能力
 */
public class HmacSignUtil {

    private static final String ALGORITHM = "HmacSHA256"; // HMAC 算法

    /**
     * 生成 HMAC-SHA256 签名
     *
     * @param secret  签名密钥
     * @param content 待签名内容
     * @return Base64 编码的签名字符串
     */
    public static String sign(String secret, String content) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(keySpec);
            byte[] hash = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC签名失败", e);
        }
    }

    /**
     * 验证 HMAC-SHA256 签名
     *
     * @param secret    签名密钥
     * @param content   原始内容
     * @param signature 待验证的签名
     * @return 签名是否有效
     */
    public static boolean verify(String secret, String content, String signature) {
        String expected = sign(secret, content);
        return expected.equals(signature);
    }

    /**
     * 构建签名内容
     * 格式：timestamp + "\n" + nonce + "\n" + method + "\n" + path + "\n" + body
     *
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param method    HTTP 方法
     * @param path      请求路径
     * @param body      请求体
     * @return 拼接后的签名内容
     */
    public static String buildSignContent(String timestamp, String nonce, String method, String path, String body) {
        return timestamp + "\n" + nonce + "\n" + method + "\n" + path + "\n" + body;
    }
}
