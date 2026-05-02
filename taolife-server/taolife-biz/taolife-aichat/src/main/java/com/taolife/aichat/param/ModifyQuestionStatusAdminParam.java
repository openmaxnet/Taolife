package com.taolife.aichat.param;

import lombok.Data;

/**
 * 管理员体质问卷题目状态修改参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class ModifyQuestionStatusAdminParam {

    private String id;

    private Integer isDisabled;
}
