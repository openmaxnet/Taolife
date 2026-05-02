package com.taolife.aicore.adapter;

import com.taolife.aicore.model.ChatCompletionRequest;
import com.taolife.aicore.model.StreamChunk;

import java.util.Map;

/**
 * 厂商适配器接口
 * 抽象不同LLM厂商在OpenAI API基础上的差异
 */
public interface VendorAdapter {

    /**
     * 厂商标识码，对应 tf_ai_model_provider.provider_code
     */
    String getVendorCode();

    /**
     * 在标准OpenAI请求体基础上注入厂商特定参数
     * 如：智谱的 thinking 参数、DeepSeek 的 reasoning_effort 参数
     *
     * @param body    已构建的标准请求体（包含 model、messages、stream 等）
     * @param request 原始请求（可用于判断 enableThinking 等）
     */
    void customizeRequestBody(Map<String, Object> body, ChatCompletionRequest request);

    /**
     * 解析厂商的流式响应块为统一的 StreamChunk
     * 处理不同厂商在 delta 中的字段命名差异
     *
     * @param rawData 原始 SSE data 行（已去掉 "data: " 前缀）
     * @return 标准化的流式块
     */
    StreamChunk parseStreamChunk(String rawData);

    /**
     * 解析厂商的错误响应
     *
     * @param responseBody HTTP 响应体
     * @param httpStatus   HTTP 状态码
     * @return 标准化的错误信息
     */
    LlmError parseError(String responseBody, int httpStatus);

    /**
     * LLM错误信息
     */
    record LlmError(String code, String message, int httpStatus) {
    }
}
