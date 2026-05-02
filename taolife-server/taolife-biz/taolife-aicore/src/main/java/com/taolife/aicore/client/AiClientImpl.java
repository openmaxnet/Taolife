package com.taolife.aicore.client;

import com.taolife.aicore.adapter.VendorAdapter;
import com.taolife.aicore.adapter.VendorAdapterRegistry;
import com.taolife.aicore.config.ChatConfigVO;
import com.taolife.aicore.config.EmbeddingConfigVO;
import com.taolife.aicore.exception.LlmException;
import com.taolife.aicore.model.ChatCompletionRequest;
import com.taolife.aicore.model.ChatCompletionResponse;
import com.taolife.aicore.model.Message;
import com.taolife.aicore.model.StreamChunk;

import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一LLM客户端实现
 * 通过 VendorAdapter 适配不同厂商的API差异
 */
@Slf4j
@Service
public class AiClientImpl implements AiClient {

    private final VendorAdapterRegistry adapterRegistry;
    private final WebClient.Builder webClientBuilder;
    private final MeterRegistry meterRegistry;

    public AiClientImpl(VendorAdapterRegistry adapterRegistry, WebClient.Builder webClientBuilder,
                        MeterRegistry meterRegistry) {
        this.adapterRegistry = adapterRegistry;
        this.webClientBuilder = webClientBuilder;
        this.meterRegistry = meterRegistry;
    }

    /**
     * 非流式调用LLM API
     * 通过VendorAdapter适配不同厂商的请求/响应差异
     *
     * @param request 聊天补全请求
     * @param config  调用配置（含厂商、模型、密钥等）
     * @return 聊天补全响应
     */
    @Override
    public ChatCompletionResponse chatCompletions(ChatCompletionRequest request, ChatConfigVO config) {
        log.info("非流式调用LLM API，厂商: {}, 模型: {}, 消息数: {}",
                config.getVendorCode(), config.getModel(),
                request.getMessages() != null ? request.getMessages().size() : 0);
        try {
            VendorAdapter adapter = adapterRegistry.getAdapter(config.getVendorCode());
            Map<String, Object> requestBody = buildRequestBody(request, adapter, false);

            ChatCompletionResponse response = meterRegistry.timer("biz.ai.request", "vendor", config.getVendorCode(), "model", config.getModel())
                    .record(() -> buildWebClient(config.getFullUrl())
                            .post()
                            .uri(config.getFullUrl())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getEncryptedApiKey())
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(ChatCompletionResponse.class)
                            .block());

            meterRegistry.counter("biz.ai.request", "vendor", config.getVendorCode(), "model", config.getModel(), "status", "success").increment();
            log.info("LLM API调用成功（非流式），模型: {}, Token: {}",
                    response.getModel(),
                    response.getUsage() != null && response.getUsage().getTotalTokens() != null
                            ? response.getUsage().getTotalTokens() : 0);
            return response;
        } catch (WebClientResponseException e) {
            meterRegistry.counter("biz.ai.request", "vendor", config.getVendorCode(), "model", config.getModel(), "status", "error").increment();
            VendorAdapter adapter = adapterRegistry.getAdapter(config.getVendorCode());
            VendorAdapter.LlmError error = adapter.parseError(e.getResponseBodyAsString(), e.getStatusCode().value());
            throw new LlmException(error.code(), error.message(), error.httpStatus(), e);
        } catch (LlmException e) {
            throw e;
        } catch (Exception e) {
            meterRegistry.counter("biz.ai.request", "vendor", config.getVendorCode(), "model", config.getModel(), "status", "error").increment();
            log.warn("调用LLM API发生异常: {}", e.getMessage());
            throw new LlmException("LLM_CALL_FAILED", "调用LLM API失败: " + e.getMessage(), 500, e);
        }
    }

    /**
     * 流式调用LLM API（SSE）
     * 通过VendorAdapter将原始响应行解析为统一的StreamChunk
     *
     * @param request 聊天补全请求
     * @param config  调用配置
     * @return 流式块Flux
     */
    @Override
    public Flux<StreamChunk> chatCompletionsStream(ChatCompletionRequest request, ChatConfigVO config) {
        log.info("流式调用LLM API，厂商: {}, 模型: {}, 思考模式: {}",
                config.getVendorCode(), config.getModel(), request.getEnableThinking());
        try {
            VendorAdapter adapter = adapterRegistry.getAdapter(config.getVendorCode());
            Map<String, Object> requestBody = buildRequestBody(request, adapter, true);

            boolean enableThinking = Boolean.TRUE.equals(request.getEnableThinking());

            return buildWebClient(config.getFullUrl())
                    .post()
                    .uri(config.getFullUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getEncryptedApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .map(adapter::parseStreamChunk)
                    .filter(chunk -> !chunk.isEmpty())
                    .filter(chunk -> enableThinking || chunk.type() != StreamChunk.ChunkType.THINKING)
                    .doOnComplete(() -> log.info("LLM流式调用完成: 厂商: {}, 模型: {}, 消息数: {}",
                            config.getVendorCode(), config.getModel(),
                            request.getMessages() != null ? request.getMessages().size() : 0))
                    .onErrorResume(WebClientResponseException.class, e -> {
                        VendorAdapter.LlmError error = adapter.parseError(
                                e.getResponseBodyAsString(), e.getStatusCode().value());
                        return Flux.error(new LlmException(error.code(), error.message(), error.httpStatus(), e));
                    });
        } catch (LlmException e) {
            return Flux.error(e);
        } catch (Exception e) {
            log.warn("流式调用LLM API发生异常: {}", e.getMessage());
            return Flux.error(new LlmException("LLM_CALL_FAILED", "调用LLM API失败: " + e.getMessage(), 500, e));
        }
    }

    /**
     * 调用Embedding API
     *
     * @param input  输入文本或文本列表
     * @param config Embedding调用配置
     * @return Embedding原始响应JSON字符串
     */
    @Override
    public String embeddings(Object input, EmbeddingConfigVO config) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("input", input);
        body.put("encoding_format", "float");
        if (config.getDimensions() != null) {
            body.put("dimensions", config.getDimensions());
        }

        return buildWebClient(config.getFullUrl())
                .post()
                .uri(config.getFullUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getEncryptedApiKey())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * 构建WebClient
     *
     * @param baseUrl 基础URL
     * @return WebClient实例
     */
    private WebClient buildWebClient(String baseUrl) {
        return webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * 构建请求体（标准OpenAI格式 + 厂商适配器扩展）
     */
    private Map<String, Object> buildRequestBody(ChatCompletionRequest request, VendorAdapter adapter, boolean stream) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", request.getModel());
        body.put("messages", convertMessagesToMap(request.getMessages()));
        body.put("stream", stream);

        if (request.getTemperature() != null) {
            body.put("temperature", request.getTemperature());
        }
        if (request.getMaxTokens() != null) {
            body.put("max_tokens", request.getMaxTokens());
        }
        if (request.getTopP() != null) {
            body.put("top_p", request.getTopP());
        }
        if (Boolean.TRUE.equals(request.getJsonObjectResponse())) {
            body.put("response_format", Map.of("type", "json_object"));
        }

        // 厂商特定参数注入
        adapter.customizeRequestBody(body, request);

        // 透传额外厂商参数
        if (request.getVendorParams() != null) {
            body.putAll(request.getVendorParams());
        }

        return body;
    }

    /**
     * 将Message列表转换为Map列表
     *
     * @param messages 消息列表
     * @return Map列表
     */
    private List<Map<String, String>> convertMessagesToMap(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }
        return messages.stream()
                .map(msg -> {
                    Map<String, String> msgMap = new HashMap<>();
                    msgMap.put("role", msg.getRole());
                    msgMap.put("content", msg.getContent());
                    return msgMap;
                })
                .toList();
    }
}
