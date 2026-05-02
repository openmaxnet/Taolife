package com.taolife.identity.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账号注销状态VO
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class AccountCancelCheckVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 注销状态：0-正常，1-申请中，2-已注销
     */
    private Integer cancellationStatus;

    /**
     * 注销申请时间
     */
    private LocalDateTime cancellationTime;

    /**
     * 计划注销时间
     */
    private LocalDateTime cancellationSchedule;

    /**
     * 前置检查是否通过
     */
    private Boolean canCancel;

    /**
     * 不通过原因
     */
    private String blockReason;
}
