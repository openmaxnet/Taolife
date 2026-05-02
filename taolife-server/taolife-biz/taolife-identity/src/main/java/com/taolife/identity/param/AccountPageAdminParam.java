package com.taolife.identity.param;

import lombok.Data;

/**
 * 管理员小程序账号分页查询参数
 *
 * @author 文二
 * @date 2026-04-13
 */
@Data
public class AccountPageAdminParam {

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 关键词（手机号/昵称模糊搜索）
     */
    private String keyword;

    /**
     * 会员等级
     */
    private Integer memberLevel;

    /**
     * 注册开始时间
     */
    private String startDate;

    /**
     * 注册结束时间
     */
    private String endDate;
}
