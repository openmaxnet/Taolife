package com.taolife.wisdom.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 节气详情VO
 * 用于详情页展示，包含完整的知识内容
 *
 * @author 文二
 * @date 2026-04-20
 */
@Data
public class SolarTermDetailVO implements Serializable {

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
     * 详情页背景大图
     */
    private String backgroundUrl;

    /**
     * 节气描述
     */
    private String description;

    /**
     * 节气简介（由来、历史）
     */
    private String introduction;

    /**
     * 气候特点
     */
    private String climate;

    /**
     * 养生原则（JSON数组）
     */
    private String healthPrinciples;

    /**
     * 传统习俗（JSON数组）
     */
    private String customs;

    /**
     * 节气谚语（JSON数组）
     */
    private String proverbs;

    /**
     * 饮食概要
     */
    private String dietSummary;

    /**
     * 是否为当前节气
     */
    private Boolean isCurrent;
}
