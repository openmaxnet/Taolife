package com.taolife.wisdom.controller;

import com.taolife.wisdom.service.ISolarTermService;
import com.taolife.wisdom.vo.SolarTermDetailVO;
import com.taolife.wisdom.vo.SolarTermListVO;
import com.taolife.common.exception.ExceptionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 节气控制器
 * 负责处理节气知识相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-20
 */
@Slf4j
@RestController
@RequestMapping("/api/wisdom/solarterm")
@RequiredArgsConstructor
@Validated
public class SolarTermController {

    private final ISolarTermService solarTermService;

    /**
     * 获取节气列表
     *
     * @return 节气列表
     */
    @GetMapping("/getSolarTermList")
    public ExceptionResult<List<SolarTermListVO>> getSolarTermList() {
        log.info("获取节气列表");
        List<SolarTermListVO> result = solarTermService.getSolarTermList();
        log.info("获取成功，共{}个节气", result.size());
        return ExceptionResult.success(result);
    }

    /**
     * 获取节气详情
     *
     * @param id 节气ID
     * @return 节气详情
     */
    @GetMapping("/getSolarTermDetail")
    public ExceptionResult<SolarTermDetailVO> getSolarTermDetail(
            @RequestParam("id") @NonNull String id) {
        log.info("获取节气详情，id: {}", id);
        SolarTermDetailVO result = solarTermService.getSolarTermDetail(id);
        log.info("获取成功，节气：{}", result.getTermName());
        return ExceptionResult.success(result);
    }

    /**
     * 获取当前节气
     *
     * @return 当前节气详情
     */
    @GetMapping("/getCurrentSolarTerm")
    public ExceptionResult<SolarTermDetailVO> getCurrentSolarTerm() {
        log.info("获取当前节气");
        SolarTermDetailVO result = solarTermService.getCurrentSolarTerm();
        log.info("获取成功，当前节气：{}", result.getTermName());
        return ExceptionResult.success(result);
    }
}
