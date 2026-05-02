package com.taolife.wisdom.param;

import lombok.Data;

/**
 * 运动项目查询参数（用户端）
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class ExerciseQueryParam {

    private Integer category;
    private Integer intensity;
    private String keyword;
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
