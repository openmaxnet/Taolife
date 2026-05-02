package com.taolife.common.annotation;

import java.lang.annotation.*;

/**
 * 跳过鉴权注解
 * 标记在Controller方法上，表示该接口不需要进行身份验证
 * 通常用于登录、注册、验证码等无需登录即可访问的接口
 *
 * @author 文二
 * @date 2026-03-16
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthSkip {

    /**
     * 是否跳过认证
     * 默认为true，设置为false时可忽略某些特殊验证
     *
     * @return 是否跳过认证
     */
    boolean value() default true;

    /**
     * 说明跳过认证的原因
     *
     * @return 说明信息
     */
    String reason() default "该接口无需登录即可访问";

    /**
     * 标记该接口是否为登录接口
     * 登录接口会在有有效token时拒绝重复登录
     *
     * @return 是否为登录接口
     */
    boolean loginEndpoint() default false;
}
