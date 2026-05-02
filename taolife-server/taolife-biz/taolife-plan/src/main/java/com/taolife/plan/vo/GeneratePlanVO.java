package com.taolife.plan.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 方案生成VO
 * 用于返回方案生成的异步任务信息
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class GeneratePlanVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异步任务ID，用于轮询
     */
    private String taskId;

    /**
     * 状态消息，如"方案生成中，请稍候..."
     */
    private String message;

    /**
     * 预估时间，如"2-5分钟"
     */
    private String estimatedTime;
}
