package com.taolife.plan.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分享到方案广场参数
 * 用于将个人方案分享到方案广场
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class PlanSquareShareParam {

    /**
     * 用户方案ID
     */
    @NotBlank(message = "方案ID不能为空")
    private String userPlanId;

    /**
     * 方案摘要
     */
    @NotBlank(message = "方案摘要不能为空")
    private String planSummary;

    /**
     * 方案标签（JSON数组字符串）
     */
    private String planTags;
}
