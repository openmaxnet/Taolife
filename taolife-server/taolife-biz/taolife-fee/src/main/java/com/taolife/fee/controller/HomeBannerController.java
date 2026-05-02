package com.taolife.fee.controller;

import com.taolife.common.annotation.AuthSkip;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.fee.service.IHomeBannerService;
import com.taolife.fee.vo.HomeBannerVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 首页轮播控制器（用户端）
 *
 * @author 文二
 * @date 2026-04-20
 */
@Slf4j
@RestController
@RequestMapping("/api/fee/banner")
@RequiredArgsConstructor
@AuthSkip
public class HomeBannerController {

    private final IHomeBannerService homeBannerService;

    /**
     * 获取首页轮播列表
     *
     * @return 启用的轮播列表
     */
    @GetMapping("/getBannerList")
    public ExceptionResult<List<HomeBannerVO>> getBannerList() {
        log.info("获取首页轮播列表");
        return ExceptionResult.success(homeBannerService.getEnabledBanners());
    }
}
