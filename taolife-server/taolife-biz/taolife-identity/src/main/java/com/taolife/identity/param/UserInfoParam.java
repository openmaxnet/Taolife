package com.taolife.identity.param;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户信息修改参数
 * 用于修改用户基本信息
 *
 * @author 文二
 * @date 2026-03-16
 */
@Data
public class UserInfoParam implements Serializable {

    private static final long serialVersionUID = 1L;

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
}
