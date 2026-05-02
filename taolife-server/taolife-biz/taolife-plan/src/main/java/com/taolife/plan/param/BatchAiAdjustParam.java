package com.taolife.plan.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 批量AI调整方案参数
 *
 * @author 文二
 * @date 2026-04-16
 */
@Data
public class BatchAiAdjustParam {

    /** 用户方案ID */
    @NotBlank(message = "方案ID不能为空")
    private String userPlanId;

    /** 批量调整需求描述 */
    @NotBlank(message = "调整需求不能为空")
    @Size(max = 500, message = "调整需求不能超过500字")
    private String adjustmentRequest;
}
