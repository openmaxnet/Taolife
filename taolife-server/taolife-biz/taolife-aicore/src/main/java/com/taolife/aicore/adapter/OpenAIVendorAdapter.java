package com.taolife.aicore.adapter;

import com.taolife.aicore.model.ChatCompletionRequest;
import com.taolife.aicore.model.StreamChunk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OpenAI标准适配器（默认/回退）
 * 适用于所有遵循标准OpenAI API协议的厂商
 */
@Slf4j
@Component
public class OpenAIVendorAdapter implements VendorAdapter {

    private static final String VENDOR_CODE = "openai";

    @Override
    public String getVendorCode() {
        return VENDOR_CODE;
    }

    /**
     * 自定义请求体
     * 标准OpenAI API无需额外参数
     */
    @Override
    public void customizeRequestBody(Map<String, Object> body, ChatCompletionRequest request) {
        // 标准OpenAI API，无需额外参数
    }

    /**
     * 解析流式响应块
     * 标准OpenAI格式：{"choices":[{"delta":{"content":"xxx"}}]}
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
            // 使用简单的字符串匹配解析，避免在 core 模块中依赖 Jackson
            // 标准格式: {"choices":[{"delta":{"content":"xxx"}}]}
            String content = extractDeltaContent(jsonData, "content");
            if (content != null && !content.isEmpty()) {
                return StreamChunk.answer(content);
            }
        } catch (Exception e) {
            log.debug("解析流式响应块失败: {}", e.getMessage());
        }

        return StreamChunk.EMPTY;
    }

    /**
     * 解析错误响应
     *
     * @param responseBody 响应体内容
     * @param httpStatus   HTTP状态码
     * @return 错误信息对象
     */
    @Override
    public LlmError parseError(String responseBody, int httpStatus) {
        return new LlmError("LLM_ERROR", responseBody, httpStatus);
    }

    /**
     * 从JSON中提取 delta 字段的指定子字段内容
     * 简单的JSON字符串解析，避免在core模块依赖Jackson
     */
    protected String extractDeltaContent(String jsonData, String fieldName) {
        // 查找 "delta":{...} 块中的目标字段
        int deltaIdx = jsonData.indexOf("\"delta\"");
        if (deltaIdx < 0) {
            return null;
        }

        String searchKey = "\"" + fieldName + "\"";
        int fieldIdx = jsonData.indexOf(searchKey, deltaIdx);
        if (fieldIdx < 0) {
            return null;
        }

        // 找到冒号后的值
        int colonIdx = jsonData.indexOf(':', fieldIdx + searchKey.length());
        if (colonIdx < 0) {
            return null;
        }

        // 跳过冒号后的空白，检查是否为 null 值
        int valueStart = colonIdx + 1;
        while (valueStart < jsonData.length() && jsonData.charAt(valueStart) == ' ') {
            valueStart++;
        }
        if (valueStart + 4 <= jsonData.length()
                && jsonData.substring(valueStart, valueStart + 4).equals("null")) {
            return null;
        }

        // 找到引号包裹的字符串值
        int startQuote = jsonData.indexOf('"', colonIdx + 1);
        if (startQuote < 0) {
            return null;
        }
        int endQuote = jsonData.indexOf('"', startQuote + 1);
        if (endQuote < 0) {
            return null;
        }

        return jsonData.substring(startQuote + 1, endQuote);
    }
}
