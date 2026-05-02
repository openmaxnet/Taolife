package com.taolife.aichat.service.impl;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.taolife.aichat.entity.ChatRule;
import com.taolife.aichat.enums.QuestionTypeEnum;
import com.taolife.aichat.mapper.ChatRuleMapper;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aichat.service.IQuestionClassificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 问题分类服务实现
 *
 * @author 文二
 * @date 2026-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionClassificationServiceImpl implements IQuestionClassificationService {

    private final ChatRuleMapper questionCategoryRuleMapper;
    private final IAiConfigService aiConfigService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 问题分类
     * 根据启用的分类规则匹配输入问题，计算置信度，并检查是否在禁止分类范围内
     *
     * @param question 用户提问文本
     * @return 分类结果（包含分类编码、名称、问题类型、置信度和安全性判定）
     */
    @Override
    public ClassificationResult classify(String question) {
        ClassificationResult result = new ClassificationResult();

        // 获取所有分类规则
        List<ChatRule> rules = questionCategoryRuleMapper.selectAllEnabled();

        // 遍历规则进行匹配
        ChatRule matchedRule = null;
        int maxMatchCount = 0;

        for (ChatRule rule : rules) {
            int matchCount = countKeywordMatches(question, rule.getKeywords());
            if (matchCount > maxMatchCount) {
                maxMatchCount = matchCount;
                matchedRule = rule;
            }
        }

        if (matchedRule != null) {
            result.setCategoryCode(matchedRule.getCategoryCode());
            result.setCategoryName(matchedRule.getCategoryName());
            result.setQuestionType(mapCategoryToQuestionType(matchedRule.getCategoryCode()));
            result.setConfidence(Math.min(1.0, maxMatchCount * 0.3));
        } else {
            // 默认分类为其他
            result.setCategoryCode("other");
            result.setCategoryName("其他");
            result.setQuestionType(QuestionTypeEnum.OTHER.getValue());
            result.setConfidence(0.5);
        }

        // 检查是否在允许的范围内（从配置读取禁止分类列表）
        result.setAllowed(!isForbiddenCategory(result.getCategoryCode()));

        if (!result.isAllowed()) {
            result.setRejectReason("您的问题超出了我们的服务范围，建议咨询专业医师");
        }

        return result;
    }

    /**
     * 获取所有启用的分类规则
     *
     * @return 分类规则列表
     */
    @Override
    public List<ChatRule> getAllRules() {
        return questionCategoryRuleMapper.selectAllEnabled();
    }

    /**
     * 统计关键词匹配次数
     * 解析JSON格式的关键词列表，统计在问题文本中匹配到的关键词数量
     *
     * @param question 用户提问文本
     * @param keywordsJson 关键词JSON数组字符串
     * @return 匹配到的关键词数量
     */
    private int countKeywordMatches(String question, String keywordsJson) {
        try {
            List<String> keywords = objectMapper.readValue(keywordsJson, new TypeReference<List<String>>() {});

            int count = 0;
            for (String keyword : keywords) {
                // 忽略大小写检查
                if (question.toLowerCase().contains(keyword.toLowerCase())) {
                    count++;
                }
            }

            return count;
        } catch (Exception e) {
            log.error("解析关键词失败", e);
            return 0;
        }
    }

    /**
     * 检查是否为禁止的分类
     * 从AI配置中读取禁止分类列表，判断给定分类编码是否被禁止
     *
     * @param categoryCode 分类编码
     * @return 是否为禁止分类
     */
    private boolean isForbiddenCategory(String categoryCode) {
        List<String> forbiddenCategories = aiConfigService.getForbiddenCategories();
        return forbiddenCategories.contains(categoryCode);
    }

    /**
     * 将分类编码映射到问题类型
     * 根据分类编码前缀（constitution/food/acupoint/health）映射为对应的问题类型枚举值
     *
     * @param categoryCode 分类编码
     * @return 问题类型枚举值
     */
    private Integer mapCategoryToQuestionType(String categoryCode) {
        if (categoryCode == null) {
            return QuestionTypeEnum.OTHER.getValue();
        }

        if (categoryCode.startsWith("constitution")) {
            return QuestionTypeEnum.CONSTITUTION.getValue();
        } else if (categoryCode.startsWith("food")) {
            return QuestionTypeEnum.FOOD_THERAPY.getValue();
        } else if (categoryCode.startsWith("acupoint")) {
            return QuestionTypeEnum.ACUPOINT.getValue();
        } else if (categoryCode.startsWith("health")) {
            return QuestionTypeEnum.HEALTH.getValue();
        } else {
            return QuestionTypeEnum.OTHER.getValue();
        }
    }
}