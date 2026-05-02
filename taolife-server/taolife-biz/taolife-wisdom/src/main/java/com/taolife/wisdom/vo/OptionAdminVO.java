package com.taolife.wisdom.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员体质问卷选项VO
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class OptionAdminVO {

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
     * 选项分值
     */
    private Integer optionValue;

    /**
     * 关联体质编码
     */
    private String targetTypeCode;

    /**
     * 关联体质名称
     */
    private String targetTypeName;

    /**
     * 反向计分分值
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