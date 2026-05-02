package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 配额状态VO
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class QuotaStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 已使用次数 */
    private Integer used;

    /** 总配额，-1表示无限 */
    private Integer total;

    /** 剩余次数，-1表示无限 */
    private Integer remaining;

    /** 是否有剩余配额 */
    private Boolean hasRemaining;
}
