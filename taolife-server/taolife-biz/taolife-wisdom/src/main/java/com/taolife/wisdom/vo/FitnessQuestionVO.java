package com.taolife.wisdom.vo;

import lombok.Data;

import java.util.List;

/**
 * 体质问卷题目VO
 *
 * @author 文二
 * @date 2026-03-22
 */
@Data
public class FitnessQuestionVO {

    /**
     * 题目ID
     */
    private String id;

    /**
     * 题目编号
     */
    private Integer questionNo;

    /**
     * 题目内容
     */
    private String questionText;

    /**
     * 题目补充说明
     */
    private String questionTextSecondary;

    /**
     * 所属分类
     */
    private String category;

    /**
     * 答案类型：1-单选，2-多选
     */
    private Integer answerType;

    /**
     * 是否必答：0-否，1-是
     */
    private Integer isRequired;

    /**
     * 选项列表
     */
    private List<FitnessOptionVO> options;

    /**
     * 体质问卷选项VO
     */
    @Data
    public static class FitnessOptionVO {
        /**
         * 选项ID
         */
        private String id;

        /**
         * 选项编号
         */
        private Integer optionNo;

        /**
         * 选项内容
         */
        private String optionText;

        /**
         * 选项分值
         */
        private Integer optionValue;

        /**
         * 关联体质编码
         */
        private String targetTypeCode;
    }
}
