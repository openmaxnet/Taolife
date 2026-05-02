package com.taolife.aichat.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员批量导入参数
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class ImportAdminParam {

    private MultipartFile file;
}
