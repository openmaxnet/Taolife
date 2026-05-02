package com.taolife.wisdom.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章列表VO
 * 用于返回文章列表数据
 *
 * @author 文二
 * @date 2026-03-21
 */
@Data
public class ArticleListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String subtitle;
    private Integer category;
    private String categoryName;
    private String tags;
    private String coverImageUrl;
    private String summary;
    private String author;
    private String source;
    private Integer readCount;
    private Integer likeCount;
    private Integer collectCount;
    private Integer isRecommended;
    private Integer isFeatured;
    private LocalDateTime publishTime;
}
