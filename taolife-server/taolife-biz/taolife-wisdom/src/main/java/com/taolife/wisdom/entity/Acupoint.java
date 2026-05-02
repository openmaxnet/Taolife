package com.taolife.wisdom.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 穴位实体
 * 对应数据库表 tl_wis_acupoint
 *
 * @author 文二
 * @date 2026-03-24
 */
@Data
@Table("tl_wis_acupoint")
public class Acupoint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 穴位ID（字符串格式）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 穴位名称
     */
    private String name;

    /**
     * 拼音
     */
    private String namePinyin;

    /**
     * 分类：1-经穴
     */
    private Integer category;

    /**
     * 经络编码
     */
    private String meridianCode;

    /**
     * 经络名称
     */
    private String meridianName;

    /**
     * 定位描述
     */
    private String locationDescription;

    /**
     * 功效
     */
    private String efficacy;

    /**
     * 主治
     */
    private String indications;

    /**
     * 操作方法
     */
    private String operationMethod;

    /**
     * 按摩提示
     */
    private String massageTips;

    /**
     * 标注类型：1-普通，2-重要，3-关键
     */
    private Integer markerType;

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
