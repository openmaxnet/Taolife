package com.taolife.identity.param;

import lombok.Data;

/**
 * 系统用户分页查询参数
 *
 * @author 文二
 * @date 2026-04-11
 */
@Data
public class SysUserPageParam {

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 真实姓名（模糊查询）
     */
    private String realName;
}