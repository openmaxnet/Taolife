package com.taolife.wisdom.param;

import lombok.Data;

/**
 * 食材保存参数
 * 用于创建和修改食材信息
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class FoodSaveParam {

    private String name;
    private String namePinyin;
    private Integer category;
    private Integer nature;
    private String flavor;
    private String meridianEntry;
    private String efficacy;
    private String indications;
    private String contraindications;
    private String usage;
    private String recipes;
    private String imageUrl;
    private Integer sortOrder;
}
