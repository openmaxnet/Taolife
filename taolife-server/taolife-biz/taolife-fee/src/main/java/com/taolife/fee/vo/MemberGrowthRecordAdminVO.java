package com.taolife.fee.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员成长值记录 VO（管理员用）
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
public class MemberGrowthRecordAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private String id;

    /** 账号ID */
    private String accountId;

    /** 成长值变动数量 */
    private Integer growthChange;

    /** 成长值来源：1-每日登录，2-签到，3-完成任务，4-开通续费，5-手动调整 */
    private Integer growthSource;

    /** 业务类型 */
    private String businessType;

    /** 业务记录ID */
    private String businessId;

    /** 备注 */
    private String remark;

    /** 变动后成长值 */
    private Integer growthValueAfter;

    /** 创建时间 */
    private LocalDateTime createTime;
}
