package com.taolife.wisdom.param;

import lombok.Data;

/**
 * 管理员体质问卷题目状态修改参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class ModifyQuestionStatusAdminParam {

    /** 题目ID */
    private String id;
    /** 禁用标记：0-启用，1-禁用 */
    private Integer isDisabled;
}