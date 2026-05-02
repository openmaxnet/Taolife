package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 首页轮播VO
 *
 * @author 文二
 * @date 2026-04-20
 */
@Data
public class HomeBannerVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
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
     * 链接类型：0-无跳转，1-节气详情，2-文章，3-外部链接，4-自定义页面
     */
    private Integer linkType;

    /**
     * 链接地址
     */
    private String linkUrl;
}
