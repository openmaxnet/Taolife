package com.taolife.identity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录结果视图对象
 * 用于返回登录成功后的令牌和用户信息
 *
 * @author 文二
 * @date 2026-04-10
 */
@Data
@NoArgsConstructor
public class LoginAdminVO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 用户信息
     */
    private UserInfoAdminVO userInfo;

    /**
     * 创建登录结果
     *
     * @param accessToken  访问令牌
     * @param refreshToken 刷新令牌
     * @param userInfo     用户信息
     */
    public LoginAdminVO(String accessToken, String refreshToken, UserInfoAdminVO userInfo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userInfo = userInfo;
    }
}
