package com.taolife.aichat.param;

import lombok.Data;

/**
 * 管理员卡片分页查询参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class CardPageAdminParam {

    private Integer pageNo;

    private Integer pageSize;

    private String solarTermId;
}