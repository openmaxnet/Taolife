package com.taolife.identity.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 * 返回用户详细信息
 *
 * @author 文二
 * @date 2026-03-16
 */
@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 账号类型：1-微信
     */
    private Integer accountType;

    /**
     * 手机号
     */
    private String phone;

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
     * 累计积分
     */
    private Integer totalPoints;

    /**
     * 可用积分
     */
    private Integer availablePoints;

    /**
     * 是否禁用：0-否，1-是
     */
    private Integer isDisabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 收藏数
     */
    private Long collectCount;

    /**
     * 关注数
     */
    private Long followingCount;
}
