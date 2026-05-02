package com.taolife.plan.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 方案调整记录实体类
 * 对应数据库表 tl_plan_adjust_record
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
@Table("tl_plan_adjust_record")
public class PlanAdjustRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 用户方案ID
     */
    private String userPlanId;

    /**
     * 方案类型：1-饮食，2-运动，3-穴位，4-生活
     */
    private Integer planType;

    /**
     * 调整类型：1-AI自动调整，2-用户手动调整，3-系统调整
     */
    private Integer adjustmentType;

    /**
     * 调整原因
     */
    private String adjustmentReason;

    /**
     * 调整前内容JSON
     */
    private String beforeContent;

    /**
     * 调整后内容JSON
     */
    private String afterContent;

    /**
     * AI模型版本
     */
    private String aiModel;

    /**
     * AI提示词
     */
    private String aiPrompt;

    /**
     * AI响应内容
     */
    private String aiResponse;

    /**
     * 用户反馈
     */
    private String userFeedback;

    /**
     * 效果评分：1-5分
     */
    private Integer effectivenessScore;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否已确认：0-待确认，1-已确认
     */
    private Integer isConfirmed = 0;
}
