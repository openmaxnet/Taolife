package com.taolife.identity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统协议VO
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class SysAgreementVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 版本号 */
    private Integer version;
}
