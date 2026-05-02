package com.taolife.plan.vo;

import lombok.Data;

/**
 * AI调整结果VO（单条）
 *
 * @author 文二
 * @date 2026-04-16
 */
@Data
public class AiAdjustResultVO {

    /** 调整前内容 */
    private String beforeContent;

    /** 调整后内容 */
    private String afterContent;

    /** 调整记录ID */
    private String adjustmentId;
}
