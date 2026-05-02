package com.taolife.wisdom.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 穴位保存参数
 * 用于创建和修改穴位
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class AcupointSaveParam {

    @NotBlank(message = "穴位名称不能为空")
    private String name;

    private String namePinyin;

    @NotNull(message = "分类不能为空")
    private Integer category;

    @NotBlank(message = "经络编码不能为空")
    private String meridianCode;

    private String meridianName;
    private String locationDescription;
    private String efficacy;
    private String indications;
    private String operationMethod;
    private String massageTips;
    private Integer markerType;
    private Integer sortOrder;
}
