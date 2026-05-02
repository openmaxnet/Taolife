package com.taolife.fee.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 广告日志分页查询参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class AdLogPageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码 */
    private Integer pageNo = 1;
    /** 每页条数 */
    private Integer pageSize = 10;
    /** 用户账号ID（筛选条件） */
    private String accountId;
    /** 广告类型（筛选条件） */
    private Integer adType;
    /** 动作类型（筛选条件） */
    private Integer action;
}
