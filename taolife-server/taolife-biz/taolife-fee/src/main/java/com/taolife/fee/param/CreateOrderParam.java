package com.taolife.fee.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建订单参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class CreateOrderParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 套餐代码 */
    @NotBlank(message = "套餐代码不能为空")
    private String planCode;

    /** 微信OpenID */
    @NotBlank(message = "OpenID不能为空")
    private String wxOpenid;
}
