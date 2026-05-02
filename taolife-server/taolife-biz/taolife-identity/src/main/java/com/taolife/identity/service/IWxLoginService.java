package com.taolife.identity.service;

import com.taolife.identity.param.WxLoginParam;
import com.taolife.identity.vo.WxLoginVO;

/**
 * 微信小程序登录服务接口
 *
 * @author 文二
 * @date 2026-03-16
 */
public interface IWxLoginService {

    /**
     * 微信小程序登录
     * 通过code获取openid，创建或更新用户信息，返回JWT Token
     *
     * @param param 登录参数
     * @return 登录结果
     */
    WxLoginVO wxLogin(WxLoginParam param);

    /**
     * 刷新Token
     * 使用refreshToken换取新的双Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的登录结果
     */
    WxLoginVO refreshToken(String refreshToken);
}
