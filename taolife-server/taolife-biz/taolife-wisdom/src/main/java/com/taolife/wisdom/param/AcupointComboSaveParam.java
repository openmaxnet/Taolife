package com.taolife.wisdom.param;

import lombok.Data;

/**
 * 穴位配伍保存参数
 * 用于创建和修改穴位配伍信息
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class AcupointComboSaveParam {

    private String name;
    private Integer category;
    private String targetConstitutionCodes;
    private String targetSeason;
    private String targetSymptom;
    private String description;
    private String acupointIds;
    private String acupointNames;
    private Integer sequence;
    private String operationMethod;
    private Integer durationMin;
    private Integer frequencyPerDay;
    private String efficacy;
    private String indications;
    private String contraindications;
    private String imageUrl;
    private String videoUrl;
    private Integer sortOrder;
}
