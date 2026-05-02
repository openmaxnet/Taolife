package com.taolife.wisdom.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章保存参数
 * 用于创建和修改文章
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class ArticleSaveParam {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String subtitle;

    @Min(value = 1, message = "分类值不正确")
    @Max(value = 5, message = "分类值不正确")
    private Integer category;

    private String tags;
    private String coverImageUrl;
    private String summary;
    private String content;

    @Min(value = 1, message = "内容类型不正确")
    @Max(value = 2, message = "内容类型不正确")
    private Integer contentType;

    private String author;
    private String source;
    private String relatedConstitutionCodes;
    private Integer relatedSeason;
    private String relatedSolarTerm;
    private Integer isRecommended;
    private Integer isFeatured;
    private LocalDateTime publishTime;
    private Integer sortOrder;

    @Min(value = 0, message = "状态值不正确")
    @Max(value = 1, message = "状态值不正确")
    private Integer status;
}
