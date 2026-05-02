package com.taolife.wisdom.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 食材实体
 * 对应数据库表 tl_wis_food
 *
 * @author 文二
 * @date 2026-03-20
 */
@Data
@Table("tl_wis_food")
public class Food implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 食材ID（UUID）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 食材名称
     */
    private String name;

    /**
     * 拼音
     */
    private String namePinyin;

    /**
     * 分类：1-谷物，2-蔬菜，3-水果，4-肉类，5-药材
     */
    private Integer category;

    /**
     * 性质：1-寒，2-凉，3-平，4-温，5-热
     */
    private Integer nature;

    /**
     * 味道
     */
    private String flavor;

    /**
     * 归经
     */
    private String meridianEntry;

    /**
     * 功效
     */
    private String efficacy;

    /**
     * 适宜人群/症状
     */
    private String indications;

    /**
     * 禁忌人群
     */
    private String contraindications;

    /**
     * 用法用量
     */
    private String usage;

    /**
     * 养生食谱
     */
    private String recipes;

    /**
     * 图片
     */
    private String imageUrl;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 收藏次数
     */
    private Integer collectCount;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 是否禁用：0-否，1-是
     */
    private Integer isDisabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
