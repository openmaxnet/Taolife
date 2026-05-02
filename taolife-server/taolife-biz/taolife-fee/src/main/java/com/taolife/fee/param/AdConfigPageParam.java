package com.taolife.fee.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 广告配置分页查询参数
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class AdConfigPageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码 */
    private Integer pageNo = 1;
    /** 每页条数 */
    private Integer pageSize = 10;
    /** 配置键（筛选条件） */
    private String configKey;
    /** 广告类型（筛选条件） */
    private Integer adType;
}
