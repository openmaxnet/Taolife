package com.taolife.identity.param;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 身体数据偏好参数
 */
@Data
public class HealthProfileParam {

    /** 身高(cm) */
    private BigDecimal height;

    /** 体重(kg) */
    private BigDecimal weight;

    /** 血型：1-A 2-B 3-AB 4-O */
    private Integer bloodType;

    /** 过敏史 */
    private String allergyHistory;

    /** 病史 */
    private String medicalHistory;
}
