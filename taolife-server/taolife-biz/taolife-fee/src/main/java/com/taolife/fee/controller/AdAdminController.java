package com.taolife.fee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.AdConfigPageParam;
import com.taolife.fee.param.AdConfigSaveParam;
import com.taolife.fee.param.AdLogPageParam;
import com.taolife.fee.service.IAdConfigAdminService;
import com.taolife.fee.service.IAdDisplayLogAdminService;
import com.taolife.fee.vo.AdConfigAdminVO;
import com.taolife.fee.vo.AdLogAdminVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 广告配置管理控制器
 * 负责处理管理端广告配置相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/fee/admin/ad")
@RequiredArgsConstructor
public class AdAdminController {

    private final IAdConfigAdminService adConfigAdminService;
    private final IAdDisplayLogAdminService adDisplayLogAdminService;

    /**
     * 分页获取广告配置列表
     * 根据条件分页获取广告配置信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getAdConfigPage")
    public ExceptionResult<PageResult<AdConfigAdminVO>> getAdConfigPage(AdConfigPageParam param) {
        log.info("分页查询广告配置，参数：{}", param);
        return ExceptionResult.success(adConfigAdminService.getAdConfigPage(param));
    }

    /**
     * 获取广告配置详情
     * 根据ID获取广告配置详细信息
     *
     * @param id 广告配置ID
     * @return 广告配置详情
     */
    @GetMapping("/getAdConfig")
    public ExceptionResult<AdConfigAdminVO> getAdConfig(@RequestParam("id") String id) {
        log.info("获取广告配置详情，id：{}", id);
        return ExceptionResult.success(adConfigAdminService.getAdConfig(id));
    }

    /**
     * 创建广告配置
     * 新增一条广告配置记录
     *
     * @param param 广告配置参数
     * @return 操作结果
     */
    @PostMapping("/createAdConfig")
    public ExceptionResult<Void> createAdConfig(@Valid @RequestBody AdConfigSaveParam param) {
        log.info("创建广告配置，参数：{}", param);
        adConfigAdminService.createAdConfig(param);
        return ExceptionResult.success();
    }

    /**
     * 修改广告配置
     * 根据ID修改广告配置信息
     *
     * @param id 广告配置ID
     * @param param 广告配置参数
     * @return 操作结果
     */
    @PostMapping("/modifyAdConfig")
    public ExceptionResult<Void> modifyAdConfig(@RequestParam("id") String id,
                                               @Valid @RequestBody AdConfigSaveParam param) {
        log.info("修改广告配置，id：{}，参数：{}", id, param);
        adConfigAdminService.modifyAdConfig(id, param);
        return ExceptionResult.success();
    }

    /**
     * 修改广告配置状态
     * 启用或禁用广告配置
     *
     * @param id 广告配置ID
     * @param isEnabled 是否启用（0-禁用，1-启用）
     * @return 操作结果
     */
    @PostMapping("/modifyAdConfigStatus")
    public ExceptionResult<Void> modifyAdConfigStatus(@RequestParam("id") String id,
                                                      @RequestParam("isEnabled") Integer isEnabled) {
        log.info("修改广告配置状态，id：{}，isEnabled：{}", id, isEnabled);
        adConfigAdminService.modifyAdConfigStatus(id, isEnabled);
        return ExceptionResult.success();
    }

    /**
     * 删除广告配置
     * 根据ID删除广告配置（逻辑删除）
     *
     * @param id 广告配置ID
     * @return 操作结果
     */
    @PostMapping("/removeAdConfig")
    public ExceptionResult<Void> removeAdConfig(@RequestParam("id") String id) {
        log.info("删除广告配置，id：{}", id);
        adConfigAdminService.removeAdConfig(id);
        return ExceptionResult.success();
    }

    /**
     * 分页获取广告日志列表
     * 根据条件分页获取广告日志信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getAdLogPage")
    public ExceptionResult<PageResult<AdLogAdminVO>> getAdLogPage(AdLogPageParam param) {
        log.info("分页查询广告日志，参数：{}", param);
        return ExceptionResult.success(adDisplayLogAdminService.getAdLogPage(param));
    }
}
