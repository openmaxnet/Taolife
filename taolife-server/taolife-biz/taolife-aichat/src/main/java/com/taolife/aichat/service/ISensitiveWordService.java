package com.taolife.aichat.service;

import java.util.List;

/**
 * 敏感词检测服务接口
 *
 * @author 文二
 * @date 2026-03-26
 */
public interface ISensitiveWordService {

    /**
     * 检测文本中的敏感词
     *
     * @param text 待检测文本
     * @return 检测结果
     */
    SensitiveWordResult detect(String text);

    /**
     * 上下文感知的敏感词检测
     *
     * @param text 待检测文本
     * @param context 上下文
     * @return 检测结果
     */
    SensitiveWordResult detectWithContext(String text, String context);

    /**
     * 初始化敏感词库（启动时加载到内存）
     */
    void initSensitiveWords();

    /**
     * 敏感词检测结果
     */
    class SensitiveWordResult {
        /**
         * 是否包含敏感词
         */
        private boolean hasSensitiveWord;

        /**
         * 匹配的敏感词列表
         */
        private List<MatchedWord> matchedWords;

        /**
         * 严重程度：1-低，2-中，3-高
         */
        private int severity;

        /**
         * 建议的处理方式：1-拒绝回答，2-替换，3-警告
         */
        private int actionType;

        public boolean hasSensitiveWord() {
            return hasSensitiveWord;
        }

        public void setHasSensitiveWord(boolean hasSensitiveWord) {
            this.hasSensitiveWord = hasSensitiveWord;
        }

        public List<MatchedWord> getMatchedWords() {
            return matchedWords;
        }

        public void setMatchedWords(List<MatchedWord> matchedWords) {
            this.matchedWords = matchedWords;
        }

        public int getSeverity() {
            return severity;
        }

        public void setSeverity(int severity) {
            this.severity = severity;
        }

        public int getActionType() {
            return actionType;
        }

        public void setActionType(int actionType) {
            this.actionType = actionType;
        }
    }

    /**
     * 匹配的敏感词
     */
    class MatchedWord {
        /**
         * 敏感词
         */
        private String word;

        /**
         * 敏感词类型：1-医疗诊断，2-政治敏感，3-不当内容
         */
        private int wordType;

        /**
         * 严重程度：1-低，2-中，3-高
         */
        private int severity;

        /**
         * 位置起始索引
         */
        private int startIndex;

        /**
         * 位置结束索引
         */
        private int endIndex;

        /**
         * 是否为误报
         */
        private boolean isFalsePositive;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getWordType() {
            return wordType;
        }

        public void setWordType(int wordType) {
            this.wordType = wordType;
        }

        public int getSeverity() {
            return severity;
        }

        public void setSeverity(int severity) {
            this.severity = severity;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }

        public boolean isFalsePositive() {
            return isFalsePositive;
        }

        public void setFalsePositive(boolean falsePositive) {
            isFalsePositive = falsePositive;
        }
    }
}