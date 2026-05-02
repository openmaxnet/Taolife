package com.taolife.wisdom.controller;

import com.taolife.wisdom.param.*;
import com.taolife.wisdom.service.ISolarTermAdminService;
import com.taolife.wisdom.vo.SolarTermAdminVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员节气管理控制器
 *
 * @author 文二
 * @date 2026-04-20
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/wisdom/admin/solarterm")
@RequiredArgsConstructor
public class SolarTermAdminController {

    private final ISolarTermAdminService adminSolarTermService;

    /**
     * 分页查询节气列表
     *
     * @param param 分页查询参数
     * @return 节气分页结果
     */
    @GetMapping("/getSolarTermPage")
    public ExceptionResult<PageResult<SolarTermAdminVO>> getSolarTermPage(SolarTermPageAdminParam param) {
        return ExceptionResult.success(adminSolarTermService.getSolarTermPage(param));
    }

    /**
     * 获取节气详情
     *
     * @param param 详情查询参数（含ID）
     * @return 节气详情
     */
    @GetMapping("/getSolarTermDetail")
    public ExceptionResult<SolarTermAdminVO> getSolarTermDetail(SolarTermDetailAdminParam param) {
        return ExceptionResult.success(adminSolarTermService.getSolarTermDetail(param));
    }

    /**
     * 创建节气
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createSolarTerm")
    public ExceptionResult<Void> createSolarTerm(SolarTermSaveAdminParam param) {
        adminSolarTermService.createSolarTerm(param);
        return ExceptionResult.success();
    }

    /**
     * 修改节气信息
     *
     * @param param 修改参数（含ID）
     * @return 操作结果
     */
    @PostMapping("/modifySolarTermInfo")
    public ExceptionResult<Void> modifySolarTermInfo(SolarTermSaveAdminParam param) {
        adminSolarTermService.modifySolarTermInfo(param);
        return ExceptionResult.success();
    }

    /**
     * 删除节气（逻辑删除）
     *
     * @param param 删除参数（含ID）
     * @return 操作结果
     */
    @PostMapping("/removeSolarTerm")
    public ExceptionResult<Void> removeSolarTerm(RemoveSolarTermAdminParam param) {
        adminSolarTermService.removeSolarTerm(param);
        return ExceptionResult.success();
    }
}
