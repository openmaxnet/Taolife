package com.taolife.aichat.controller;

import com.taolife.aichat.param.AiProviderPageParam;
import com.taolife.aichat.param.AiProviderSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aichat.service.IAiProviderService;
import com.taolife.aichat.vo.AiProviderVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员AI模型厂商管理控制器
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/ai/admin/provider")
@RequiredArgsConstructor
public class AiProviderAdminController {

    private final IAiProviderService aiProviderService;
    private final IAiConfigService aiConfigService;

    /**
     * 分页查询厂商列表
     *
     * @param param 分页查询参数
     * @return 厂商分页结果
     */
    @GetMapping("/getProviderPage")
    public ExceptionResult<PageResult<AiProviderVO>> getProviderPage(AiProviderPageParam param) {
        return ExceptionResult.success(aiProviderService.getProviderPage(param));
    }

    /**
     * 获取厂商详情
     *
     * @param id 厂商ID
     * @return 厂商详情
     */
    @GetMapping("/getProviderDetail")
    public ExceptionResult<AiProviderVO> getProviderDetail(@RequestParam String id) {
        return ExceptionResult.success(aiProviderService.getProviderDetail(id));
    }

    /**
     * 创建厂商
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createProvider")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> createProvider(@RequestBody AiProviderSaveParam param) {
        aiProviderService.createProvider(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 修改厂商信息
     *
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyProviderInfo")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> modifyProviderInfo(@RequestBody AiProviderSaveParam param) {
        aiProviderService.modifyProviderInfo(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 删除厂商
     *
     * @param param 删除参数
     * @return 操作结果
     */
    @PostMapping("/removeProvider")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> removeProvider(@RequestBody AiRemoveParam param) {
        aiProviderService.removeProvider(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 修改厂商状态
     *
     * @param param 状态修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyProviderStatus")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> modifyProviderStatus(@RequestBody AiStatusParam param) {
        aiProviderService.modifyProviderStatus(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 根据类型获取启用的厂商列表
     *
     * @param providerType 厂商类型
     * @return 厂商列表
     */
    @GetMapping("/getEnabledProviderList")
    public ExceptionResult<List<AiProviderVO>> getEnabledProvidersByType(
        @RequestParam(required = false) String providerType) {
        return ExceptionResult.success(aiProviderService.getEnabledProvidersByType(providerType));
    }
}
