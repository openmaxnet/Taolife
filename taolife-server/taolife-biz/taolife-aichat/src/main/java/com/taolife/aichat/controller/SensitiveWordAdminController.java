package com.taolife.aichat.controller;

import com.taolife.aichat.param.*;
import com.taolife.aichat.param.ImportAdminParam;
import com.taolife.aichat.service.ISensitiveWordAdminService;
import com.taolife.aichat.vo.SensitiveWordAdminVO;
import com.taolife.aichat.vo.ImportResultVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员敏感词管理控制器
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/ai/admin/sensitive")
@RequiredArgsConstructor
public class SensitiveWordAdminController {

    private final ISensitiveWordAdminService adminSensitiveWordService;

    /**
     * 分页查询敏感词列表
     *
     * @param param 分页查询参数
     * @return 敏感词分页结果
     */
    @GetMapping("/getSensitiveWordPage")
    public ExceptionResult<PageResult<SensitiveWordAdminVO>> getSensitiveWordPage(SensitiveWordPageAdminParam param) {
        return ExceptionResult.success(adminSensitiveWordService.getSensitiveWordPage(param));
    }

    /**
     * 获取敏感词详情
     *
     * @param param 详情查询参数
     * @return 敏感词详情
     */
    @GetMapping("/getSensitiveWordDetail")
    public ExceptionResult<SensitiveWordAdminVO> getSensitiveWordDetail(SensitiveWordDetailAdminParam param) {
        return ExceptionResult.success(adminSensitiveWordService.getSensitiveWordDetail(param));
    }

    /**
     * 创建敏感词
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createSensitiveWord")
    public ExceptionResult<Void> createSensitiveWord(@RequestBody SensitiveWordSaveAdminParam param) {
        adminSensitiveWordService.createSensitiveWord(param);
        return ExceptionResult.success();
    }

    /**
     * 修改敏感词信息
     *
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifySensitiveWordInfo")
    public ExceptionResult<Void> modifySensitiveWordInfo(@RequestBody SensitiveWordSaveAdminParam param) {
        adminSensitiveWordService.modifySensitiveWordInfo(param);
        return ExceptionResult.success();
    }

    /**
     * 删除敏感词
     *
     * @param param 删除参数
     * @return 操作结果
     */
    @PostMapping("/removeSensitiveWord")
    public ExceptionResult<Void> removeSensitiveWord(RemoveSensitiveWordAdminParam param) {
        adminSensitiveWordService.removeSensitiveWord(param);
        return ExceptionResult.success();
    }

    /**
     * 修改敏感词状态
     *
     * @param param 状态修改参数
     * @return 操作结果
     */
    @PostMapping("/modifySensitiveWordStatus")
    public ExceptionResult<Void> modifySensitiveWordStatus(ModifySensitiveWordStatusAdminParam param) {
        adminSensitiveWordService.modifySensitiveWordStatus(param);
        return ExceptionResult.success();
    }

    /**
     * 批量导入敏感词（Excel）
     *
     * @param param 导入参数（含Excel文件）
     * @return 导入结果
     */
    @PostMapping("/importSensitiveWords")
    public ExceptionResult<ImportResultVO> importSensitiveWords(ImportAdminParam param) {
        return ExceptionResult.success(adminSensitiveWordService.importSensitiveWords(param));
    }
}