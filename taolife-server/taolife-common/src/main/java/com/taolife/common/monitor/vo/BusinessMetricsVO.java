package com.taolife.common.monitor.vo;

import lombok.Data;

import java.util.Map;

/**
 * 业务指标（自定义计数器）
 */
@Data
public class BusinessMetricsVO {
    /** 用户注册总数 */
    private double userRegisterTotal;
    /** 用户登录总数 */
    private double userLoginTotal;
    /** 订单创建总数 */
    private double orderCreateTotal;
    /** AI 调用总数 */
    private double aiRequestTotal;
    /** AI 调用平均耗时（ms） */
    private double aiRequestAvgTimeMs;
    /** 其他自定义计数器 */
    private Map<String, Double> customCounters;
}
