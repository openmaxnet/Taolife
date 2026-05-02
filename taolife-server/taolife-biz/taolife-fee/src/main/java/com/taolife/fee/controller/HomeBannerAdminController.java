package com.taolife.fee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.BannerPageAdminParam;
import com.taolife.fee.param.BannerSaveAdminParam;
import com.taolife.fee.service.IHomeBannerService;
import com.taolife.fee.vo.BannerAdminVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 首页轮播管理控制器（管理端）
 *
 * @author 文二
 * @date 2026-04-20
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/fee/admin/banner")
@RequiredArgsConstructor
public class HomeBannerAdminController {

    private final IHomeBannerService homeBannerService;

    /**
     * 分页获取轮播列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getBannerPage")
    public ExceptionResult<PageResult<BannerAdminVO>> getBannerPage(BannerPageAdminParam param) {
        log.info("管理端分页获取轮播列表，参数：{}", param);
        return ExceptionResult.success(homeBannerService.getBannerPage(param));
    }

    /**
     * 获取轮播详情
     *
     * @param id 轮播ID
     * @return 轮播详情
     */
    @GetMapping("/getBannerDetail")
    public ExceptionResult<BannerAdminVO> getBannerDetail(@RequestParam("id") String id) {
        log.info("管理端获取轮播详情，id：{}", id);
        return ExceptionResult.success(homeBannerService.getBannerDetail(id));
    }

    /**
     * 创建轮播
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createBanner")
    public ExceptionResult<Void> createBanner(@RequestBody BannerSaveAdminParam param) {
        log.info("管理端创建轮播，参数：{}", param);
        homeBannerService.createBanner(param);
        return ExceptionResult.success();
    }

    /**
     * 修改轮播信息
     *
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyBannerInfo")
    public ExceptionResult<Void> modifyBannerInfo(@RequestBody BannerSaveAdminParam param) {
        log.info("管理端修改轮播信息，参数：{}", param);
        homeBannerService.modifyBannerInfo(param);
        return ExceptionResult.success();
    }

    /**
     * 删除轮播（逻辑删除）
     *
     * @param id 轮播ID
     * @return 操作结果
     */
    @PostMapping("/removeBanner")
    public ExceptionResult<Void> removeBanner(@RequestParam("id") String id) {
        log.info("管理端删除轮播，id：{}", id);
        homeBannerService.removeBanner(id);
        return ExceptionResult.success();
    }

    /**
     * 修改轮播状态
     *
     * @param id     轮播ID
     * @param status 状态（0：禁用，1：启用）
     * @return 操作结果
     */
    @PostMapping("/modifyBannerStatus")
    public ExceptionResult<Void> modifyBannerStatus(@RequestParam("id") String id,
                                                      @RequestParam("status") Integer status) {
        log.info("管理端修改轮播状态，id：{}，status：{}", id, status);
        homeBannerService.modifyBannerStatus(id, status);
        return ExceptionResult.success();
    }
}
