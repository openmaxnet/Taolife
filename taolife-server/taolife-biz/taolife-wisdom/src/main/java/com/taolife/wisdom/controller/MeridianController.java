package com.taolife.wisdom.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.wisdom.param.MeridianQueryParam;
import com.taolife.wisdom.service.IMeridianService;
import com.taolife.wisdom.vo.MeridianDetailVO;
import com.taolife.wisdom.vo.MeridianListVO;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 经络控制器
 * 处理经络相关的HTTP请求
 *
 * @author 文二
 * @date 2026-03-24
 */
@Slf4j
@RestController
@RequestMapping("/api/wisdom/meridian")
@RequiredArgsConstructor
public class MeridianController {

    private final IMeridianService meridianService;

    /**
     * 获取经络列表
     * 根据条件分页获取经络信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getMeridianPage")
    public ExceptionResult<PageResult<MeridianListVO>> getMeridianPage(MeridianQueryParam param) {
        log.info("获取经络列表，param: {}", param);
        return ExceptionResult.success(meridianService.getMeridianPage(param));
    }

    /**
     * 获取经络详情
     * 根据经络ID获取经络的详细信息
     *
     * @param id 经络ID
     * @return 经络详细信息
     */
    @GetMapping("/getMeridianDetail")
    public ExceptionResult<MeridianDetailVO> getMeridianDetail(@RequestParam("id") String id) {
        log.info("获取经络详情，id: {}", id);
        return ExceptionResult.success(meridianService.getMeridianDetail(id));
    }
}
