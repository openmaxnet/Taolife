package com.taolife.aicore.client;

import com.taolife.aicore.config.ChatConfigVO;
import com.taolife.aicore.config.EmbeddingConfigVO;
import com.taolife.aicore.model.ChatCompletionRequest;
import com.taolife.aicore.model.ChatCompletionResponse;
import com.taolife.aicore.model.StreamChunk;
import reactor.core.publisher.Flux;

/**
 * 统一LLM客户端接口
 * 厂商无关的OpenAI API兼容客户端
 */
public interface AiClient {

    /**
     * 非流式Chat Completion
     *
     * @param request 请求参数（messages、temperature 等）
     * @param config  模型配置（fullUrl、apiKey、vendorCode 等）
     * @return 响应结果
     */
    ChatCompletionResponse chatCompletions(ChatCompletionRequest request, ChatConfigVO config);

    /**
     * 流式Chat Completion
     *
     * @param request 请求参数
     * @param config  模型配置
     * @return 标准化流式块（THINKING/ANSWER/EMPTY）
     */
    Flux<StreamChunk> chatCompletionsStream(ChatCompletionRequest request, ChatConfigVO config);

    /**
     * 非流式Embedding
     *
     * @param input  输入文本或文本列表
     * @param config Embedding配置
     * @return 嵌入向量结果（原始JSON字符串，由调用方解析）
     */
    String embeddings(Object input, EmbeddingConfigVO config);
}
