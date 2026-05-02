package com.taolife.aichat.controller;

import com.taolife.aichat.param.AiSceneConfigPageParam;
import com.taolife.aichat.param.AiSceneConfigSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.service.IAiConfigService;
import com.taolife.aichat.service.IAiSceneConfigService;
import com.taolife.aichat.vo.AiSceneConfigVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员AI场景配置管理控制器
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/ai/admin/scene")
@RequiredArgsConstructor
public class AiSceneConfigAdminController {

    private final IAiSceneConfigService aiSceneConfigService;
    private final IAiConfigService aiConfigService;

    /**
     * 分页查询场景配置列表
     *
     * @param param 分页查询参数
     * @return 场景分页结果
     */
    @GetMapping("/getScenePage")
    public ExceptionResult<PageResult<AiSceneConfigVO>> getScenePage(AiSceneConfigPageParam param) {
        return ExceptionResult.success(aiSceneConfigService.getScenePage(param));
    }

    /**
     * 获取场景配置详情
     *
     * @param id 场景配置ID
     * @return 场景详情
     */
    @GetMapping("/getSceneDetail")
    public ExceptionResult<AiSceneConfigVO> getSceneDetail(@RequestParam String id) {
        return ExceptionResult.success(aiSceneConfigService.getSceneDetail(id));
    }

    /**
     * 创建场景配置
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createScene")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> createScene(@RequestBody AiSceneConfigSaveParam param) {
        aiSceneConfigService.createScene(param);
        // 清除场景缓存
        aiConfigService.evictSceneConfig(param.getSceneCode());
        return ExceptionResult.success();
    }

    /**
     * 修改场景配置信息
     *
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifySceneInfo")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> modifySceneInfo(@RequestBody AiSceneConfigSaveParam param) {
        aiSceneConfigService.modifySceneInfo(param);
        // 清除场景缓存
        aiConfigService.evictSceneConfig(param.getSceneCode());
        return ExceptionResult.success();
    }

    /**
     * 删除场景配置
     *
     * @param param 删除参数
     * @return 操作结果
     */
    @PostMapping("/removeScene")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> removeScene(@RequestBody AiRemoveParam param) {
        // 需要先获取sceneCode再删除
        AiSceneConfigVO scene = aiSceneConfigService.getSceneDetail(param.getId());
        aiSceneConfigService.removeScene(param);
        // 清除场景缓存
        if (scene != null) {
            aiConfigService.evictSceneConfig(scene.getSceneCode());
        }
        return ExceptionResult.success();
    }

    /**
     * 修改场景配置状态
     *
     * @param param 状态修改参数
     * @return 操作结果
     */
    @PostMapping("/modifySceneStatus")
    @Transactional(rollbackFor = Exception.class)
    public ExceptionResult<Void> modifySceneStatus(@RequestBody AiStatusParam param) {
        // 需要先获取sceneCode再修改状态
        AiSceneConfigVO scene = aiSceneConfigService.getSceneDetail(param.getId());
        aiSceneConfigService.modifySceneStatus(param);
        // 清除场景缓存
        if (scene != null) {
            aiConfigService.evictSceneConfig(scene.getSceneCode());
        }
        return ExceptionResult.success();
    }

    /**
     * 根据场景编码获取场景配置
     *
     * @param sceneCode 场景编码
     * @return 场景配置详情
     */
    @GetMapping("/getSceneByCode")
    public ExceptionResult<AiSceneConfigVO> getSceneByCode(@RequestParam String sceneCode) {
        return ExceptionResult.success(aiSceneConfigService.getBySceneCode(sceneCode));
    }

    /**
     * 获取所有启用的场景配置
     *
     * @return 场景配置列表
     */
    @GetMapping("/getEnabledSceneList")
    public ExceptionResult<List<AiSceneConfigVO>> getEnabledSceneList() {
        return ExceptionResult.success(aiSceneConfigService.getAllEnabled());
    }
}