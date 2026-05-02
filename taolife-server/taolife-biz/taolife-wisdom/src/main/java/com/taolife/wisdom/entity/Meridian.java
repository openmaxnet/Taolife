package com.taolife.wisdom.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 经络实体
 * 对应数据库表 tl_wis_meridian
 *
 * @author 文二
 * @date 2026-03-24
 */
@Data
@Table("tl_wis_meridian")
public class Meridian implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 经络ID（字符串格式）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 经络编码
     */
    private String code;

    /**
     * 经络名称
     */
    private String name;

    /**
     * 拼音
     */
    private String namePinyin;

    /**
     * 分类：1-十二正经，2-奇经八脉
     */
    private Integer category;

    /**
     * 描述
     */
    private String description;

    /**
     * 循行描述
     */
    private String pathDescription;

    /**
     * 主治
     */
    private String mainIndications;

    /**
     * 线条颜色（十六进制）
     */
    private String lineColor;

    /**
     * 线条宽度
     */
    private BigDecimal lineWidth;

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
