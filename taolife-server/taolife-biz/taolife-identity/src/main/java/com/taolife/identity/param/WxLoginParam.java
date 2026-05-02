package com.taolife.identity.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信小程序登录参数
 *
 * @author 文二
 * @date 2026-03-16
 */
@Data
public class WxLoginParam {

    /**
     * 微信登录凭证
     * 小程序调用wx.login()获取
     */
    @NotBlank(message = "登录凭证不能为空")
    private String code;

    /**
     * 手机号获取凭证（暂时注释）
     * 小程序调用button open-type="getPhoneNumber"获取
     */
    // @NotBlank(message = "手机号凭证不能为空")
    // private String phoneCode;

    /**
     * 加密数据
     * 包括用户敏感信息，需要使用session_key解密
     */
    private String encryptedData;

    /**
     * 加密算法的初始向量
     * 用于解密encryptedData
     */
    private String iv;
}
