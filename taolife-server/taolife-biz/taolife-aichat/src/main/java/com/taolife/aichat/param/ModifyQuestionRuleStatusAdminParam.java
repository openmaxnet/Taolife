package com.taolife.aichat.param;

import lombok.Data;

/**
 * 管理员分类规则状态修改参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class ModifyQuestionRuleStatusAdminParam {

    private String id;

    private Integer isEnabled;
}
