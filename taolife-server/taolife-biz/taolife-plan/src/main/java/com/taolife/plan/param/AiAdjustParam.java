package com.taolife.plan.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AI调整方案参数（单条）
 *
 * @author 文二
 * @date 2026-04-16
 */
@Data
public class AiAdjustParam {

    /** 用户方案ID */
    @NotBlank(message = "方案ID不能为空")
    private String userPlanId;

    /** 方案类型：1-饮食 2-运动 3-穴位 4-经络 5-生活方式 */
    @NotNull(message = "方案类型不能为空")
    @Min(value = 1, message = "方案类型值不正确")
    @Max(value = 5, message = "方案类型值不正确")
    private Integer planType;

    /** 调整需求描述 */
    @NotBlank(message = "调整需求不能为空")
    @Size(max = 500, message = "调整需求不能超过500字")
    private String adjustmentRequest;
}
