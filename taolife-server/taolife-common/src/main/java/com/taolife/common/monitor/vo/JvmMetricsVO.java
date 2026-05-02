package com.taolife.common.monitor.vo;

import lombok.Data;

/**
 * JVM 运行时指标
 */
@Data
public class JvmMetricsVO {
    /** 堆内存已用（MB） */
    private double heapUsed;
    /** 堆内存最大（MB） */
    private double heapMax;
    /** 堆内存使用率（0~1） */
    private double heapUsage;
    /** 非堆内存已用（MB） */
    private double nonHeapUsed;
    /** 活跃线程数 */
    private int activeThreads;
    /** 峰值线程数 */
    private int peakThreads;
    /** 系统 CPU 使用率（0~1） */
    private double systemCpuUsage;
    /** 进程 CPU 使用率（0~1） */
    private double processCpuUsage;
    /** JVM 运行时间（秒） */
    private long uptimeSeconds;
}
