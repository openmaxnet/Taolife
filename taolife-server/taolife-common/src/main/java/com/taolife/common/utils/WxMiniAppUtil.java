package com.taolife.common.utils;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.taolife.common.properties.WxApiProperties;
import com.taolife.common.properties.WxMiniAppProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 微信小程序API工具类
 * 提供微信小程序登录相关的API调用功能
 *
 * @author 文二
 * @date 2026-03-16
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WxMiniAppUtil {

    private final WxMiniAppProperties wxProperties;
    private final WxApiProperties wxApiProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 小程序登录凭证校验
     * 通过code获取openid和session_key
     *
     * @param code 小程序wx.login()返回的code
     * @return 包含openid、session_key、unionid的Map，失败返回null
     */
    public Map<String, String> code2Session(String code) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("appid", wxProperties.getAppId());
        params.put("secret", wxProperties.getAppSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        try {
            // 构建URL
            StringBuilder urlBuilder = new StringBuilder(
                wxApiProperties.getApiUrl() + wxApiProperties.getCode2SessionUrl());
            urlBuilder.append("?");
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!first) {
                    urlBuilder.append("&");
                }
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }

            String requestUrl = urlBuilder.toString();
            log.info("code2Session请求URL：{}", requestUrl);
            String response = restTemplate.getForEntity(requestUrl, String.class).getBody();
            log.info("code2Session响应：{}", response);

            JsonNode jsonNode = objectMapper.readTree(response);
            int errCode = jsonNode.has("errcode") ? jsonNode.get("errcode").asInt() : 0;

            if (errCode != 0) {
                String errMsg = getWxErrMsg(errCode);
                log.error("code2Session失败：errCode={}, errMsg={}", errCode, errMsg);
                return null;
            }

            Map<String, String> result = new LinkedHashMap<>();
            if (jsonNode.has("openid")) {
                result.put("openid", jsonNode.get("openid").asString());
            }
            if (jsonNode.has("session_key")) {
                result.put("session_key", jsonNode.get("session_key").asString());
            }
            if (jsonNode.has("unionid")) {
                result.put("unionid", jsonNode.get("unionid").asString());
            }

            log.info("code2Session成功：openid={}", result.get("openid"));
            return result;
        } catch (Exception e) {
            log.error("code2Session异常：{} - {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 获取接口调用凭证
     * 用于调用微信小程序的业务接口
     *
     * @return access_token，失败返回null
     */
    public String getAccessToken() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("grant_type", "client_credential");
        params.put("appid", wxProperties.getAppId());
        params.put("secret", wxProperties.getAppSecret());

        try {
            StringBuilder urlBuilder = new StringBuilder(
                wxApiProperties.getApiUrl() + wxApiProperties.getGetAccessTokenUrl());
            urlBuilder.append("?");
            params.forEach((key, value) -> urlBuilder.append(key).append("=").append(value).append("&"));

            String response = restTemplate.getForEntity(urlBuilder.toString(), String.class).getBody();
            log.debug("getAccessToken响应：{}", response);

            JsonNode jsonNode = objectMapper.readTree(response);
            int errCode = jsonNode.has("errcode") ? jsonNode.get("errcode").asInt() : 0;

            if (errCode != 0) {
                String errMsg = getWxErrMsg(errCode);
                log.error("getAccessToken失败：errCode={}, errMsg={}", errCode, errMsg);
                return null;
            }

            String accessToken = jsonNode.get("access_token").asString();
            int expiresIn = jsonNode.get("expires_in").asInt();

            log.info("getAccessToken成功：expiresIn={}秒", expiresIn);
            return accessToken;
        } catch (Exception e) {
            log.error("getAccessToken异常：{} - {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 获取用户手机号
     * 用于获取用户绑定的手机号
     *
     * @param accessToken 接口调用凭证
     * @param code 手机号获取凭证
     * @return 手机号，失败返回null
     */
    public String getPhoneNumber(String accessToken, String code) {
        try {
            String url = wxApiProperties.getApiUrl() + wxApiProperties.getGetPhoneNumberUrl() + "?access_token=" + accessToken;

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("code", code);

            ResponseEntity<String> response = restTemplate.postForEntity(url, body, String.class);
            String responseBody = response.getBody();
            log.debug("getPhoneNumber响应：{}", responseBody);

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            int errCode = jsonNode.has("errcode") ? jsonNode.get("errcode").asInt() : -1;

            if (errCode != 0) {
                String errMsg = getWxErrMsg(errCode);
                log.error("getPhoneNumber失败：errCode={}, errMsg={}", errCode, errMsg);
                return null;
            }

            JsonNode phoneInfo = jsonNode.get("phone_info");
            if (phoneInfo == null) {
                log.error("getPhoneNumber失败：phone_info为空");
                return null;
            }

            String phoneNumber = phoneInfo.has("phoneNumber") ? phoneInfo.get("phoneNumber").asString() : null;
            String purePhoneNumber = phoneInfo.has("purePhoneNumber") ? phoneInfo.get("purePhoneNumber").asString() : null;
            String countryCode = phoneInfo.has("countryCode") ? phoneInfo.get("countryCode").asString() : null;

            log.info("getPhoneNumber成功：countryCode={}, purePhoneNumber={}", countryCode, purePhoneNumber);
            return purePhoneNumber != null ? purePhoneNumber : phoneNumber;
        } catch (Exception e) {
            log.error("getPhoneNumber异常：{} - {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 解密用户敏感数据
     * 用于解密用户头像、昵称等敏感信息
     *
     * @param sessionKey    会话密钥
     * @param encryptedData 加密数据
     * @param iv            加密算法的初始向量
     * @return 解密后的JSON字符串，失败返回null
     */
    public String decryptUserData(String sessionKey, String encryptedData, String iv) {
        try {
            String result = decrypt(sessionKey, encryptedData, iv);
            if (result == null) {
                return null;
            }

            log.debug("解密用户数据成功：{}", result);
            return result;
        } catch (Exception e) {
            log.error("解密用户数据异常：{} - {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 解密手机号数据
     * 用于解密通过button组件获取的加密手机号
     *
     * @param sessionKey    会话密钥
     * @param encryptedData 加密数据
     * @param iv            加密算法的初始向量
     * @return 解密后的手机号，失败返回null
     */
    public String decryptPhoneNumber(String sessionKey, String encryptedData, String iv) {
        try {
            String userData = decryptUserData(sessionKey, encryptedData, iv);
            if (userData == null) {
                return null;
            }

            JsonNode jsonNode = objectMapper.readTree(userData);
            String phoneNumber = jsonNode.has("phoneNumber") ? jsonNode.get("phoneNumber").asString() : null;
            String purePhoneNumber = jsonNode.has("purePhoneNumber") ? jsonNode.get("purePhoneNumber").asString() : null;

            log.info("解密手机号成功：purePhoneNumber={}", purePhoneNumber);
            return purePhoneNumber != null ? purePhoneNumber : phoneNumber;
        } catch (Exception e) {
            log.error("解密手机号异常：{} - {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * AES解密
     *
     * @param sessionKey    会话密钥
     * @param encryptedData 加密数据
     * @param iv            加密算法的初始向量
     * @return 解密后的字符串
     */
    private String decrypt(String sessionKey, String encryptedData, String iv) {
        try {
            // 初始化Cipher
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            SecretKeySpec keySpec = new SecretKeySpec(sessionKey.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            // 解密
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));

            // 转换为字符串
            String result = new String(decrypted, StandardCharsets.UTF_8);
            return result;
        } catch (Exception e) {
            log.error("AES解密异常：{} - {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 根据错误码获取错误消息
     */
    private String getWxErrMsg(int errCode) {
        return switch (errCode) {
            case 0 -> "成功";
            case -1 -> "系统繁忙，此时请开发者稍候再试";
            case -2 -> "获取手机号达到上限";
            case 40029 -> "code无效";
            case 40226 -> "code已被使用，请重新获取";
            case 45011 -> "请求频率超限，请稍后再试";
            case 40013 -> "AppID有误";
            case 40125 -> "Secret有误";
            case 80001 -> "小程序绑定关系未绑定";
            case 40001 -> "access_token无效";
            case 40014 -> "access_token已过期";
            case 45009 -> "access_token超出有效期";
            case 50001 -> "登录态失效";
            default -> "未知错误，错误码：" + errCode;
        };
    }

    /**
     * 检查配置是否有效
     *
     * @return 是否有效
     */
    public boolean isValid() {
        return wxProperties.isEnabled()
                && wxProperties.getAppId() != null
                && !wxProperties.getAppId().isEmpty()
                && wxProperties.getAppSecret() != null
                && !wxProperties.getAppSecret().isEmpty();
    }
}
