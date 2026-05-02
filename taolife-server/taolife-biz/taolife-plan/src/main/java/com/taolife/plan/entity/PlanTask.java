package com.taolife.plan.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 方案任务实体类
 * 对应数据库表 tf_plan_task
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
@Table("tl_plan_task")
public class PlanTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID（主键）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 用户方案ID
     */
    private String userPlanId;

    /**
     * 方案类型：1-饮食，2-运动，3-穴位，4-生活
     */
    private Integer planType;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String taskDescription;

    /**
     * 目标次数
     */
    private Integer targetCount;

    /**
     * 已完成次数
     */
    private Integer completedCount;

    /**
     * 任务日期
     */
    private LocalDate taskDate;

    /**
     * 任务分类：1-每日必做，2-每周任务，3-自定义
     */
    private Integer taskCategory;

    /**
     * 优先级：1-低，2-中，3-高
     */
    private Integer priority;

    /**
     * 资源类型：food, exercise, acupoint, article
     */
    private String resourceType;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 状态：1-待完成，2-已完成，3-已跳过
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
