package com.taolife.identity.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统协议保存参数
 *
 * @author 文二
 * @date 2026-04-28
 */
@Data
public class SysAgreementSaveParam {

    /** 文档标识 */
    @NotBlank(message = "文档标识不能为空")
    private String code;

    /** 标题 */
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 内容 */
    private String content;
}
