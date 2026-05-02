package com.taolife.wisdom.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 体质问卷题目实体
 * 对应数据库表 tl_wis_fitness_question
 *
 * @author 文二
 * @date 2026-03-22
 */
@Data
@Table("tl_wis_fitness_question")
public class FitnessQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
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
     * 是否删除：0-否，1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 是否禁用：0-否，1-是
     */
    private Integer isDisabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
