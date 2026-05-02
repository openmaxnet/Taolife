package com.taolife.common.annotation;

import java.lang.annotation.*;

/**
 * 加密接口标记注解
 * 标记需要加密的 Controller 类或方法
 * 加在类上表示该 Controller 所有方法都需要加密，加在方法上可覆盖类级别配置
 *
 * @author 文二
 * @date 2026-05-01
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypted {
}
