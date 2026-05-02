package com.taolife.aichat.controller;

import com.taolife.aichat.param.AiPromptTemplatePageParam;
import com.taolife.aichat.param.AiPromptTemplateSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aichat.service.IAiPromptTemplateService;
import com.taolife.aichat.vo.AiPromptTemplateVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员AI提示词模板管理控制器
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/ai/admin/prompt")
@RequiredArgsConstructor
public class AiPromptTemplateAdminController {

    private final IAiPromptTemplateService aiPromptTemplateService;
    private final IAiConfigService aiConfigService;

    /**
     * 分页查询提示词模板列表
     *
     * @param param 分页查询参数
     * @return 模板分页结果
     */
    @GetMapping("/getTemplatePage")
    public ExceptionResult<PageResult<AiPromptTemplateVO>> getTemplatePage(AiPromptTemplatePageParam param) {
        return ExceptionResult.success(aiPromptTemplateService.getTemplatePage(param));
    }

    /**
     * 获取提示词模板详情
     *
     * @param id 模板ID
     * @return 模板详情
     */
    @GetMapping("/getTemplateDetail")
    public ExceptionResult<AiPromptTemplateVO> getTemplateDetail(@RequestParam String id) {
        return ExceptionResult.success(aiPromptTemplateService.getTemplateDetail(id));
    }

    /**
     * 创建提示词模板
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createTemplate")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> createTemplate(@RequestBody AiPromptTemplateSaveParam param) {
        aiPromptTemplateService.createTemplate(param);
        // 清除模板缓存
        aiConfigService.evictTemplate(param.getTemplateCode());
        return ExceptionResult.success();
    }

    /**
     * 修改提示词模板信息
     *
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyTemplateInfo")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> modifyTemplateInfo(@RequestBody AiPromptTemplateSaveParam param) {
        aiPromptTemplateService.modifyTemplateInfo(param);
        // 清除模板缓存
        aiConfigService.evictTemplate(param.getTemplateCode());
        return ExceptionResult.success();
    }

    /**
     * 删除提示词模板
     *
     * @param param 删除参数
     * @return 操作结果
     */
    @PostMapping("/removeTemplate")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> removeTemplate(@RequestBody AiRemoveParam param) {
        // 需要先获取templateCode再删除，因为删除后无法获取
        AiPromptTemplateVO template = aiPromptTemplateService.getTemplateDetail(param.getId());
        aiPromptTemplateService.removeTemplate(param);
        // 清除模板缓存
        if (template != null) {
            aiConfigService.evictTemplate(template.getTemplateCode());
        }
        return ExceptionResult.success();
    }

    /**
     * 修改提示词模板状态
     *
     * @param param 状态修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyTemplateStatus")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> modifyTemplateStatus(@RequestBody AiStatusParam param) {
        // 需要先获取templateCode再修改状态
        AiPromptTemplateVO template = aiPromptTemplateService.getTemplateDetail(param.getId());
        aiPromptTemplateService.modifyTemplateStatus(param);
        // 清除模板缓存
        if (template != null) {
            aiConfigService.evictTemplate(template.getTemplateCode());
        }
        return ExceptionResult.success();
    }

    /**
     * 根据模板编码获取最新版本模板
     *
     * @param templateCode 模板编码
     * @return 模板详情
     */
    @GetMapping("/getTemplateByCode")
    public ExceptionResult<AiPromptTemplateVO> getTemplateByCode(@RequestParam String templateCode) {
        return ExceptionResult.success(aiPromptTemplateService.getLatestByCode(templateCode));
    }

    /**
     * 渲染提示词模板
     *
     * @param templateCode 模板编码
     * @param variables    模板变量
     * @return 渲染后的文本
     */
    @PostMapping("/render")
    public ExceptionResult<String> renderTemplate(@RequestParam String templateCode, @RequestBody Map<String, Object> variables) {
        return ExceptionResult.success(aiPromptTemplateService.renderTemplate(templateCode, variables));
    }
}