package com.taolife.fee.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信支付回调参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class WxPayCallbackParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 加密信息 */
    private String encrypt;

    /** 签名 */
    private String signature;

    /** 回调类型 */
    private String type;

    /** 创建时间 */
    private Long createTime;
}
