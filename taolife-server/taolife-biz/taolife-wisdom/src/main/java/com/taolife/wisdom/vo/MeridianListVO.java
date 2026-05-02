package com.taolife.wisdom.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 经络列表VO
 * 用于返回经络列表数据
 *
 * @author 文二
 * @date 2026-03-24
 */
@Data
public class MeridianListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String code;
    private String name;
    private String namePinyin;
    private Integer category;
    private String categoryName;
    private String description;
    private String mainIndications;
    private String lineColor;
    private BigDecimal lineWidth;
}
