package com.taolife.plan.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 方案任务查询参数
 * 用于分页查询方案执行任务
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class PlanTaskQueryParam {

    /**
     * 方案类型筛选（1-4）
     */
    private Integer planType;

    /**
     * 任务日期筛选
     */
    private String taskDate;

    /**
     * 任务状态筛选：1-待完成，2-已完成，3-已跳过
     */
    private Integer status;

    /**
     * 页码，默认1
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo = 1;

    /**
     * 每页数量，默认10
     */
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 50, message = "每页数量不能大于50")
    private Integer pageSize = 10;
}
