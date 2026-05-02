package com.taolife.aichat.service;

import java.util.List;

/**
 * 上下文构建服务接口
 * 支持会话隔离：每个会话独立上下文，不串上下文
 *
 * @author 文二
 * @date 2026-03-26
 */
public interface IContextBuilderService {

    /**
     * 构建Prompt上下文
     *
     * @param sessionId 会话ID（用于隔离上下文）
     * @param query 用户问题
     * @return Prompt上下文
     */
    PromptContext buildContext(String sessionId, String query);

    /**
     * 获取当前会话的对话历史（隔离上下文）
     *
     * @param sessionId 会话ID
     * @param limit 历史消息数量
     * @return 历史消息列表
     */
    List<ConversationMessage> getConversationHistory(String sessionId, int limit);

    /**
     * Prompt上下文
     */
    class PromptContext {
        /** 用户体质信息 */
        private String userConstitution;

        /** 用户偏好摘要 */
        private String userPreferenceSummary;

        /** 知识库内容 */
        private String knowledgeContext;

        /** 会话历史 */
        private String conversationHistory;

        /** 完整Prompt */
        private String fullPrompt;

        public String getUserConstitution() { return userConstitution; }
        public void setUserConstitution(String userConstitution) { this.userConstitution = userConstitution; }
        public String getUserPreferenceSummary() { return userPreferenceSummary; }
        public void setUserPreferenceSummary(String userPreferenceSummary) { this.userPreferenceSummary = userPreferenceSummary; }
        public String getKnowledgeContext() { return knowledgeContext; }
        public void setKnowledgeContext(String knowledgeContext) { this.knowledgeContext = knowledgeContext; }
        public String getConversationHistory() { return conversationHistory; }
        public void setConversationHistory(String conversationHistory) { this.conversationHistory = conversationHistory; }
        public String getFullPrompt() { return fullPrompt; }
        public void setFullPrompt(String fullPrompt) { this.fullPrompt = fullPrompt; }
    }

    /**
     * 对话消息
     */
    class ConversationMessage {
        /**
         * 角色：user/assistant
         */
        private String role;

        /**
         * 内容
         */
        private String content;

        /**
         * 时间
         */
        private String time;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}