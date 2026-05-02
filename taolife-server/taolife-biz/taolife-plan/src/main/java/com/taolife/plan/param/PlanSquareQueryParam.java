package com.taolife.plan.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 方案广场查询参数
 * 用于分页查询方案广场列表
 *
 * @author 文二
 * @date 2026-04-06
 */
@Data
public class PlanSquareQueryParam {

    /**
     * 搜索关键词（方案摘要）
     */
    private String keyword;

    /**
     * 方案类型筛选
     */
    private Integer planType;

    /**
     * 体质名称筛选
     */
    private String constitutionName;

    /**
     * 排序方式：1-最新(createTime DESC)，2-最多点赞(likeCount DESC)，3-最多浏览(viewCount DESC)
     */
    private Integer sortBy;

    /**
     * 页码，默认1
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo = 1;

    /**
     * 每页数量，默认10
     */
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 50, message = "每页数量不能大于50")
    private Integer pageSize = 10;
}
