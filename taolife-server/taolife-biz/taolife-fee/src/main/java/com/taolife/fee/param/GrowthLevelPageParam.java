package com.taolife.fee.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 成长等级分页查询参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class GrowthLevelPageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码 */
    private Integer pageNo = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 等级值（筛选条件） */
    private Integer level;

    /** 等级名称（筛选条件） */
    private String levelName;
}
