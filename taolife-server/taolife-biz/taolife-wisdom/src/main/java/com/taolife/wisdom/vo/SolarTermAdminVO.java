package com.taolife.wisdom.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员节气VO
 *
 * @author 文二
 * @date 2026-04-20
 */
@Data
public class SolarTermAdminVO {

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
     * 节气简介
     */
    private String introduction;

    /**
     * 气候特点
     */
    private String climate;

    /**
     * 养生原则
     */
    private String healthPrinciples;

    /**
     * 传统习俗
     */
    private String customs;

    /**
     * 节气谚语
     */
    private String proverbs;

    /**
     * 饮食概要
     */
    private String dietSummary;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
