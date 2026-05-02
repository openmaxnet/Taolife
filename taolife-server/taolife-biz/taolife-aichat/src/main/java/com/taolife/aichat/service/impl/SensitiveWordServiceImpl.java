package com.taolife.aichat.service.impl;

import com.taolife.aichat.entity.SensitiveWord;
import com.taolife.aichat.enums.SensitiveWordTypeEnum;
import com.taolife.aichat.mapper.SensitiveWordMapper;
import com.taolife.aichat.service.ISensitiveWordService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感词检测服务实现
 * 启动时加载AC自动机到内存，支持分级处理
 *
 * @author 文二
 * @date 2026-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SensitiveWordServiceImpl implements ISensitiveWordService {

    private final SensitiveWordMapper sensitiveWordMapper;

    /**
     * 敏感词库缓存（内存）
     */
    private volatile Map<String, SensitiveWord> sensitiveWordCache = new ConcurrentHashMap<>();

    /**
     * 敏感词模式缓存
     */
    private volatile Pattern sensitiveWordPattern;

    /**
     * 初始化敏感词库
     */
    @Override
    @PostConstruct
    public void initSensitiveWords() {
        log.info("开始初始化敏感词库...");
        List<SensitiveWord> words = sensitiveWordMapper.selectAllEnabled();

        // 构建敏感词缓存
        Map<String, SensitiveWord> cache = new ConcurrentHashMap<>();
        for (SensitiveWord word : words) {
            cache.put(word.getWord(), word);
        }

        // 构建正则表达式模式
        StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append("(");
        for (String word : cache.keySet()) {
            if (patternBuilder.length() > 1) {
                patternBuilder.append("|");
            }
            // 转义特殊字符
            patternBuilder.append(escapeRegex(word));
        }
        patternBuilder.append(")");

        try {
            sensitiveWordPattern = Pattern.compile(patternBuilder.toString());
        } catch (Exception e) {
            log.error("构建敏感词正则表达式失败", e);
            sensitiveWordPattern = null;
        }

        sensitiveWordCache = cache;
        log.info("敏感词库初始化完成，共加载 {} 个敏感词", cache.size());
    }

    /**
     * 敏感词检测
     * 使用内存中的AC自动机模式对输入文本进行敏感词匹配，返回匹配结果及严重程度
     *
     * @param text 待检测的文本
     * @return 敏感词检测结果（含是否命中、匹配词列表、严重程度和处理方式）
     */
    @Override
    public SensitiveWordResult detect(String text) {
        SensitiveWordResult result = new SensitiveWordResult();

        if (text == null || text.isEmpty() || sensitiveWordPattern == null) {
            result.setHasSensitiveWord(false);
            result.setMatchedWords(List.of());
            result.setSeverity(0);
            result.setActionType(0);
            return result;
        }

        // 匹配敏感词
        Matcher matcher = sensitiveWordPattern.matcher(text);
        List<MatchedWord> matchedWords = new java.util.ArrayList<>();

        while (matcher.find()) {
            String matchedText = matcher.group();
            SensitiveWord sensitiveWord = sensitiveWordCache.get(matchedText);

            if (sensitiveWord != null) {
                MatchedWord matchedWord = new MatchedWord();
                matchedWord.setWord(sensitiveWord.getWord());
                matchedWord.setWordType(sensitiveWord.getWordType());
                matchedWord.setSeverity(sensitiveWord.getSeverity());
                matchedWord.setStartIndex(matcher.start());
                matchedWord.setEndIndex(matcher.end());
                matchedWords.add(matchedWord);
            }
        }

        result.setHasSensitiveWord(!matchedWords.isEmpty());
        result.setMatchedWords(matchedWords);

        // 计算严重程度（取最大值）
        int maxSeverity = matchedWords.stream()
            .mapToInt(MatchedWord::getSeverity)
            .max()
            .orElse(0);
        result.setSeverity(maxSeverity);

        // 计算处理方式
        int actionType = matchedWords.stream()
            .mapToInt(MatchedWord::getSeverity)
            .max()
            .orElse(1);
        // 严重程度1-低->警告(3)，2-中->替换(2)，3-高->拒绝(1)
        result.setActionType(4 - actionType);

        return result;
    }

    /**
     * 带上下文的敏感词检测
     * 在基础检测基础上，对医疗诊断类敏感词进行上下文分析，减少误报
     *
     * @param text 待检测的文本
     * @param context 上下文信息（可为null）
     * @return 敏感词检测结果（含上下文误报修正）
     */
    @Override
    public SensitiveWordResult detectWithContext(String text, String context) {
        // 基础检测
        SensitiveWordResult baseResult = detect(text);

        if (!baseResult.hasSensitiveWord()) {
            return baseResult;
        }

        // 上下文分析（简化实现）
        for (MatchedWord matched : baseResult.getMatchedWords()) {
            // 检查是否在医疗上下文中使用敏感词
            if (matched.getWordType() == SensitiveWordTypeEnum.MEDICAL_DIAGNOSIS.getValue()) {
                // 简单的上下文判断：如果包含"什么病"、"确诊"等词汇，可能是医疗诊断
                if (containsDiagnosisContext(text)) {
                    // 保持敏感
                } else {
                    // 可能是误报
                    matched.setFalsePositive(true);
                }
            }
        }

        return baseResult;
    }

    /**
     * 检查是否包含诊断上下文
     * 通过诊断相关关键词（如"什么病"、"确诊"等）判断文本是否涉及医疗诊断场景
     *
     * @param text 待检查的文本
     * @return 是否包含诊断上下文
     */
    private boolean containsDiagnosisContext(String text) {
        String[] diagnosisKeywords = {"什么病", "得了", "确诊", "患病", "病症", "检查", "治疗"};
        for (String keyword : diagnosisKeywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转义正则表达式特殊字符
     * 将文本中的正则特殊字符（. * + ? [ ] ( ) { } | ^ $ 等）进行转义处理
     *
     * @param text 原始文本
     * @return 转义后的安全正则表达式文本
     */
    private String escapeRegex(String text) {
        return text.replace("\\", "\\\\")
            .replace(".", "\\.")
            .replace("*", "\\*")
            .replace("+", "\\+")
            .replace("?", "\\?")
            .replace("[", "\\[")
            .replace("]", "\\]")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace("|", "\\|")
            .replace("^", "\\^")
            .replace("$", "\\$");
    }
}