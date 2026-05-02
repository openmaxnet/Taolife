package com.taolife.aichat.controller;

import com.taolife.aichat.param.AiModelPageParam;
import com.taolife.aichat.param.AiModelSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aichat.service.IAiModelService;
import com.taolife.aichat.vo.AiModelVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员AI模型管理控制器
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/ai/admin/model")
@RequiredArgsConstructor
public class AiModelAdminController {

    private final IAiModelService aiModelService;
    private final IAiConfigService aiConfigService;

    /**
     * 分页查询模型列表
     *
     * @param param 分页查询参数
     * @return 模型分页结果
     */
    @GetMapping("/getModelPage")
    public ExceptionResult<PageResult<AiModelVO>> getModelPage(AiModelPageParam param) {
        return ExceptionResult.success(aiModelService.getModelPage(param));
    }

    /**
     * 获取模型详情
     *
     * @param id 模型ID
     * @return 模型详情
     */
    @GetMapping("/getModelDetail")
    public ExceptionResult<AiModelVO> getModelDetail(@RequestParam String id) {
        return ExceptionResult.success(aiModelService.getModelDetail(id));
    }

    /**
     * 创建模型
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createModel")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> createModel(@RequestBody AiModelSaveParam param) {
        aiModelService.createModel(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 修改模型信息
     *
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyModelInfo")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> modifyModelInfo(@RequestBody AiModelSaveParam param) {
        aiModelService.modifyModelInfo(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 删除模型
     *
     * @param param 删除参数
     * @return 操作结果
     */
    @PostMapping("/removeModel")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> removeModel(@RequestBody AiRemoveParam param) {
        aiModelService.removeModel(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 修改模型状态
     *
     * @param param 状态修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyModelStatus")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> modifyModelStatus(@RequestBody AiStatusParam param) {
        aiModelService.modifyModelStatus(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 根据模型编码获取模型
     *
     * @param modelCode 模型编码
     * @return 模型详情
     */
    @GetMapping("/getModelByCode")
    public ExceptionResult<AiModelVO> getModelByCode(@RequestParam String modelCode) {
        return ExceptionResult.success(aiModelService.getModelByCode(modelCode));
    }

    /**
     * 根据厂商ID获取模型列表
     *
     * @param providerId 厂商ID
     * @return 模型列表
     */
    @GetMapping("/getModelListByProviderId")
    public ExceptionResult<List<AiModelVO>> getModelListByProviderId(@RequestParam String providerId) {
        return ExceptionResult.success(aiModelService.getModelListByProviderId(providerId));
    }
}
