package com.taolife.plan.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量确认AI调整参数
 *
 * @author 文二
 * @date 2026-04-16
 */
@Data
public class BatchConfirmAdjustParam {

    /** 调整记录ID列表 */
    @NotEmpty(message = "调整记录ID列表不能为空")
    private List<@NotBlank String> adjustmentIds;
}
