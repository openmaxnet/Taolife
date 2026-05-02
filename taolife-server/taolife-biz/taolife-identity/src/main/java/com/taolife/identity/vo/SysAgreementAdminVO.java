package com.taolife.identity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统协议管理列表VO
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class SysAgreementAdminVO {

    /** 协议ID */
    private String id;

    /** 文档标识 */
    private String code;

    /** 标题 */
    private String title;

    /** 版本号 */
    private Integer version;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
