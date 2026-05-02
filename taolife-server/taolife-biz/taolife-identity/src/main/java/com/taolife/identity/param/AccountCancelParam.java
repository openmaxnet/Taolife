package com.taolife.identity.param;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 账号注销申请参数
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class AccountCancelParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 注销原因
     */
    @Size(max = 500, message = "注销原因不能超过500字")
    private String reason;
}
