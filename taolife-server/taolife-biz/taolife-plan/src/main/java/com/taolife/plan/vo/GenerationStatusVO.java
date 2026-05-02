package com.taolife.plan.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 方案生成状态VO
 * 用于返回方案生成的进度状态
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class GenerationStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异步任务ID
     */
    private String taskId;

    /**
     * 状态：1=等待中，2=处理中，3=已完成，4=失败
     */
    private Integer status;

    /**
     * 进度百分比 0-100
     */
    private Integer progress;

    /**
     * 生成的方案ID（完成前为null）
     */
    private String result;

    /**
     * 错误信息（失败时才有值）
     */
    private String errorMessage;

    /**
     * 进度描述文本（由后端枚举返回）
     */
    private String message;
}
