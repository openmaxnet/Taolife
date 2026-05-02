package com.taolife.common.utils;

import java.io.IOException;
import java.time.Duration;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.properties.CaptchaProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 验证码工具类
 * 提供验证码的生成、校验等功能
 */
@Slf4j
@ConditionalOnClass(jakarta.servlet.http.HttpServletRequest.class)
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaUtil {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CaptchaProperties captchaProperties;

    // Redis key前缀
    private static final String CAPTCHA_COUNT_PREFIX = "captcha_count:";
    private static final String IMAGE_CAPTCHA_PREFIX = "image_captcha:";
    private static final String SMS_CAPTCHA_PREFIX = "sms_captcha:";
    private static final int MAX_CAPTCHA_PER_MINUTE = 5;

    // 请求参数名称
    private static final String CLIENT_ID_PARAM = "clientId";
    private static final String IMAGE_CAPTCHA_CODE_PARAM = "imageCaptchaCode"; // 图形验证码参数
    private static final String SMS_CAPTCHA_CODE_PARAM = "smsCaptchaCode"; // 短信验证码参数
    private static final String CAPTCHA_KEY_PARAM = "captchaKey"; // 验证码唯一标识参数

    /**
     * 生成图片验证码
     * @param clientId   客户端ID
     * @param captchaKey 验证码唯一标识
     * @return 验证码生成结果响应
     */
    public ExceptionResult<String> createImageCaptcha(String clientId, String captchaKey) {
        log.info("开始生成图片验证码，clientId: {}, captchaKey: {}", clientId, captchaKey);
        try {
            // 检查1分钟内验证码生成次数
            String countKey = CAPTCHA_COUNT_PREFIX + clientId;
            String count = redisTemplate.opsForValue().get(countKey);
            if (count != null) {
                int currentCount = Integer.parseInt(count);
                if (currentCount >= MAX_CAPTCHA_PER_MINUTE) {
                    log.warn("验证码生成频率过高，clientId: {}, 当前次数: {}", clientId, count);
                    return ExceptionResult.error("验证码刷新太频繁，请稍后再试");
                }
            }

            // 生成验证码，排除容易混淆的字符：0,O,1,I,l
            String captchaCode = RandomStringUtils.secure().next(captchaProperties.getImage().getLength(), "23456789ABCDEFGHJKLMNPQRSTUVWXYZ");

            // 存储验证码，使用clientId和captchaKey组合
            redisTemplate.opsForValue().set(
                    IMAGE_CAPTCHA_PREFIX + clientId + ":" + captchaKey,
                    captchaCode,
                    Duration.ofSeconds(captchaProperties.getImage().getExpireSeconds()));

            // 增加计数，设置1分钟过期
            redisTemplate.opsForValue().increment(countKey);
            redisTemplate.expire(countKey, Duration.ofMinutes(1));

            log.info("图片验证码生成成功，clientId: {}, captchaKey: {}, captchaCode: {}", clientId, captchaKey, captchaCode);
            return ExceptionResult.success(captchaCode);
        } catch (Exception e) {
            log.error("图片验证码生成失败，clientId: {}, 错误: {}", clientId, e.getClass().getSimpleName(), e.getMessage());
            return ExceptionResult.error("验证码生成失败");
        }
    }

    /**
     * 生成短信验证码
     * @param phone 手机号
     * @return 验证码生成结果响应
     */
    public ExceptionResult<String> generateSmsCaptcha(String phone) {
        log.info("开始生成短信验证码，phone: {}", phone);
        try {
            // 检查1分钟内短信验证码生成次数
            String countKey = CAPTCHA_COUNT_PREFIX + phone;
            String count = redisTemplate.opsForValue().get(countKey);
            if (count != null && Integer.parseInt(count) >= MAX_CAPTCHA_PER_MINUTE) {
                log.warn("短信验证码发送频率过高，phone: {}, 当前次数: {}", phone, count);
                return ExceptionResult.error("短信验证码发送太频繁，请稍后再试");
            }

            // 生成验证码
            String captchaCode = RandomStringUtils.secure().nextNumeric(captchaProperties.getSms().getLength());

            // 存储验证码
            redisTemplate.opsForValue().set(
                    SMS_CAPTCHA_PREFIX + phone,
                    captchaCode,
                    Duration.ofSeconds(captchaProperties.getSms().getExpireSeconds()));

            // 增加计数，设置1分钟过期
            redisTemplate.opsForValue().increment(countKey);
            redisTemplate.expire(countKey, Duration.ofMinutes(1));

            log.info("短信验证码生成成功，phone: {}, captchaCode: {}", phone, captchaCode);
            return ExceptionResult.success(captchaCode);
        } catch (Exception e) {
            log.error("短信验证码发送失败，phone: {}, 错误: {}", phone, e.getClass().getSimpleName(), e.getMessage());
            return ExceptionResult.error("短信验证码发送失败");
        }
    }

    /**
     * 验证图片验证码
     * @param clientId    客户端ID
     * @param captchaKey 验证码key
     * @param captchaCode 验证码
     * @return 验证结果响应
     */
    public ExceptionResult<Boolean> validateCaptchaResponse(String clientId, String captchaKey, String captchaCode) {
        try {
            String key = IMAGE_CAPTCHA_PREFIX + clientId + ":" + captchaKey;
            log.debug("验证码Redis Key: {}", key);
            String storedCaptchaCode = redisTemplate.opsForValue().get(key);
            log.debug("Redis存储验证码: {}, 用户输入验证码: {}", storedCaptchaCode, captchaCode);
            if (captchaCode == null || storedCaptchaCode == null) {
                log.warn("验证码不匹配或为空");
                return ExceptionResult.error("验证码不匹配或为空");
            }
            // 不区分大小写比较
            if (!captchaCode.equalsIgnoreCase(storedCaptchaCode)) {
                log.warn("验证码不匹配");
                return ExceptionResult.error("验证码不匹配");
            }

            // 验证成功后立即删除验证码
            redisTemplate.delete(key);
            log.info("验证码验证成功，clientId: {}, captchaKey: {}", clientId, captchaKey);
            return ExceptionResult.success(true);
        } catch (Exception e) {
            log.error("验证码验证失败，clientId: {}, 错误: {}", clientId, e.getClass().getSimpleName(), e.getMessage());
            return ExceptionResult.error("验证码验证失败");
        }
    }

    /**
     * 验证短信验证码
     * @param phone       手机号
     * @param captchaCode 验证码
     * @return 验证结果响应
     */
    public ExceptionResult<Boolean> validateSmsCaptchaResponse(String phone, String captchaCode) {
        try {
            String key = SMS_CAPTCHA_PREFIX + phone;
            log.debug("短信验证码Redis Key: {}", key);
            String storedCaptchaCode = redisTemplate.opsForValue().get(key);

            if (captchaCode == null || !captchaCode.equals(storedCaptchaCode)) {
                log.warn("短信验证码不匹配或为空，phone: {}, 存储验证码: {}, 用户输入验证码: {}", phone, storedCaptchaCode, captchaCode);
                return ExceptionResult.error("短信验证码不匹配或为空");
            }

            // 验证成功后立即删除验证码
            redisTemplate.delete(key);
            log.info("短信验证码验证成功，phone: {}", phone);
            return ExceptionResult.success(true);
        } catch (Exception e) {
            log.error("短信验证码验证失败，phone: {}, 错误: {}", phone, e.getClass().getSimpleName(), e.getMessage());
            return ExceptionResult.error("短信验证码验证失败");
        }
    }

    /**
     * 从HTTP请求中验证图片验证码
     * @param request HTTP请求
     * @return 验证结果响应
     */
    public ExceptionResult<Boolean> validateCaptchaFromRequest(HttpServletRequest request) {
        log.debug("开始从请求中验证验证码");
        try {
            // 只处理application/json请求
            if (!MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType())) {
                log.warn("不支持的请求格式: {}", request.getContentType());
                return ExceptionResult.error("不支持的请求格式");
            }

            // 读取请求体
            String requestBody = request.getReader().lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);

            // 解析请求体
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            // 获取验证码参数
            String clientId = jsonNode.has(CLIENT_ID_PARAM) ? jsonNode.get(CLIENT_ID_PARAM).asString() : null;
            String captchaKey = jsonNode.has(CAPTCHA_KEY_PARAM) ? jsonNode.get(CAPTCHA_KEY_PARAM).asString() : null;
            String captchaCode = jsonNode.has(IMAGE_CAPTCHA_CODE_PARAM)
                    ? jsonNode.get(IMAGE_CAPTCHA_CODE_PARAM).asString()
                    : null;

            // 参数验证
            if (clientId == null) {
                log.warn("缺少客户端ID参数: {}", CLIENT_ID_PARAM);
                return ExceptionResult.error("缺少客户端ID参数");
            }
            if (captchaKey == null) {
                log.warn("缺少验证码Key参数");
                return ExceptionResult.error("缺少验证码Key参数");
            }
            if (captchaCode == null) {
                log.warn("缺少验证码参数: {}", IMAGE_CAPTCHA_CODE_PARAM);
                return ExceptionResult.error("缺少验证码参数");
            }

            // 验证验证码
            return validateCaptchaResponse(clientId, captchaKey, captchaCode);
        } catch (IOException e) {
            log.error("请求体解析失败：{} - {}", e.getClass().getSimpleName(), e.getMessage());
            return ExceptionResult.error("请求体解析失败");
        }
    }

    /**
     * 从HTTP请求中验证短信验证码
     * @param request HTTP请求
     * @return 验证结果响应
     */
    public ExceptionResult<Boolean> validateSmsCaptchaFromRequest(HttpServletRequest request) {
        log.debug("开始从请求中验证短信验证码");
        try {
            // 只处理application/json请求
            if (!MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType())) {
                log.warn("不支持的请求格式: {}", request.getContentType());
                return ExceptionResult.error("不支持的请求格式");
            }

            // 读取请求体
            String requestBody = request.getReader().lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);

            // 解析请求体
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            // 获取验证码参数
            String phone = jsonNode.has("phone") ? jsonNode.get("phone").asString() : null;
            String captchaCode = jsonNode.has(SMS_CAPTCHA_CODE_PARAM) ? jsonNode.get(SMS_CAPTCHA_CODE_PARAM).asString() : null;

            // 参数验证
            if (phone == null) {
                log.warn("缺少手机号参数");
                return ExceptionResult.error("缺少手机号参数");
            }
            if (captchaCode == null) {
                log.warn("缺少验证码参数: {}", SMS_CAPTCHA_CODE_PARAM);
                return ExceptionResult.error("缺少验证码参数");
            }

            // 验证验证码
            return validateSmsCaptchaResponse(phone, captchaCode);
        } catch (IOException e) {
            log.error("请求体解析失败：{} - {}", e.getClass().getSimpleName(), e.getMessage());
            return ExceptionResult.error("请求体解析失败");
        }
    }

    // 静态工具方法

    /**
     * 生成验证码文本
     */
    public static String generateCaptchaText() {
        return RandomStringUtils.secure().next(5, "23456789ABCDEFGHJKLMNPQRSTUVWXYZ");
    }

    /**
     * 生成验证码图片
     * @param code 验证码文本
     * @return base64编码的图片
     */
    public static String generateCaptchaImage(String code) {
        return ImageCaptchaUtil.generateImage(code);
    }

    /**
     * 保存验证码token
     * @param clientId 客户端ID
     * @param captchaText 验证码文本
     * @return token字符串
     */
    public static String saveCaptchaToken(String clientId, String captchaText) {
        // 这里应该结合Redis实现，为了简化暂时返回一个简单的token
        return clientId + ":" + System.currentTimeMillis();
    }

    /**
     * 验证验证码token
     * @param clientId 客户端ID
     * @param captchaToken 验证码token
     * @return 验证结果
     */
    public static boolean validateCaptcha(String clientId, String captchaToken) {
        // 这里应该结合Redis实现，为了简化暂时返回true
        return captchaToken != null && captchaToken.startsWith(clientId + ":");
    }

    /**
     * 验证短信验证码
     * @param phone 手机号
     * @param smsCode 短信验证码
     * @return 验证结果
     */
    public static boolean validateSmsCaptcha(String phone, String smsCode) {
        // 这里应该结合Redis实现，为了简化暂时返回true
        return smsCode != null && smsCode.length() == 6;
    }
}