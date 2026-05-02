package com.taolife.wisdom.param;

import lombok.Data;

/**
 * 管理员体质问卷选项保存参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class OptionSaveAdminParam {

    /**
     * ID（修改时传入）
     */
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
     * 反向计分分值
     */
    private Integer reverseValue;

    /**
     * 排序
     */
    private Integer sortOrder;
}