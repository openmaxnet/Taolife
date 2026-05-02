package com.taolife.plan.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 确认AI调整参数（单条）
 *
 * @author 文二
 * @date 2026-04-16
 */
@Data
public class ConfirmAdjustParam {

    /** 调整记录ID */
    @NotBlank(message = "调整记录ID不能为空")
    private String adjustmentId;
}
