package com.taolife.plan.vo;

import lombok.Data;

/**
 * 批量调整结果中的子方案项
 *
 * @author 文二
 * @date 2026-04-16
 */
@Data
public class SubPlanAdjustItem {

    /** 方案类型：1-饮食 2-运动 3-穴位 4-经络 5-生活方式 */
    private Integer planType;

    /** 方案类型名称 */
    private String planTypeName;

    /** 调整前内容 */
    private String beforeContent;

    /** 调整后内容 */
    private String afterContent;

    /** 调整记录ID */
    private String adjustmentId;
}
