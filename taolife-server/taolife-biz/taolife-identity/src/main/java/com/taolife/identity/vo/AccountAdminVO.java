package com.taolife.identity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员小程序账号视图对象（列表）
 *
 * @author 文二
 * @date 2026-04-13
 */
@Data
@NoArgsConstructor
public class AccountAdminVO {

    /**
     * 账号ID
     */
    private String id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 会员等级：0-普通用户，1-月卡会员，2-年卡会员，3-终身会员
     */
    private Integer memberLevel;

    /**
     * 会员过期时间
     */
    private LocalDateTime memberExpireTime;

    /**
     * 禁用标记（0：启用，1：禁用）
     */
    private Integer isDisabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
