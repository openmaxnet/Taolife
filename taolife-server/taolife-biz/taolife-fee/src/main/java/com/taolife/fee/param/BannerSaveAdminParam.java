package com.taolife.fee.param;

import lombok.Data;

/**
 * 管理员轮播保存参数
 *
 * @author 文二
 * @date 2026-04-20
 */
@Data
public class BannerSaveAdminParam {

    /**
     * 轮播ID（修改时必填）
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

    /**
     * 排序（数值越大越靠前）
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}
