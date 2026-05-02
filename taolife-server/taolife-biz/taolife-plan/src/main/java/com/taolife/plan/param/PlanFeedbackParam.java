package com.taolife.plan.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 方案反馈参数
 * 用于提交健康方案或调整效果反馈
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class PlanFeedbackParam {

    /**
     * 反馈类型：1-方案反馈，2-调整效果反馈
     */
    @NotNull(message = "反馈类型不能为空")
    @Min(value = 1, message = "反馈类型值不正确")
    @Max(value = 2, message = "反馈类型值不正确")
    private Integer feedbackType;

    /**
     * 目标ID（方案历史ID或调整记录ID）
     */
    @NotBlank(message = "目标ID不能为空")
    private String targetId;

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    private String content;

    /**
     * 用户评分（1-5）
     */
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer rating;
}
