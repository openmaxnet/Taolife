package com.taolife.wisdom.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 经络详情VO
 * 用于返回经络详细信息
 *
 * @author 文二
 * @date 2026-03-24
 */
@Data
public class MeridianDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String code;
    private String name;
    private String namePinyin;
    private Integer category;
    private String categoryName;
    private String description;
    private String pathDescription;
    private String mainIndications;
    private String lineColor;
    private BigDecimal lineWidth;
    private Integer sortOrder;
}
