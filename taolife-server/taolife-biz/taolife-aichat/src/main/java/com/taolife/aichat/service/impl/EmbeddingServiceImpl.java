package com.taolife.aichat.service.impl;

import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aichat.service.IEmbeddingService;
import com.taolife.aicore.config.EmbeddingConfigVO;
import com.taolife.common.model.EmbeddingApiRequest;
import com.taolife.common.model.EmbeddingApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * 向量化服务实现
 * 接入Qwen3-Embedding-8B向量模型
 *
 * @author 文二
 * @date 2026-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements IEmbeddingService {

    private final IAiConfigService aiConfigService;
    private final WebClient.Builder webClientBuilder;

    /**
     * 文本向量化
     * 调用Qwen3-Embedding-8B向量模型API，将输入文本转换为向量表示
     *
     * @param text 待向量化的文本
     * @return 浮点数向量数组，向量维度由配置决定（默认1024）
     */
    @Override
    public float[] embed(String text) {
        try {
            EmbeddingConfigVO config = aiConfigService.getEmbeddingConfig();

            // 构建请求
            EmbeddingApiRequest request = new EmbeddingApiRequest();
            request.setModel(config.getModel());
            request.setInput(text);
            request.setEncodingFormat("float");

            // 调用向量模型API
            WebClient webClient = webClientBuilder.build();
            EmbeddingApiResponse response = webClient.post()
                    .uri(config.getFullUrl())
                    .header("Authorization", "Bearer " + config.getEncryptedApiKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(EmbeddingApiResponse.class)
                    .block(Duration.ofMillis(30000)); // 默认30秒超时

            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                log.info("文本向量化成功，文本长度: {}, 向量维度: {}",
                        text.length(), response.getData().get(0).getEmbedding().length);
                return response.getData().get(0).getEmbedding();
            }

            log.error("向量模型API返回空响应");
            return new float[config.getDimensions() != null ? config.getDimensions() : 1024];

        } catch (Exception e) {
            log.error("文本向量化失败: {}", text, e);
            EmbeddingConfigVO config = aiConfigService.getEmbeddingConfig();
            return new float[config.getDimensions() != null ? config.getDimensions() : 1024];
        }
    }

    /**
     * 批量文本向量化
     * 遍历文本数组，逐条调用单文本向量化方法
     *
     * @param texts 待向量化的文本数组
     * @return 浮点数向量二维数组，每个文本对应一个向量
     */
    @Override
    public float[][] embedBatch(String[] texts) {
        float[][] results = new float[texts.length][];
        for (int i = 0; i < texts.length; i++) {
            results[i] = embed(texts[i]);
        }
        return results;
    }

    /**
     * 计算余弦相似度
     * 计算两个向量之间的余弦相似度，值范围[-1, 1]
     *
     * @param vec1 向量1
     * @param vec2 向量2
     * @return 余弦相似度值
     * @throws IllegalArgumentException 当两个向量维度不匹配时抛出
     */
    @Override
    public double cosineSimilarity(float[] vec1, float[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }

        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}