package com.taolife.wisdom.param;

import lombok.Data;

/**
 * 管理员体质类型保存参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class FitnessTypeSaveAdminParam {

    /**
     * ID（修改时传入）
     */
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
     * 是否禁用：0-否，1-是
     */
    private Integer isDisabled;
}
