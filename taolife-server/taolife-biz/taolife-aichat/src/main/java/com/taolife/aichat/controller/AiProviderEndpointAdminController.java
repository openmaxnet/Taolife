package com.taolife.aichat.controller;

import com.taolife.aichat.param.AiProviderEndpointPageParam;
import com.taolife.aichat.param.AiProviderEndpointSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aichat.service.IAiProviderEndpointService;
import com.taolife.aichat.vo.AiProviderEndpointVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员AI厂商端点配置管理控制器
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/ai/admin/endpoint")
@RequiredArgsConstructor
public class AiProviderEndpointAdminController {

    private final IAiProviderEndpointService aiProviderEndpointService;
    private final IAiConfigService aiConfigService;

    /**
     * 分页查询端点配置列表
     *
     * @param param 分页查询参数
     * @return 端点分页结果
     */
    @GetMapping("/getEndpointPage")
    public ExceptionResult<PageResult<AiProviderEndpointVO>> getEndpointPage(AiProviderEndpointPageParam param) {
        return ExceptionResult.success(aiProviderEndpointService.getEndpointPage(param));
    }

    /**
     * 获取端点配置详情
     *
     * @param id 端点配置ID
     * @return 端点详情
     */
    @GetMapping("/getEndpointDetail")
    public ExceptionResult<AiProviderEndpointVO> getEndpointDetail(@RequestParam String id) {
        return ExceptionResult.success(aiProviderEndpointService.getEndpointDetail(id));
    }

    /**
     * 创建端点配置
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createEndpoint")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> createEndpoint(@RequestBody AiProviderEndpointSaveParam param) {
        aiProviderEndpointService.createEndpoint(param);
        // 清除相关缓存（端点变更影响GLM和Embedding配置）
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 修改端点配置信息
     *
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyEndpointInfo")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> modifyEndpointInfo(@RequestBody AiProviderEndpointSaveParam param) {
        aiProviderEndpointService.modifyEndpointInfo(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 删除端点配置
     *
     * @param param 删除参数
     * @return 操作结果
     */
    @PostMapping("/removeEndpoint")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> removeEndpoint(@RequestBody AiRemoveParam param) {
        aiProviderEndpointService.removeEndpoint(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 修改端点配置状态
     *
     * @param param 状态修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyEndpointStatus")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> modifyEndpointStatus(@RequestBody AiStatusParam param) {
        aiProviderEndpointService.modifyEndpointStatus(param);
        // 清除相关缓存
        aiConfigService.evictChatConfig(null);
        aiConfigService.evictEmbeddingConfig();
        return ExceptionResult.success();
    }

    /**
     * 根据厂商ID获取所有启用的端点
     *
     * @param providerId 厂商ID
     * @return 端点列表
     */
    @GetMapping("/getEndpointListByProviderId")
    public ExceptionResult<List<AiProviderEndpointVO>> getEndpointListByProviderId(@RequestParam String providerId) {
        return ExceptionResult.success(aiProviderEndpointService.getEndpointsByProviderId(providerId));
    }

    /**
     * 根据厂商ID和端点类型获取端点
     *
     * @param providerId   厂商ID
     * @param endpointType 端点类型
     * @return 端点详情
     */
    @GetMapping("/getEndpointByProviderIdAndType")
    public ExceptionResult<AiProviderEndpointVO> getEndpointByProviderIdAndType(
            @RequestParam String providerId,
            @RequestParam String endpointType) {
        return ExceptionResult.success(aiProviderEndpointService.getEndpointByProviderIdAndType(providerId, endpointType));
    }
}