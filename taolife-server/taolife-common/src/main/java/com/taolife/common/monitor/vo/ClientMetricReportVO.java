package com.taolife.common.monitor.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 客户端指标上报请求
 */
@Data
public class ClientMetricReportVO {
    /** 来源：admin / miniapp */
    private String source;
    /** 指标列表 */
    private List<MetricItem> metrics;

    @Data
    public static class MetricItem {
        /** 类型：web_vital / api_request / page_view / error / memory_warning */
        private String type;
        /** 指标名称：LCP / FID / CLS / TTFB / INP 等 */
        private String name;
        /** 指标值 */
        private double value;
        /** 评级：good / needs-improvement / poor */
        private String rating;
        /** 时间戳 */
        private long timestamp;
        /** 附加标签 */
        private Map<String, String> tags;
    }
}
