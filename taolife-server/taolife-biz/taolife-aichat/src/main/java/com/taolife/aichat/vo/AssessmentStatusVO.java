package com.taolife.aichat.vo;

import lombok.Data;

/**
 * 体质评估状态VO
 * 用于返回用户是否有体质评估记录的标识
 *
 * @author 文二
 * @date 2026-03-23
 */
@Data
public class AssessmentStatusVO {

    /**
     * 状态：0-没有评估记录，1-有评估记录
     */
    private Integer assessmentStatus;
}