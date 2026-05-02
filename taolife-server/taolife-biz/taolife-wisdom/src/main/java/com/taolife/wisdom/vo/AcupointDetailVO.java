package com.taolife.wisdom.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 穴位详情VO
 * 用于返回穴位详细信息
 *
 * @author 文二
 * @date 2026-03-24
 */
@Data
public class AcupointDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String namePinyin;
    private Integer category;
    private String categoryName;
    private String meridianCode;
    private String meridianName;
    private String locationDescription;
    private String efficacy;
    private String indications;
    private String operationMethod;
    private String massageTips;
    private Integer markerType;
    private String markerTypeName;
    private Integer sortOrder;
}
