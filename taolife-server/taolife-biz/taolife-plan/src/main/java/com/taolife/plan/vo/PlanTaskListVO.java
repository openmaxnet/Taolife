package com.taolife.plan.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 方案任务列表VO
 * 用于返回方案任务的分页列表
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class PlanTaskListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private String id;

    /**
     * 方案类型
     */
    private Integer planType;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述（截断）
     */
    private String taskDescription;

    /**
     * 任务日期（格式：yyyy-MM-dd）
     */
    private String taskDate;

    /**
     * 状态：1=待完成 2=已完成 3=已跳过
     */
    private Integer status;

    /**
     * 优先级：1=低 2=中 3=高
     */
    private Integer priority;

    /**
     * 任务分类：1=每日必做 2=每周任务 3=自定义
     */
    private Integer taskCategory;

    /**
     * 目标次数
     */
    private Integer targetCount;

    /**
     * 已完成次数
     */
    private Integer completedCount;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 资源ID
     */
    private String resourceId;
}
