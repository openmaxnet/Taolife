package com.taolife.identity.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息实体
 * 对应数据库表 tf_user
 * 存储用户的扩展信息（区别于账号信息）
 *
 * @author 文二
 * @date 2026-03-16
 */
@Data
@Table("tl_id_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（UUID）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 关联账号ID
     */
    private String accountId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /** 紧急联系电话 */
    private String emergencyPhone;

    /** 是否删除：0-否，1-是 */
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
