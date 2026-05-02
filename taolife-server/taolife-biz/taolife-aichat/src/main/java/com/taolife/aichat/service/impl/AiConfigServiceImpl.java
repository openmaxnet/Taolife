package com.taolife.aichat.service.impl;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import com.taolife.aicore.balance.ModelCandidate;
import com.taolife.aicore.balance.ModelSelector;
import com.taolife.aicore.config.ChatConfigVO;
import com.taolife.aicore.config.EmbeddingConfigVO;
import com.taolife.aichat.entity.*;
import com.taolife.aichat.enums.AiCacheExpireEnum;
import com.taolife.aichat.enums.AiEndpointTypeEnum;
import com.taolife.aichat.enums.AiProviderTypeEnum;
import com.taolife.aichat.enums.AiRedisKeyEnum;
import com.taolife.aichat.mapper.*;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aichat.service.IModelConcurrencyService;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.AesEncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI 配置服务实现
 * 管理场景配置、提示词模板、模型路由与负载均衡、缓存控制
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiConfigServiceImpl implements IAiConfigService {

    private final StringRedisTemplate stringRedisTemplate;
    private final AiProviderMapper aiProviderMapper;
    private final AiModelMapper aiModelMapper;
    private final AiPromptTemplateMapper aiPromptTemplateMapper;
    private final AiSceneConfigMapper aiSceneConfigMapper;
    private final AiProviderEndpointMapper aiProviderEndpointMapper;
    private final AiSceneModelMapper aiSceneModelMapper;
    private final IModelConcurrencyService concurrencyService;
    private final ModelSelector modelSelector;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========== 场景配置 ==========

    /**
     * 根据场景编码获取Chat配置（缓存优先）
     * 从缓存获取，缓存未命中时从数据库加载并写入缓存
     *
     * @param sceneCode 场景编码
     * @return Chat配置
     */
    @Override
    public ChatConfigVO getChatConfig(String sceneCode) {
        // 尝试从缓存获取
        String cacheKey = AiRedisKeyEnum.CHAT_CONFIG_PREFIX.getCode() + (sceneCode != null ? sceneCode : "default");
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, ChatConfigVO.class);
            } catch (JacksonException e) {
                log.warn("解析Chat配置缓存失败，重新获取: {}", e.getMessage());
            }
        }

        // 从数据库加载
        ChatConfigVO config = loadChatConfigFromDb(sceneCode);

        // 写入缓存
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(config),
                    AiCacheExpireEnum.DEFAULT_CACHE_HOURS.getValue() + (long) (Math.random() * 10), TimeUnit.MINUTES);
        } catch (JacksonException e) {
            log.error("写入Chat配置缓存失败: {}", e.getMessage());
        }

        return config;
    }

    /**
     * 从数据库加载Chat配置
     * 先尝试通过场景配置解析，降级时使用默认provider+默认model
     *
     * @param sceneCode 场景编码
     * @return Chat配置
     */
    private ChatConfigVO loadChatConfigFromDb(String sceneCode) {
        // 路径1: 通过场景配置解析
        if (sceneCode != null) {
            AiSceneConfig sceneConfig = aiSceneConfigMapper.selectBySceneCode(sceneCode);
            if (sceneConfig != null && sceneConfig.getIsEnabled() == 1) {
                return resolveSceneConfig(sceneConfig);
            }
        }

        // 路径2: 降级为默认provider + 默认model
        return resolveDefaultConfig();
    }

    /**
     * 解析场景配置为Chat配置
     * 支持多模型负载均衡和单模型两种模式
     *
     * @param sceneConfig 场景配置
     * @return Chat配置
     */
    private ChatConfigVO resolveSceneConfig(AiSceneConfig sceneConfig) {
        // 查找场景绑定的模型列表
        List<AiSceneModel> sceneModels = aiSceneModelMapper.selectBySceneConfigId(sceneConfig.getId());

        if (sceneModels != null && !sceneModels.isEmpty()) {
            // 多模型: 通过负载均衡选择
            List<ModelCandidate> candidates = buildCandidates(sceneModels);
            if (!candidates.isEmpty()) {
                ModelCandidate selected = modelSelector.select(candidates);
                if (selected != null) {
                    return selected.config();
                }
                // 所有模型满载，使用第一个（调用方根据策略处理）
                return candidates.get(0).config();
            }
        }

        // 单模型: 通过 sceneConfig.modelInstanceId
        if (sceneConfig.getModelInstanceId() != null) {
            return buildConfigFromModelId(sceneConfig.getModelInstanceId());
        }

        return resolveDefaultConfig();
    }

    /**
     * 构建候选模型列表（含并发信息）
     *
     * @param sceneModels 场景绑定的模型列表
     * @return 候选模型列表
     */
    private List<ModelCandidate> buildCandidates(List<AiSceneModel> sceneModels) {
        return sceneModels.stream()
                .map(sm -> {
                    ChatConfigVO config = buildConfigFromModelId(sm.getModelId());
                    if (config == null) return null;

                    AiModel model = aiModelMapper.selectOneById(sm.getModelId());
                    int maxConcurrency = sm.getMaxConcurrency() != null && sm.getMaxConcurrency() > 0
                            ? sm.getMaxConcurrency()
                            : (model.getMaxConcurrency() != null ? model.getMaxConcurrency() : 0);
                    int currentConcurrency = concurrencyService.getCurrentConcurrency(sm.getModelId());

                    return new ModelCandidate(
                            sm.getModelId(), config,
                            sm.getPriority() != null ? sm.getPriority() : 0,
                            sm.getWeight() != null ? sm.getWeight() : 100,
                            maxConcurrency, currentConcurrency
                    );
                })
                .filter(c -> c != null)
                .toList();
    }

    /**
     * 根据模型ID构建Chat配置
     * 从模型、厂商、端点三级关联查询组装完整配置
     *
     * @param modelId 模型ID
     * @return Chat配置，模型不可用时返回null
     */
    private ChatConfigVO buildConfigFromModelId(String modelId) {
        AiModel model = aiModelMapper.selectOneById(modelId);
        if (model == null || model.getStatus() != 1) {
            return null;
        }

        AiProvider provider = aiProviderMapper.selectOneById(model.getProviderId());
        if (provider == null || provider.getStatus() != 1) {
            return null;
        }

        AiProviderEndpoint endpoint = aiProviderEndpointMapper.selectOneByProviderIdAndType(
                provider.getId(), AiEndpointTypeEnum.MODEL_API.getCode());
        if (endpoint == null) {
            return null;
        }

        String fullUrl = buildFullUrl(provider.getApiEndpoint(), endpoint.getEndpointUri());

        ChatConfigVO config = new ChatConfigVO();
        config.setFullUrl(fullUrl);
        config.setModel(model.getModelCode());
        config.setModelName(model.getModelName());
        config.setTemperature(model.getTemperature());
        config.setMaxTokens(model.getMaxTokens());
        config.setTopP(model.getTopP());
        config.setEncryptedApiKey(AesEncryptionUtil.decrypt(provider.getApiKey()));
        config.setSupportsThinking(model.getSupportsThinking() != null && model.getSupportsThinking() == 1);
        config.setSupportsImage(model.getSupportsImage() != null && model.getSupportsImage() == 1);
        config.setProviderId(provider.getId());
        config.setVendorCode(provider.getProviderCode());
        config.setModelId(model.getId());
        return config;
    }

    /**
     * 解析默认Chat配置
     * 查找默认Chat厂商下的默认模型和端点
     *
     * @return Chat配置
     */
    private ChatConfigVO resolveDefaultConfig() {
        AiProvider provider = aiProviderMapper.selectDefaultByType(AiProviderTypeEnum.CHAT.getCode());
        if (provider == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "未配置默认的Chat厂商");
        }

        AiProviderEndpoint endpoint = aiProviderEndpointMapper.selectOneByProviderIdAndType(
                provider.getId(), AiEndpointTypeEnum.MODEL_API.getCode());
        if (endpoint == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "Chat厂商未配置model_api端点");
        }

        AiModel aiModel = aiModelMapper.selectDefaultByTypeAndProviderId(
                AiProviderTypeEnum.CHAT.getCode(), provider.getId());
        if (aiModel == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "默认Chat厂商下未配置默认模型");
        }

        String fullUrl = buildFullUrl(provider.getApiEndpoint(), endpoint.getEndpointUri());

        ChatConfigVO config = new ChatConfigVO();
        config.setFullUrl(fullUrl);
        config.setModel(aiModel.getModelCode());
        config.setModelName(aiModel.getModelName());
        config.setTemperature(aiModel.getTemperature());
        config.setMaxTokens(aiModel.getMaxTokens());
        config.setTopP(aiModel.getTopP());
        config.setEncryptedApiKey(AesEncryptionUtil.decrypt(provider.getApiKey()));
        config.setSupportsThinking(aiModel.getSupportsThinking() != null && aiModel.getSupportsThinking() == 1);
        config.setSupportsImage(aiModel.getSupportsImage() != null && aiModel.getSupportsImage() == 1);
        config.setProviderId(provider.getId());
        config.setVendorCode(provider.getProviderCode());
        config.setModelId(aiModel.getId());
        return config;
    }

    /**
     * 获取Embedding配置（缓存优先）
     *
     * @return Embedding配置
     */
    @Override
    public EmbeddingConfigVO getEmbeddingConfig() {
        String cached = stringRedisTemplate.opsForValue().get(AiRedisKeyEnum.EMBEDDING_CONFIG.getCode());
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, EmbeddingConfigVO.class);
            } catch (JacksonException e) {
                log.warn("解析Embedding配置缓存失败，重新获取: {}", e.getMessage());
            }
        }

        EmbeddingConfigVO config = loadEmbeddingConfigFromDb();

        try {
            stringRedisTemplate.opsForValue().set(AiRedisKeyEnum.EMBEDDING_CONFIG.getCode(),
                    objectMapper.writeValueAsString(config),
                    AiCacheExpireEnum.DEFAULT_CACHE_HOURS.getValue() + (long) (Math.random() * 10), TimeUnit.MINUTES);
        } catch (JacksonException e) {
            log.error("写入Embedding配置缓存失败: {}", e.getMessage());
        }

        return config;
    }

    /**
     * 从数据库加载Embedding配置
     *
     * @return Embedding配置
     */
    private EmbeddingConfigVO loadEmbeddingConfigFromDb() {
        AiProvider provider = aiProviderMapper.selectDefaultByType(AiProviderTypeEnum.EMBEDDING.getCode());
        if (provider == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "未配置默认的Embedding厂商");
        }

        AiProviderEndpoint endpoint = aiProviderEndpointMapper.selectOneByProviderIdAndType(
                provider.getId(), AiEndpointTypeEnum.MODEL_API.getCode());
        if (endpoint == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "Embedding厂商未配置model_api端点");
        }

        AiModel aiModel = aiModelMapper.selectDefaultByTypeAndProviderId(
                AiProviderTypeEnum.EMBEDDING.getCode(), provider.getId());
        if (aiModel == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "默认Embedding厂商下未配置默认模型");
        }

        String fullUrl = buildFullUrl(provider.getApiEndpoint(), endpoint.getEndpointUri());

        EmbeddingConfigVO config = new EmbeddingConfigVO();
        config.setFullUrl(fullUrl);
        config.setModel(aiModel.getModelCode());
        config.setModelName(aiModel.getModelName());
        config.setDimensions(aiModel.getMaxTokens()); // dimensions暂存在maxTokens兼容
        config.setEncryptedApiKey(AesEncryptionUtil.decrypt(provider.getApiKey()));
        config.setProviderId(provider.getId());
        config.setVendorCode(provider.getProviderCode());
        return config;
    }

    /**
     * 获取场景绑定的候选模型列表（含并发信息）
     *
     * @param sceneCode 场景编码
     * @return 候选模型列表
     */
    @Override
    public List<ModelCandidate> getSceneCandidates(String sceneCode) {
        if (sceneCode == null) {
            return Collections.emptyList();
        }
        AiSceneConfig sceneConfig = aiSceneConfigMapper.selectBySceneCode(sceneCode);
        if (sceneConfig == null) {
            return Collections.emptyList();
        }
        List<AiSceneModel> sceneModels = aiSceneModelMapper.selectBySceneConfigId(sceneConfig.getId());
        if (sceneModels == null || sceneModels.isEmpty()) {
            return Collections.emptyList();
        }
        return buildCandidates(sceneModels);
    }

    // ========== 提示词模板 ==========

    /**
     * 获取聊天系统提示词
     *
     * @return 系统提示词内容
     */
    @Override
    public String getChatSystemPrompt() { return getTemplateContentByCode("chat_system"); }

    /**
     * 获取上下文构建提示词
     *
     * @return 上下文构建提示词内容
     */
    @Override
    public String getContextBuilderPrompt() { return getTemplateContentByCode("context_builder"); }

    /**
     * 获取全局上下文提示词
     *
     * @return 全局上下文提示词内容
     */
    @Override
    public String getGlobalContextPrompt() { return getTemplateContentByCode("global_context"); }

    /**
     * 获取体质分析系统提示词
     *
     * @return 体质分析提示词内容
     */
    @Override
    public String getConstitutionSystemPrompt() { return getTemplateContentByCode("constitution_system"); }

    /**
     * 获取方案生成提示词
     *
     * @return 方案生成提示词内容
     */
    @Override
    public String getPlanGenerationPrompt() { return getTemplateContentByCode("plan_generation"); }

    /**
     * 获取周期报告提示词
     *
     * @return 周期报告提示词内容
     */
    @Override
    public String getCycleReportPrompt() { return getTemplateContentByCode("cycle_report"); }

    /**
     * 根据模板编码获取模板内容（缓存优先）
     *
     * @param templateCode 模板编码
     * @return 模板内容
     */
    private String getTemplateContentByCode(String templateCode) {
        String cacheKey = AiRedisKeyEnum.TEMPLATE_PREFIX.getCode() + templateCode;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        AiPromptTemplate template = aiPromptTemplateMapper.selectLatestByCode(templateCode);
        if (template == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "未找到提示词模板: " + templateCode);
        }
        if (template.getIsEnabled() != 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "提示词模板未启用: " + templateCode);
        }

        String content = template.getTemplateContent();
        if (content == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "提示词模板内容为空: " + templateCode);
        }

        stringRedisTemplate.opsForValue().set(cacheKey, content,
                AiCacheExpireEnum.DEFAULT_CACHE_HOURS.getValue() + (long) (Math.random() * 10), TimeUnit.MINUTES);
        return content;
    }

    /**
     * 渲染提示词模板（替换变量）
     *
     * @param templateCode 模板编码
     * @param variables    模板变量
     * @return 渲染后的文本
     */
    @Override
    public String renderPrompt(String templateCode, Map<String, Object> variables) {
        String template = getTemplateContentByCode(templateCode);
        if (template == null || template.isEmpty()) return template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            template = template.replace("${" + entry.getKey() + "}",
                    entry.getValue() != null ? entry.getValue().toString() : "");
        }
        return template;
    }

    // ========== 业务参数（场景驱动）==========

    /**
     * 获取禁止分类列表（缓存优先）
     *
     * @return 禁止分类列表
     */
    @Override
    public List<String> getForbiddenCategories() {
        String cacheKey = AiRedisKeyEnum.FORBIDDEN_CATEGORIES.getCode();
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<String>>() {});
            } catch (JacksonException e) {
                log.warn("解析禁止分类缓存失败，重新获取: {}", e.getMessage());
            }
        }

        AiPromptTemplate template = aiPromptTemplateMapper.selectLatestByCode("forbidden_categories");
        if (template == null || template.getIsEnabled() != 1 || template.getTemplateContent() == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "未找到或未启用禁止分类模板");
        }

        List<String> categories;
        try {
            categories = objectMapper.readValue(template.getTemplateContent(), new TypeReference<List<String>>() {});
        } catch (JacksonException e) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "禁止分类模板内容格式错误");
        }

        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(categories),
                    AiCacheExpireEnum.DEFAULT_CACHE_HOURS.getValue() + (long) (Math.random() * 10), TimeUnit.MINUTES);
        } catch (JacksonException e) {
            log.error("写入禁止分类缓存失败: {}", e.getMessage());
        }
        return categories;
    }

    /**
     * 获取场景的相似度阈值
     *
     * @param sceneCode 场景编码
     * @return 相似度阈值
     */
    @Override
    public Double getSimilarityThreshold(String sceneCode) {
        AiSceneConfig config = getSceneConfigOrThrow(sceneCode);
        return config.getSimilarityThreshold();
    }

    /**
     * 获取场景的知识库检索TopK
     *
     * @param sceneCode 场景编码
     * @return TopK值
     */
    @Override
    public Integer getKnowledgeTopK(String sceneCode) {
        AiSceneConfig config = getSceneConfigOrThrow(sceneCode);
        return config.getKnowledgeTopK();
    }

    /**
     * 获取场景的历史消息限制数
     *
     * @param sceneCode 场景编码
     * @return 历史限制数
     */
    @Override
    public Integer getHistoryLimit(String sceneCode) {
        AiSceneConfig config = getSceneConfigOrThrow(sceneCode);
        return config.getHistoryLimit();
    }

    /**
     * 获取场景配置，不存在时抛异常
     *
     * @param sceneCode 场景编码
     * @return 场景配置
     */
    private AiSceneConfig getSceneConfigOrThrow(String sceneCode) {
        String code = sceneCode != null ? sceneCode : "ai_chat";
        AiSceneConfig config = aiSceneConfigMapper.selectBySceneCode(code);
        if (config == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "未找到场景配置: " + code);
        }
        return config;
    }

    // ========== 缓存管理 ==========

    /**
     * 清除Chat配置缓存
     *
     * @param sceneCode 场景编码
     */
    @Override
    public void evictChatConfig(String sceneCode) {
        String cacheKey = AiRedisKeyEnum.CHAT_CONFIG_PREFIX.getCode() + (sceneCode != null ? sceneCode : "default");
        stringRedisTemplate.delete(cacheKey);
        log.info("已清除Chat配置缓存: {}", cacheKey);
    }

    /**
     * 清除Embedding配置缓存
     */
    @Override
    public void evictEmbeddingConfig() {
        stringRedisTemplate.delete(AiRedisKeyEnum.EMBEDDING_CONFIG.getCode());
        log.info("已清除Embedding配置缓存");
    }

    /**
     * 清除提示词模板缓存
     *
     * @param templateCode 模板编码
     */
    @Override
    public void evictTemplate(String templateCode) {
        stringRedisTemplate.delete(AiRedisKeyEnum.TEMPLATE_PREFIX.getCode() + templateCode);
        log.info("已清除提示词模板缓存: {}", templateCode);
    }

    /**
     * 清除场景配置缓存
     *
     * @param sceneCode 场景编码
     */
    @Override
    public void evictSceneConfig(String sceneCode) {
        evictChatConfig(sceneCode);
        log.info("已清除场景配置缓存: {}", sceneCode);
    }

    /**
     * 清除所有AI配置缓存
     */
    @Override
    public void evictAllConfigCache() {
        try {
            String chatPattern = AiRedisKeyEnum.CHAT_CONFIG_PREFIX.getCode() + "*";
            stringRedisTemplate.delete(stringRedisTemplate.keys(chatPattern));
            stringRedisTemplate.delete(AiRedisKeyEnum.EMBEDDING_CONFIG.getCode());
            stringRedisTemplate.delete(AiRedisKeyEnum.FORBIDDEN_CATEGORIES.getCode());
            String templatePattern = AiRedisKeyEnum.TEMPLATE_PREFIX.getCode() + "*";
            stringRedisTemplate.delete(stringRedisTemplate.keys(templatePattern));
            log.info("已清除所有AI配置缓存");
        } catch (Exception e) {
            log.error("清除所有AI配置缓存失败: {}", e.getMessage());
        }
    }

    // ========== 工具方法 ==========

    /**
     * 构建完整API地址（拼接baseUrl和uri）
     *
     * @param baseUrl 基础地址
     * @param uri     接口路径
     * @return 完整URL
     */
    private String buildFullUrl(String baseUrl, String uri) {
        if (baseUrl == null || uri == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "API endpoint或uri为空");
        }
        baseUrl = baseUrl.replaceAll("/+$", "");
        uri = uri.replaceAll("^/+", "");
        return baseUrl + "/" + uri;
    }
}
