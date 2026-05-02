package com.taolife.aicore.adapter;

import com.taolife.aicore.model.ChatCompletionRequest;
import com.taolife.aicore.model.StreamChunk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * DeepSeek厂商适配器
 * DeepSeek的API基本兼容OpenAI协议，思考模式参数略有差异
 */
@Slf4j
@Component
public class DeepSeekVendorAdapter extends OpenAIVendorAdapter {

    private static final String VENDOR_CODE = "deepseek";

    /**
     * 获取厂商编码
     *
     * @return 厂商编码（deepseek）
     */
    @Override
    public String getVendorCode() {
        return VENDOR_CODE;
    }

    /**
     * 自定义请求体
     * DeepSeek的思考模式参数预留扩展
     */
    @Override
    public void customizeRequestBody(Map<String, Object> body, ChatCompletionRequest request) {
        // DeepSeek v4 默认开启思考模式，需显式控制
        String thinkingType = Boolean.TRUE.equals(request.getEnableThinking()) ? "enabled" : "disabled";
        body.put("thinking", Map.of("type", thinkingType));
    }

    /**
     * 解析DeepSeek流式响应块
     * 兼容 reasoning_content（思考过程）和 content（正式回答）
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
            // DeepSeek同样使用 reasoning_content 字段
            String reasoning = extractDeltaContent(jsonData, "reasoning_content");
            if (reasoning != null && !reasoning.isEmpty()) {
                return StreamChunk.thinking(reasoning);
            }

            String content = extractDeltaContent(jsonData, "content");
            if (content != null && !content.isEmpty()) {
                return StreamChunk.answer(content);
            }
        } catch (Exception e) {
            log.debug("解析DeepSeek流式响应块失败: {}", e.getMessage());
        }

        return StreamChunk.EMPTY;
    }
}
