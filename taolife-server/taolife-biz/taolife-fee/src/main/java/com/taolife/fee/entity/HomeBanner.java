package com.taolife.fee.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 首页轮播实体类
 * 对应数据库表 tl_fee_home_banner
 *
 * @author 文二
 * @date 2026-04-20
 */
@Data
@Table("tl_fee_home_banner")
public class HomeBanner implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
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

    /**
     * 生效开始时间
     */
    private LocalDateTime startDate;

    /**
     * 生效结束时间
     */
    private LocalDateTime endDate;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
