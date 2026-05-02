package com.taolife.wisdom.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 体质测评记录VO
 *
 * @author 文二
 * @date 2026-03-22
 */
@Data
public class FitnessRecordVO {

    /**
     * 记录ID
     */
    private String recordId;

    /**
     * 体质编码
     */
    private String constitutionCode;

    /**
     * 体质名称
     */
    private String constitutionName;

    /**
     * 体质得分
     */
    private BigDecimal score;

    /**
     * 置信度
     */
    private BigDecimal confidence;

    /**
     * 测评模式：1-简易模式，2-精细模式
     */
    private Integer assessmentMode;

    /**
     * 兼夹体质JSON
     */
    private String mixedTypes;

    /**
     * 测评时间
     */
    private String createTime;
}
