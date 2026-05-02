package com.taolife.fee.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员套餐分页查询参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class MemberPlanPageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码 */
    private Integer pageNo = 1;
    /** 每页条数 */
    private Integer pageSize = 10;
    /** 套餐代码（筛选条件） */
    private String planCode;
    /** 是否启用（筛选条件） */
    private Integer isEnabled;
}
