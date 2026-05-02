package com.taolife.fee.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员成长值变动记录实体
 * 对应数据库表 tf_member_growth_record
 *
 * @author 文二
 * @date 2026-04-18
 */
@Data
@Table("tl_fee_growth_record")
public class MemberGrowthRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /** 账号ID */
    private String accountId;

    /** 成长值变动数量（正数增加，负数减少） */
    private Integer growthChange;

    /** 成长值来源：1-每日登录，2-签到，3-完成任务，4-开通续费，5-手动调整 */
    private Integer growthSource;

    /** 业务类型：SUBSCRIPTION / TASK / CHECKIN 等 */
    private String businessType;

    /** 业务记录ID */
    private String businessId;

    /** 备注 */
    private String remark;

    /** 变动后成长值 */
    private Integer growthValueAfter;

    /** 是否删除：0-否，1-是 */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;
}
