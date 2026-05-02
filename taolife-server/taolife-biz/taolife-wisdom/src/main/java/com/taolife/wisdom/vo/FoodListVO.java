package com.taolife.wisdom.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 食材列表VO
 * 用于返回食材列表数据
 *
 * @author 文二
 * @date 2026-03-20
 */
@Data
public class FoodListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String namePinyin;
    private Integer category;
    private String categoryName;
    private Integer nature;
    private String natureName;
    private String flavor;
    private String meridianEntry;
    private String efficacy;
    private String imageUrl;
    private Integer viewCount;
    private Integer collectCount;
}
