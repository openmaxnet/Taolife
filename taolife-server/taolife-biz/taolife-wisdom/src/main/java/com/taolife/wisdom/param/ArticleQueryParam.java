package com.taolife.wisdom.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 文章查询参数
 * 用于分页查询文章列表
 *
 * @author 文二
 * @date 2026-03-21
 */
@Data
public class ArticleQueryParam {

    @Min(value = 0, message = "分类值不正确")
    @Max(value = 5, message = "分类值不正确")
    private Integer category;

    private Integer isRecommended;
    private Integer isFeatured;
    private String keyword;
    private String constitutionCode;

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 50, message = "每页数量不能大于50")
    private Integer pageSize = 10;
}
