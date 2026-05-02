package com.taolife.plan.param;

import lombok.Data;

/**
 * 生成方案请求参数
 *
 * @author 文二
 * @date 2026-04-10
 */
@Data
public class GeneratePlanParam {

    /**
     * 方案周期天数（默认7天）
     */
    private Integer cycleDays = 7;
}
