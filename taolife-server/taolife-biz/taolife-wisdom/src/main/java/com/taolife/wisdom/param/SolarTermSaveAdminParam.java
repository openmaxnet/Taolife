package com.taolife.wisdom.param;

import lombok.Data;

/**
 * 管理员节气保存参数
 *
 * @author 文二
 * @date 2026-04-20
 */
@Data
public class SolarTermSaveAdminParam {

    /**
     * ID（修改时传入）
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
     * 开始月份
     */
    private Integer startMonth;

    /**
     * 开始日期
     */
    private Integer startDay;

    /**
     * 结束月份
     */
    private Integer endMonth;

    /**
     * 结束日期
     */
    private Integer endDay;

    /**
     * 节气描述
     */
    private String description;

    /**
     * 季节：1-春，2-夏，3-秋，4-冬
     */
    private Integer season;

    /**
     * 列表页封面图
     */
    private String coverUrl;

    /**
     * 详情页背景大图
     */
    private String backgroundUrl;

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
}
