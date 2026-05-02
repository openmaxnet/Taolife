package com.taolife.plan.vo;

import lombok.Data;

import java.util.List;

/**
 * 批量AI调整结果VO
 *
 * @author 文二
 * @date 2026-04-16
 */
@Data
public class BatchAiAdjustResultVO {

    /** 各子方案的调整结果列表 */
    private List<SubPlanAdjustItem> adjustments;
}
