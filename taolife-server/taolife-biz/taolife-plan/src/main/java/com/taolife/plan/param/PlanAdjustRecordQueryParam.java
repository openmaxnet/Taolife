package com.taolife.plan.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 调整记录查询参数
 * 用于分页查询方案调整记录
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class PlanAdjustRecordQueryParam {

    /**
     * 调整类型筛选：1-AI调整，2-手动调整，3-系统调整
     */
    private Integer adjustmentType;

    /**
     * 筛选开始日期
     */
    private String startDate;

    /**
     * 筛选结束日期
     */
    private String endDate;

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
