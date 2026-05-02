package com.taolife.fee.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 打卡记录实体
 * 对应数据库表 tf_checkin_record
 *
 * @author 文二
 * @date 2026-04-02
 */
@Data
@Table("tl_fee_checkin_record")
public class CheckinRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 打卡日期
     */
    private LocalDate checkinDate;

    /**
     * 总签到天数
     */
    private Integer totalCheckinDays;

    /**
     * 连续签到天数
     */
    private Integer consecutiveCheckinDays;

    /**
     * 获得积分
     */
    private Integer pointsEarned;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
