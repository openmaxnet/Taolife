package com.taolife.aichat.service;

import com.taolife.aicore.balance.ModelCandidate;
import com.taolife.aicore.config.ChatConfigVO;
import com.taolife.aicore.config.EmbeddingConfigVO;

import java.util.List;
import java.util.Map;

/**
 * AI统一配置服务接口
 * 场景驱动的配置解析，支持多模型负载均衡
 */
public interface IAiConfigService {

    // ========== 场景配置（核心）==========

    /**
     * 根据场景编码获取Chat配置（含负载均衡选择）
     * 1. 查找场景绑定的模型列表
     * 2. 多模型时通过 ModelSelector 选择
     * 3. 单模型时直接返回
     * 4. 无场景配置时降级为默认provider+默认model
     *
     * @param sceneCode 场景编码
     * @return Chat配置（包含 fullUrl、vendorCode、model 等）
     */
    ChatConfigVO getChatConfig(String sceneCode);

    /**
     * 获取Embedding配置
     *
     * @return Embedding配置
     */
    EmbeddingConfigVO getEmbeddingConfig();

    /**
     * 获取场景绑定的候选模型列表（含并发信息）
     *
     * @param sceneCode 场景编码
     * @return 候选模型列表
     */
    List<ModelCandidate> getSceneCandidates(String sceneCode);

    // ========== 提示词模板 ==========

    /**
     * 获取聊天系统提示词
     *
     * @return 系统提示词内容
     */
    String getChatSystemPrompt();

    /**
     * 获取上下文构建提示词
     *
     * @return 上下文构建提示词内容
     */
    String getContextBuilderPrompt();

    /**
     * 获取全局上下文提示词
     *
     * @return 全局上下文提示词内容
     */
    String getGlobalContextPrompt();

    /**
     * 获取体质分析系统提示词
     *
     * @return 体质分析提示词内容
     */
    String getConstitutionSystemPrompt();

    /**
     * 获取方案生成提示词
     *
     * @return 方案生成提示词内容
     */
    String getPlanGenerationPrompt();

    /**
     * 获取周期报告提示词
     *
     * @return 周期报告提示词内容
     */
    String getCycleReportPrompt();

    /**
     * 渲染提示词模板
     * 将模板中的 ${key} 替换为对应的变量值
     *
     * @param templateCode 模板编码
     * @param variables    模板变量
     * @return 渲染后的文本
     */
    String renderPrompt(String templateCode, Map<String, Object> variables);

    // ========== 业务参数 ==========

    /**
     * 获取禁止分类列表
     *
     * @return 禁止分类列表
     */
    List<String> getForbiddenCategories();

    /**
     * 获取相似度阈值
     *
     * @param sceneCode 场景编码
     * @return 相似度阈值
     */
    Double getSimilarityThreshold(String sceneCode);

    /**
     * 获取知识库检索TopK
     *
     * @param sceneCode 场景编码
     * @return TopK值
     */
    Integer getKnowledgeTopK(String sceneCode);

    /**
     * 获取历史消息限制数
     *
     * @param sceneCode 场景编码
     * @return 历史限制数
     */
    Integer getHistoryLimit(String sceneCode);

    // ========== 兼容旧接口（无场景参数时使用默认场景） ==========

    /**
     * 获取默认场景的Chat配置
     *
     * @return Chat配置
     */
    default ChatConfigVO getChatConfig() {
        return getChatConfig(null);
    }

    /**
     * 获取默认场景的相似度阈值
     *
     * @return 相似度阈值
     */
    default Double getSimilarityThreshold() {
        return getSimilarityThreshold(null);
    }

    /**
     * 获取默认场景的知识库检索TopK
     *
     * @return TopK值
     */
    default Integer getKnowledgeTopK() {
        return getKnowledgeTopK(null);
    }

    /**
     * 获取默认场景的历史消息限制数
     *
     * @return 历史限制数
     */
    default Integer getHistoryLimit() {
        return getHistoryLimit(null);
    }

    // ========== 缓存管理 ==========

    /**
     * 清除Chat配置缓存
     *
     * @param sceneCode 场景编码（null时清除默认场景）
     */
    void evictChatConfig(String sceneCode);

    /**
     * 清除Embedding配置缓存
     */
    void evictEmbeddingConfig();

    /**
     * 清除提示词模板缓存
     *
     * @param templateCode 模板编码
     */
    void evictTemplate(String templateCode);

    /**
     * 清除场景配置缓存
     *
     * @param sceneCode 场景编码
     */
    void evictSceneConfig(String sceneCode);

    /**
     * 清除所有AI配置缓存
     */
    void evictAllConfigCache();
}
