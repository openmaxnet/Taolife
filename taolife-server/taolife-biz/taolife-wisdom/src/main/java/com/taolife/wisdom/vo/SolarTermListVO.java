package com.taolife.wisdom.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 节气列表VO
 * 用于列表页展示，包含基本信息
 *
 * @author 文二
 * @date 2026-04-20
 */
@Data
public class SolarTermListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节气ID
     */
    private String id;

    /**
     * 节气名称
     */
    private String termName;

    /**
     * 节气序号（1-24）
     */
    private Integer termOrder;

    /**
     * 日期范围描述（如"3月20日 - 4月4日"）
     */
    private String dateRange;

    /**
     * 季节：1-春，2-夏，3-秋，4-冬
     */
    private Integer season;

    /**
     * 季节名称
     */
    private String seasonName;

    /**
     * 封面图
     */
    private String coverUrl;

    /**
     * 节气描述
     */
    private String description;

    /**
     * 是否为当前节气
     */
    private Boolean isCurrent;
}
