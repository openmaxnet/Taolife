package com.taolife.aichat.vo;

import lombok.Data;

import java.util.List;

/**
 * 批量导入结果VO
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class ImportResultVO {

    /**
     * 总数
     */
    private Integer totalCount;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 失败数
     */
    private Integer failCount;

    /**
     * 失败原因列表
     */
    private List<String> failList;
}
