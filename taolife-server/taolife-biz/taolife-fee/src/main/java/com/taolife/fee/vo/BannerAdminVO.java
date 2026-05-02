package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理端轮播VO
 *
 * @author 文二
 * @date 2026-04-20
 */
@Data
public class BannerAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 轮播ID
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 链接类型
     */
    private Integer linkType;

    /**
     * 链接URL
     */
    private String linkUrl;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startDate;

    /**
     * 结束时间
     */
    private LocalDateTime endDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
