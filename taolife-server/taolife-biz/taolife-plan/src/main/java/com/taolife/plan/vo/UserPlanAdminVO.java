package com.taolife.plan.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户方案列表VO（管理后台）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class UserPlanAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 方案ID */
    private String id;
    /** 用户账号ID */
    private String accountId;
    /** 体质名称 */
    private String constitutionName;
    /** 季节 */
    private Integer season;
    /** 季节名称 */
    private String seasonName;
    /** 方案标题 */
    private String planTitle;
    /** 状态 */
    private Integer status;
    /** 完成率 */
    private BigDecimal completionRate;
    /** 总任务数 */
    private Integer totalTasks;
    /** 已完成任务数 */
    private Integer completedTasks;
    /** 用户评分 */
    private Integer userRating;
    /** 调整次数 */
    private Integer adjustmentCount;
    /** 开始日期 */
    private String startDate;
    /** 结束日期 */
    private String endDate;
    /** 创建时间 */
    private String createTime;
}
