package com.taolife.identity.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员登录参数
 * 用于管理员登录时的参数验证
 *
 * @author 文二
 * @date 2026-04-10
 */
@Data
public class LoginAdminParam {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
