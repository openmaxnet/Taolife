package com.taolife.identity.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 管理员小程序账号详情视图对象
 *
 * @author 文二
 * @date 2026-04-13
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountDetailAdminVO extends AccountAdminVO {

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;

    /**
     * 身高(cm)
     */
    private BigDecimal height;

    /**
     * 体重(kg)
     */
    private BigDecimal weight;

    /**
     * 血型：1-A型，2-B型，3-AB型，4-O型
     */
    private Integer bloodType;

    /**
     * 过敏史
     */
    private String allergyHistory;

    /**
     * 病史
     */
    private String medicalHistory;
}
