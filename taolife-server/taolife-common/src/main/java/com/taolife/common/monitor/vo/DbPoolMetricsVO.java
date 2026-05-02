package com.taolife.common.monitor.vo;

import lombok.Data;

/**
 * 数据库连接池指标
 */
@Data
public class DbPoolMetricsVO {
    /** 活跃连接数 */
    private int activeConnections;
    /** 空闲连接数 */
    private int idleConnections;
    /** 最大连接数 */
    private int maxConnections;
    /** 等待连接的线程数 */
    private int pendingThreads;
    /** 连接池使用率（0~1） */
    private double usage;
}
