package com.taolife.fee.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 签到日历参数
 * 用于查询签到日历
 *
 * @author 文二
 * @date 2026-04-02
 */
@Data
public class CheckinCalendarParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 年份
     */
    @NotNull(message = "年份不能为空")
    @Min(value = 2020, message = "年份不能小于2020")
    @Max(value = 2100, message = "年份不能大于2100")
    private Integer year;

    /**
     * 月份
     */
    @NotNull(message = "月份不能为空")
    @Min(value = 1, message = "月份不能小于1")
    @Max(value = 12, message = "月份不能大于12")
    private Integer month;
}
