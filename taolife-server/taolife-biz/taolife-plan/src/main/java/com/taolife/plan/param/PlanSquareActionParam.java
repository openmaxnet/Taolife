package com.taolife.plan.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 方案广场操作参数
 * 用于点赞、收藏等操作
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class PlanSquareActionParam {

    /**
     * 方案广场ID
     */
    @NotBlank(message = "方案广场ID不能为空")
    private String squareId;

    /**
     * 操作类型：1-点赞，2-取消点赞，3-收藏，4-取消收藏
     */
    @NotNull(message = "操作类型不能为空")
    @Min(value = 1, message = "操作类型值不正确")
    @Max(value = 4, message = "操作类型值不正确")
    private Integer actionType;
}
