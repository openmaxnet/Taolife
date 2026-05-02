package com.taolife.plan.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 方案周期报告实体
 * 对应数据库表 tf_plan_cycle_report
 *
 * @author 文二
 * @date 2026-04-08
 */
@Data
@Table("tl_plan_cycle_report")
public class PlanCycleReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报告ID（主键）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 被总结的方案ID
     */
    private String planId;

    /**
     * 触发总结的新方案ID
     */
    private String newPlanId;

    /**
     * AI生成的报告内容（Markdown格式）
     */
    private String reportContent;

    /**
     * 阅读状态：0-未读，1-已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
