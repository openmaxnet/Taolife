package com.taolife.wisdom.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 体质类型实体
 * 对应数据库表 tl_wis_fitness_type
 *
 * @author 文二
 * @date 2026-03-22
 */
@Data
@Table("tl_wis_fitness_type")
public class FitnessType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 体质ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 体质编码
     */
    private String code;

    /**
     * 体质名称
     */
    private String name;

    /**
     * 英文名
     */
    private String nameEn;

    /**
     * 体质描述
     */
    private String description;

    /**
     * 体质特征
     */
    private String characteristics;

    /**
     * 形成原因
     */
    private String formationReason;

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
