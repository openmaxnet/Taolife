package com.taolife.identity.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账号实体
 * 对应数据库表 tf_account
 *
 * @author 文二
 * @date 2026-03-16
 */
@Data
@Table("tl_id_account")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号ID（UUID）
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 账号类型：1-微信
     */
    private Integer accountType;

    /**
     * 第三方OpenID
     */
    private String openid;

    /**
     * 微信UnionID
     */
    private String unionid;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码（加密）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 会员等级：0-普通用户，1-月卡会员，2-年卡会员，3-终身会员
     */
    private Integer memberLevel;

    /**
     * 会员过期时间
     */
    private LocalDateTime memberExpireTime;

    /**
     * 会员成长值
     */
    private Integer growthValue;

    /**
     * 会员成长等级：0-无，1~9对应V1~V9
     */
    private Integer growthLevel;

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
     * 注销状态：0-正常，1-申请中，2-已注销
     */
    private Integer cancellationStatus;

    /**
     * 注销申请时间
     */
    private LocalDateTime cancellationTime;

    /**
     * 计划注销时间（申请时间+7天）
     */
    private LocalDateTime cancellationSchedule;

    /**
     * 注销原因
     */
    private String cancellationReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
