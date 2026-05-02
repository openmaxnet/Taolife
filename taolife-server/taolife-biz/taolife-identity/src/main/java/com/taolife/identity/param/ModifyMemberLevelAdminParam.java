package com.taolife.identity.param;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员修改会员等级参数
 *
 * @author 文二
 * @date 2026-04-13
 */
@Data
public class ModifyMemberLevelAdminParam {

    /**
     * 账号ID
     */
    private String id;

    /**
     * 会员等级：0-普通用户，1-月卡会员，2-年卡会员，3-终身会员
     */
    private Integer memberLevel;

    /**
     * 会员过期时间（等级为0或3时可为null）
     */
    private LocalDateTime memberExpireTime;
}
