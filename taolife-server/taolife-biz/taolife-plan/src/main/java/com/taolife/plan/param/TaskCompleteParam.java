package com.taolife.plan.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 完成任务参数
 * 用于标记方案任务为已完成
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class TaskCompleteParam {

    /**
     * 任务ID
     */
    @NotBlank(message = "任务ID不能为空")
    private String taskId;
}
