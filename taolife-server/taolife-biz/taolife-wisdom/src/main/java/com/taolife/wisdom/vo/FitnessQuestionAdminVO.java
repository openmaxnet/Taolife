package com.taolife.wisdom.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员体质问卷题目VO
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class FitnessQuestionAdminVO {

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
     * 维度
     */
    private String dimension;

    /**
     * 答案类型：1-单选，2-多选
     */
    private Integer answerType;

    /**
     * 是否必答：0-否，1-是
     */
    private Integer isRequired;

    /**
     * 所属模式：1-简易模式，2-精细模式
     */
    private Integer questionMode;

    /**
     * 题目权重
     */
    private BigDecimal weight;

    /**
     * 是否反向计分：0-否，1-是
     */
    private Integer reverseScore;

    /**
     * 一致性检验题：0-否，1-是
     */
    private Integer isConsistencyCheck;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否禁用：0-否，1-是
     */
    private Integer isDisabled;

    /**
     * 选项数量
     */
    private Integer optionCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
