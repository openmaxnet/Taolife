package com.taolife.aicore.adapter;

import com.taolife.aicore.model.ChatCompletionRequest;
import com.taolife.aicore.model.StreamChunk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 智谱AI厂商适配器
 * 处理智谱特有的API差异：
 * - 思考模式: thinking: { type: "enabled" }
 * - 流式解析: delta.reasoning_content + delta.content
 */
@Slf4j
@Component
public class ZhipuVendorAdapter extends OpenAIVendorAdapter {

    private static final String VENDOR_CODE = "zhipu";

    /**
     * 获取厂商编码
     *
     * @return 厂商编码（zhipu）
     */
    @Override
    public String getVendorCode() {
        return VENDOR_CODE;
    }

    /**
     * 自定义请求体
     * 智谱AI的思考模式通过 thinking: { type: "enabled" } 启用
     */
    @Override
    public void customizeRequestBody(Map<String, Object> body, ChatCompletionRequest request) {
        String thinkingType = Boolean.TRUE.equals(request.getEnableThinking()) ? "enabled" : "disabled";
        body.put("thinking", Map.of("type", thinkingType));
    }

    /**
     * 解析智谱流式响应块
     * 优先解析 reasoning_content（思考过程），再解析 content（正式回答）
     *
     * @param rawData 原始响应数据
     * @return 解析后的流式块
     */
    @Override
    public StreamChunk parseStreamChunk(String rawData) {
        if (rawData == null || rawData.isEmpty()) {
            return StreamChunk.EMPTY;
        }

        String jsonData = rawData;
        if (rawData.startsWith("data: ")) {
            jsonData = rawData.substring(6);
            if (jsonData.equals("[DONE]")) {
                return StreamChunk.DONE;
            }
        }

        try {
            // 优先检查 reasoning_content（思考过程）
            String reasoning = extractDeltaContent(jsonData, "reasoning_content");
            if (reasoning != null && !reasoning.isEmpty()) {
                return StreamChunk.thinking(reasoning);
            }

            // 然后检查 content（正式回答）
            String content = extractDeltaContent(jsonData, "content");
            if (content != null && !content.isEmpty()) {
                return StreamChunk.answer(content);
            }
        } catch (Exception e) {
            log.debug("解析智谱流式响应块失败: {}", e.getMessage());
        }

        return StreamChunk.EMPTY;
    }

    /**
     * 解析智谱AI错误响应
     * 智谱错误格式：{"error":{"code":"xxx","message":"xxx"}}
     *
     * @param responseBody 响应体内容
     * @param httpStatus   HTTP状态码
     * @return 错误信息对象
     */
    @Override
    public LlmError parseError(String responseBody, int httpStatus) {
        // 智谱错误格式: {"error":{"code":"xxx","message":"xxx"}}
        try {
            String code = extractJsonValue(responseBody, "code");
            String message = extractJsonValue(responseBody, "message");
            return new LlmError(
                    code != null ? code : "ZHIPU_ERROR",
                    message != null ? message : responseBody,
                    httpStatus
            );
        } catch (Exception e) {
            return new LlmError("ZHIPU_ERROR", responseBody, httpStatus);
        }
    }

    /**
     * 从嵌套JSON中提取指定字段的值
     */
    private String extractJsonValue(String json, String fieldName) {
        String searchKey = "\"" + fieldName + "\"";
        int idx = json.indexOf(searchKey);
        if (idx < 0) {
            return null;
        }
        int colonIdx = json.indexOf(':', idx + searchKey.length());
        if (colonIdx < 0) {
            return null;
        }
        int startQuote = json.indexOf('"', colonIdx + 1);
        if (startQuote < 0) {
            return null;
        }
        int endQuote = json.indexOf('"', startQuote + 1);
        if (endQuote < 0) {
            return null;
        }
        return json.substring(startQuote + 1, endQuote);
    }
}
