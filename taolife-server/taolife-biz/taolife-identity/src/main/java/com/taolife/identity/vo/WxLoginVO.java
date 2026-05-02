package com.taolife.identity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信小程序登录返回
 *
 * @author 文二
 * @date 2026-03-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxLoginVO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 用户基本信息
     */
    private UserBasicInfoVO userInfo;

    /**
     * 是否新用户
     */
    private Boolean isNewUser;

    /**
     * 用户基本信息VO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBasicInfoVO {

        /**
         * 账号ID
         */
        private String accountId;

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
         * 会员等级：0-普通用户，1-月卡会员，2-年卡会员，3-终身会员
         */
        private Integer memberLevel;
    }
}
