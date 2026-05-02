package com.taolife.common.exception;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * 智谱AI API错误处理器
 * 将 WebClientResponseException 解析为友好的 BusinessException
 * <p>
 * 解析逻辑：
 * 1. 从响应体中提取智谱业务错误码和消息
 * 2. 优先按业务错误码映射到系统内部 ExceptionCode
 * 3. 无业务码时按 HTTP 状态码映射
 * 4. 使用智谱官方错误描述作为异常消息
 *
 * @author 文二
 * @date 2026-04-09
 */
@Slf4j
public final class ZhipuErrorHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ZhipuErrorHandler() {
    }

    /**
     * 处理 WebClient 响应异常，转换为 BusinessException
     *
     * @param e WebClientResponseException
     * @return BusinessException 包含映射后的错误码和智谱官方消息
     */
    public static BusinessException handle(WebClientResponseException e) {
        int httpStatus = e.getStatusCode().value();
        String body = e.getResponseBodyAsString();

        // 解析业务错误码和消息
        String bizCode = null;
        String bizMessage = null;
        if (body != null && !body.isBlank()) {
            String[] parsed = parseErrorBody(body);
            bizCode = parsed[0];
            bizMessage = parsed[1];
        }

        // 优先按业务错误码映射
        if (bizCode != null) {
            ZhipuErrorCode zhipuCode = ZhipuErrorCode.fromBizCode(bizCode);
            String message = bizMessage != null && !bizMessage.isBlank()
                    ? bizMessage
                    : zhipuCode.getMessage();
            log.warn("智谱AI错误: HTTP {}, 码: {}, {}", httpStatus, bizCode, message);
            return new BusinessException(zhipuCode.getMappedCode(), message);
        }

        // 按 HTTP 状态码映射
        ZhipuErrorCode zhipuCode = ZhipuErrorCode.fromHttpStatus(httpStatus);
        log.warn("智谱AI错误: HTTP {}, {}", httpStatus, zhipuCode.getMessage());
        return new BusinessException(zhipuCode.getMappedCode(), zhipuCode.getMessage());
    }

    /**
     * 解析智谱AI错误响应体
     * 响应格式: {"error":{"code":"1002","message":"..."}}
     *
     * @param body 响应体字符串
     * @return String[2]: [0]=业务错误码, [1]=错误消息; 解析失败时返回 [null, null]
     */
    static String[] parseErrorBody(String body) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(body);
            JsonNode errorNode = root.path("error");
            if (errorNode.isMissingNode()) {
                return new String[]{null, null};
            }
            String code = errorNode.path("code").asString(null);
            String message = errorNode.path("message").asString(null);
            return new String[]{code, message};
        } catch (Exception e) {
            log.debug("解析智谱AI错误响应体失败: {}", e.getMessage());
            return new String[]{null, null};
        }
    }
}
