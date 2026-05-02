package com.taolife.fee.param;

import lombok.Data;

/**
 * 管理员轮播分页查询参数
 *
 * @author 文二
 * @date 2026-04-20
 */
@Data
public class BannerPageAdminParam {

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 20;

    /**
     * 状态筛选：0-禁用，1-启用
     */
    private Integer status;
}
