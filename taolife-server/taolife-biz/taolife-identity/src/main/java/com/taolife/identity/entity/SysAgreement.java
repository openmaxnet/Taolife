package com.taolife.identity.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统协议与声明实体
 * 对应数据库表 tf_sys_agreement
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
@Table("tl_sys_agreement")
public class SysAgreement implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 协议ID */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 文档标识 */
    private String code;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 版本号 */
    private Integer version;

    /** 逻辑删除标记 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
