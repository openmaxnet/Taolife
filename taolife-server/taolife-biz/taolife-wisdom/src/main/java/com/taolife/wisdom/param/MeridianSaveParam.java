package com.taolife.wisdom.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 经络保存参数
 * 用于创建和修改经络
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class MeridianSaveParam {

    @NotBlank(message = "经络编码不能为空")
    private String code;

    @NotBlank(message = "经络名称不能为空")
    private String name;

    private String namePinyin;

    @NotNull(message = "分类不能为空")
    private Integer category;

    private String description;
    private String pathDescription;
    private String mainIndications;
    private String lineColor;
    private BigDecimal lineWidth;
    private Integer sortOrder;
}
