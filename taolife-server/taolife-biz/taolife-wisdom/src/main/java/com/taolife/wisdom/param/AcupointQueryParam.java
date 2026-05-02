package com.taolife.wisdom.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 穴位查询参数
 * 用于分页查询穴位列表
 *
 * @author 文二
 * @date 2026-03-24
 */
@Data
public class AcupointQueryParam {

    private String meridianCode;

    @Min(value = 0, message = "标注类型值不正确")
    @Max(value = 3, message = "标注类型值不正确")
    private Integer markerType;

    private String keyword;

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 50, message = "每页数量不能大于50")
    private Integer pageSize = 10;
}
