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
 * 体质测评记录实体
 * 对应数据库表 tl_wis_fitness_record
 *
 * @author 文二
 * @date 2026-03-22
 */
@Data
@Table("tl_wis_fitness_record")
public class FitnessRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 用户ID（可选）
     */
    private String userId;

    /**
     * 体质类型编码
     */
    private String constitutionCode;

    /**
     * 体质类型名称
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
     * 兼夹体质JSON数组
     */
    private String mixedTypes;

    /**
     * 体质倾向JSON数组
     */
    private String tendencyTypes;

    /**
     * 问卷答案JSON
     */
    private String questionAnswers;

    /**
     * 分析结果JSON
     */
    private String analysisResult;

    /**
     * 完整报告内容
     */
    private String reportContent;

    /**
     * 调理建议JSON
     */
    private String suggestions;

    /**
     * 是否为最新记录：0-否，1-是
     */
    private Integer isLatest;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 测评时间
     */
    @Column(onUpdateValue = "now()")
    private LocalDateTime createTime;
}
