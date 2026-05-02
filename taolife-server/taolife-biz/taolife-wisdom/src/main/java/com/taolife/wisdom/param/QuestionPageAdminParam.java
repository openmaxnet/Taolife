package com.taolife.wisdom.param;

import lombok.Data;

/**
 * 管理员体质问卷题目分页查询参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class QuestionPageAdminParam {

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 所属模式：1-简易模式，2-精细模式
     */
    private Integer questionMode;
}