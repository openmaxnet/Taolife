package com.taolife.plan.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 手动调整方案参数
 * 用于手动调整健康方案内容
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class ManualAdjustmentParam {

    /**
     * 用户方案ID
     */
    @NotBlank(message = "方案ID不能为空")
    private String userPlanId;

    /**
     * 方案类型：1-饮食，2-运动，3-穴位，4-经络，5-生活
     */
    @NotNull(message = "方案类型不能为空")
    @Min(value = 1, message = "方案类型值不正确")
    @Max(value = 5, message = "方案类型值不正确")
    private Integer planType;

    /**
     * 调整原因
     */
    @NotBlank(message = "调整原因不能为空")
    private String adjustmentReason;

    /**
     * 调整后内容（JSON格式）
     */
    @NotBlank(message = "调整后内容不能为空")
    private String afterContent;
}
