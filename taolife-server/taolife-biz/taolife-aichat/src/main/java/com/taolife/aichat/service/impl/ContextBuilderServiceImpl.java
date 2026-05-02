package com.taolife.aichat.service.impl;

import com.taolife.aichat.entity.ChatMessage;
import com.taolife.aichat.entity.ChatSession;
import com.taolife.aichat.entity.HealthDocument;
import com.taolife.aichat.enums.RoleEnum;
import com.taolife.aichat.mapper.ChatMessageMapper;
import com.taolife.aichat.mapper.ChatSessionMapper;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.identity.entity.UserPreference;
import com.taolife.identity.mapper.UserPreferenceMapper;
import com.taolife.wisdom.service.IFitnessService;
import com.taolife.aichat.service.IContextBuilderService;
import com.taolife.aichat.service.IDocSearchService;
import com.taolife.wisdom.vo.FitnessResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 上下文构建服务实现
 * 支持会话隔离：每个会话独立上下文，不串上下文
 *
 * @author 文二
 * @date 2026-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContextBuilderServiceImpl implements IContextBuilderService {

    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final IDocSearchService docSearchService;
    private final IFitnessService constitutionService;
    private final IAiConfigService aiConfigService;
    private final UserPreferenceMapper userPreferenceMapper;

    /**
     * 构建对话上下文
     * 包含会话信息、用户体质信息、用户偏好摘要、相关知识检索和会话历史，共同组成完整的Prompt上下文
     *
     * @param sessionId 会话ID
     * @param query 用户问题
     * @return 构建完成的Prompt上下文对象
     */
    @Override
    public PromptContext buildContext(String sessionId, String query) {
        log.info("开始构建对话上下文：会话ID: {}, 用户问题: {}", sessionId, query);
        
        PromptContext context = new PromptContext();

        // 1. 获取会话信息
        log.info("步骤1: 获取会话信息");
        ChatSession session = chatSessionMapper.selectById(sessionId);
        log.info("会话信息获取完成，会话标题: {}", session != null ? session.getSessionTitle() : "null");

        // 2. 获取用户体质信息（如果关联了体质记录）
        log.info("步骤2: 获取用户体质信息");
        if (session != null && session.getConstitutionId() != null) {
            // 根据体质记录ID查询体质信息
            FitnessResultVO constitutionResult = constitutionService.getAssessmentResult(session.getConstitutionId());
            if (constitutionResult != null) {
                context.setUserConstitution("用户体质：" + constitutionResult.getConstitutionName() 
                    + "（得分：" + constitutionResult.getScore() + "分）");
            } else {
                context.setUserConstitution("用户体质：未知");
            }
        } else {
            // 尝试获取用户最新测评结果
            FitnessResultVO latestResult = constitutionService.getLatestResult(session.getAccountId());
            if (latestResult != null) {
                context.setUserConstitution("用户体质：" + latestResult.getConstitutionName() 
                    + "（得分：" + latestResult.getScore() + "分）");
            } else {
                context.setUserConstitution("用户尚未进行体质测评");
            }
        }
        log.info("用户体质信息: {}", context.getUserConstitution());

        // 2.5 获取用户偏好摘要
        if (session != null) {
            try {
                UserPreference pref = userPreferenceMapper.selectByAccountId(session.getAccountId());
                if (pref != null) {
                    context.setUserPreferenceSummary(buildPreferenceSummary(pref));
                }
            } catch (Exception e) {
                log.warn("获取用户偏好失败, accountId: {}", session.getAccountId(), e);
            }
        }
        if (context.getUserPreferenceSummary() == null) {
            context.setUserPreferenceSummary("");
        }

        // 3. 检索相关知识
        int knowledgeTopK = aiConfigService.getKnowledgeTopK();
        log.info("步骤3: 检索相关知识，topK: {}", knowledgeTopK);
        List<HealthDocument> knowledgeDocs =
            docSearchService.retrieve(query, knowledgeTopK);
        log.info("知识检索完成，检索到 {} 条相关知识", knowledgeDocs != null ? knowledgeDocs.size() : 0);
        context.setKnowledgeContext(formatKnowledge(knowledgeDocs));

        // 4. 获取会话历史（隔离上下文）
        int historyLimit = aiConfigService.getHistoryLimit();
        log.info("步骤4: 获取会话历史，限制条数: {}", historyLimit);
        List<ConversationMessage> history = getConversationHistory(sessionId, historyLimit);
        context.setConversationHistory(formatHistory(history));
        log.info("会话历史获取完成，历史消息数: {}", history != null ? history.size() : 0);

        // 5. 构建完整Prompt
        log.info("步骤5: 构建完整Prompt");
        context.setFullPrompt(buildPrompt(context, query));
        log.info("Prompt构建完成，长度: {} 字符", context.getFullPrompt() != null ? context.getFullPrompt().length() : 0);
        log.info("对话上下文构建完成");

        return context;
    }

    /**
     * 获取会话历史消息
     * 只获取当前会话的消息记录，确保会话间上下文隔离
     *
     * @param sessionId 会话ID
     * @param limit 返回消息数量的上限
     * @return 会话历史消息列表
     */
    @Override
    public List<ConversationMessage> getConversationHistory(String sessionId, int limit) {
        // 只获取当前会话的消息，确保会话隔离
        List<ChatMessage> messages = chatMessageMapper.selectBySessionId(sessionId);

        // 转换并限制数量
        return messages.stream()
            .map(this::convertToConversationMessage)
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 格式化知识库内容
     */
    private String formatKnowledge(List<HealthDocument> docs) {
        if (docs == null || docs.isEmpty()) {
            return "未找到相关知识库内容";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【相关知识库内容】\n");
        for (int i = 0; i < docs.size(); i++) {
            HealthDocument doc = docs.get(i);
            sb.append(i + 1).append(". ").append(doc.getTitle()).append("\n");
            sb.append("   ").append(doc.getContent()).append("\n");
        }
        return sb.toString();
    }

    /**
     * 格式化历史对话
     */
    private String formatHistory(List<ConversationMessage> history) {
        if (history == null || history.isEmpty()) {
            return "（无历史对话）";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【会话历史】\n");
        for (ConversationMessage msg : history) {
            String roleName = "user".equals(msg.getRole()) ? "用户" : "助手";
            sb.append(roleName).append("：").append(msg.getContent()).append("\n");
        }
        return sb.toString();
    }

    /**
     * 构建完整Prompt
     */
    private String buildPrompt(PromptContext context, String query) {
        // 渲染变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("userConstitution", context.getUserConstitution());
        variables.put("userPreferenceSummary", context.getUserPreferenceSummary());
        variables.put("knowledgeContext", context.getKnowledgeContext());
        variables.put("conversationHistory", context.getConversationHistory());
        variables.put("query", query);

        return aiConfigService.renderPrompt("global_context", variables);
    }

    /**
     * 转换为对话消息
     */
    private ConversationMessage convertToConversationMessage(ChatMessage message) {
        ConversationMessage msg = new ConversationMessage();
        msg.setRole(RoleEnum.getByValue(message.getRole()).getCode());
        msg.setContent(message.getContent());
        if (message.getCreateTime() != null) {
            msg.setTime(message.getCreateTime().toString());
        }
        return msg;
    }

    /**
     * 构建用户偏好摘要文本
     */
    private String buildPreferenceSummary(UserPreference p) {
        StringBuilder sb = new StringBuilder();

        // 身体数据
        if (p.getHeight() != null) sb.append("身高").append(p.getHeight()).append("cm；");
        if (p.getWeight() != null) sb.append("体重").append(p.getWeight()).append("kg；");
        if (p.getAllergyHistory() != null && !p.getAllergyHistory().isEmpty())
            sb.append("过敏史：").append(p.getAllergyHistory()).append("；");
        if (p.getMedicalHistory() != null && !p.getMedicalHistory().isEmpty())
            sb.append("病史：").append(p.getMedicalHistory()).append("；");

        // 饮食偏好
        if (p.getPreferredFoodTexture() != null) {
            String[] t = {"", "清淡", "浓郁", "辛辣", "偏甜"};
            if (p.getPreferredFoodTexture() >= 1 && p.getPreferredFoodTexture() <= 4)
                sb.append("口味偏好：").append(t[p.getPreferredFoodTexture()]).append("；");
        }
        if (p.getDietaryGoal() != null && !p.getDietaryGoal().isEmpty())
            sb.append("饮食目标：").append(p.getDietaryGoal()).append("；");

        // 运动偏好
        if (p.getPreferredExerciseType() != null) {
            String[] t = {"", "有氧", "力量", "柔韧", "球类", "传统功法"};
            if (p.getPreferredExerciseType() >= 1 && p.getPreferredExerciseType() <= 5)
                sb.append("偏好运动：").append(t[p.getPreferredExerciseType()]).append("；");
        }

        // 生活习惯
        if (p.getSleepTime() != null && p.getWakeTime() != null)
            sb.append("作息：").append(p.getSleepTime()).append("-").append(p.getWakeTime()).append("；");
        if (p.getStressLevel() != null) {
            String[] s = {"", "低", "中", "高"};
            if (p.getStressLevel() >= 1 && p.getStressLevel() <= 3)
                sb.append("压力：").append(s[p.getStressLevel()]).append("；");
        }

        // 健康目标
        if (p.getHealthGoalPrimary() != null) {
            String[] g = {"", "增强免疫", "改善睡眠", "体重管理", "缓解压力", "改善消化", "增强体力"};
            if (p.getHealthGoalPrimary() >= 1 && p.getHealthGoalPrimary() <= 6)
                sb.append("主要目标：").append(g[p.getHealthGoalPrimary()]).append("；");
        }

        // AI偏好
        if (p.getAiTonePreference() != null) {
            String[] tones = {"", "专业", "亲切", "简洁"};
            if (p.getAiTonePreference() >= 1 && p.getAiTonePreference() <= 3)
                sb.append("回复风格：").append(tones[p.getAiTonePreference()]).append("；");
        }

        return sb.length() > 0 ? "【用户偏好】" + sb : "";
    }
}