package com.taolife.wisdom.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 体质问卷选项实体
 * 对应数据库表 tl_wis_fitness_option
 *
 * @author 文二
 * @date 2026-03-22
 */
@Data
@Table("tl_wis_fitness_option")
public class FitnessOption implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选项ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 题目ID
     */
    private String questionId;

    /**
     * 选项编号
     */
    private Integer optionNo;

    /**
     * 选项内容
     */
    private String optionText;

    /**
     * 选项分值（1-5）
     */
    private Integer optionValue;

    /**
     * 关联体质编码
     */
    private String targetTypeCode;

    /**
     * 反向计分时的分值
     */
    private Integer reverseValue;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
