package com.taolife.aichat.service;

import com.taolife.aichat.entity.ChatRule;

import java.util.List;

/**
 * 问题分类服务接口
 *
 * @author 文二
 * @date 2026-03-26
 */
public interface IQuestionClassificationService {

    /**
     * 对问题进行分类
     *
     * @param question 用户问题
     * @return 分类结果
     */
    ClassificationResult classify(String question);

    /**
     * 获取所有分类规则
     *
     * @return 分类规则列表
     */
    List<ChatRule> getAllRules();

    /**
     * 分类结果
     */
    class ClassificationResult {
        /**
         * 问题类型：1-体质类，2-食疗类，3-穴位类，4-养生类，5-其他
         */
        private Integer questionType;

        /**
         * 分类编码
         */
        private String categoryCode;

        /**
         * 分类名称
         */
        private String categoryName;

        /**
         * 置信度
         */
        private Double confidence;

        /**
         * 是否在允许的范围内
         */
        private boolean isAllowed;

        /**
         * 拒绝原因（如果不允许）
         */
        private String rejectReason;

        public Integer getQuestionType() {
            return questionType;
        }

        public void setQuestionType(Integer questionType) {
            this.questionType = questionType;
        }

        public String getCategoryCode() {
            return categoryCode;
        }

        public void setCategoryCode(String categoryCode) {
            this.categoryCode = categoryCode;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public Double getConfidence() {
            return confidence;
        }

        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }

        public boolean isAllowed() {
            return isAllowed;
        }

        public void setAllowed(boolean allowed) {
            isAllowed = allowed;
        }

        public String getRejectReason() {
            return rejectReason;
        }

        public void setRejectReason(String rejectReason) {
            this.rejectReason = rejectReason;
        }
    }
}