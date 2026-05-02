package com.taolife.plan.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 方案调整推荐建议缓存
 * 存储AI生成的个性化调整标签
 *
 * @author 文二
 * @date 2026-04-16
 */
@Data
@Table("tl_plan_adjust_suggestion")
public class PlanAdjustSuggestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 子方案ID
     */
    @Column(isLogicDelete = false)
    private String subPlanId;

    /**
     * 方案类型：1饮食2运动3穴位4经络5生活
     */
    private Integer planType;

    /**
     * 推荐标签JSON数组
     */
    private String suggestions;

    /**
     * 生成时间
     */
    private LocalDateTime suggestionsTime;
}
