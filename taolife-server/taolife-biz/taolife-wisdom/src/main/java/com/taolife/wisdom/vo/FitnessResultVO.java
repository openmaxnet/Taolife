package com.taolife.wisdom.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 体质测评结果VO
 *
 * @author 文二
 * @date 2026-03-22
 */
@Data
public class FitnessResultVO {

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
     * 兼夹体质列表
     */
    private List<FitnessTypeVO> mixedTypes;

    /**
     * 体质倾向列表
     */
    private List<FitnessTypeVO> tendencyTypes;

    /**
     * 体质描述
     */
    private String description;

    /**
     * 体质特征
     */
    private String characteristics;

    /**
     * 健康建议
     */
    private String healthAdvice;

    /**
     * 饮食指导
     */
    private String dietGuidance;

    /**
     * 运动指导
     */
    private String exerciseGuidance;

    /**
     * 情志调节
     */
    private String emotionGuidance;

    /**
     * 穴位保健
     */
    private String acupointGuidance;

    /**
     * 所有体质得分
     */
    private Map<String, BigDecimal> allScores;

    /**
     * 测评时间
     */
    private String createTime;
}
