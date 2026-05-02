package com.taolife.wisdom.vo;

import lombok.Data;

/**
 * 体质类型VO
 *
 * @author 文二
 * @date 2026-03-22
 */
@Data
public class FitnessTypeVO {

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
}
