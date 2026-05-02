package com.taolife.wisdom.param;

import lombok.Data;

/**
 * 管理员节气分页查询参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class SolarTermPageAdminParam {

    /** 页码 */
    private Integer pageNo;
    /** 每页条数 */
    private Integer pageSize;
}