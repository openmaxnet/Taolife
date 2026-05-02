package com.taolife.identity.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 系统用户保存参数
 * 用于创建和修改用户
 *
 * @author 文二
 * @date 2026-04-11
 */
@Data
public class SysUserSaveParam {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码（创建时必填，修改时可选）
     * 8-20位，包含大小写字母和数字
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+\\-=]{8,20}$",
            message = "密码必须8-20位，包含大小写字母和数字")
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 角色ID
     */
    @NotBlank(message = "角色不能为空")
    private String roleId;
}