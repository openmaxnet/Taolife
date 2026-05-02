package com.taolife.wisdom.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 穴位列表VO
 * 用于返回穴位列表数据
 *
 * @author 文二
 * @date 2026-03-24
 */
@Data
public class AcupointListVO implements Serializable {

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
    private Integer markerType;
    private String markerTypeName;
}
