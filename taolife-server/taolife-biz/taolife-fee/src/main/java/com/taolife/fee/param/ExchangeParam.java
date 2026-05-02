package com.taolife.fee.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 积分兑换参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class ExchangeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商品编码 */
    @NotBlank(message = "商品编码不能为空")
    private String goodsCode;
}
