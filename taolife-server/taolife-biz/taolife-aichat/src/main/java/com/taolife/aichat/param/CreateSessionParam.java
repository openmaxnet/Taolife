package com.taolife.aichat.param;

import lombok.Data;

/**
 * 创建会话请求参数
 * 用于创建新的AI问答会话
 *
 * @author 文二
 * @date 2026-03-26
 */
@Data
public class CreateSessionParam {

    /**
     * 关联的体质记录ID（可选）
     * 用于关联用户的体质评估结果，提供个性化建议
     */
    private String constitutionId;

    /**
     * 聊天模型（可选，默认 glm-flash）
     * 支持的模型：glm-flash、glm-4
     */
    private String chatModel;

    /**
     * 向量模型（可选，默认 qwen-embedding）
     * 支持的模型：qwen-embedding、bge-m3
     */
    private String embeddingModel;
}
