package com.taolife.wisdom.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.wisdom.param.AcupointQueryParam;
import com.taolife.wisdom.service.IAcupointService;
import com.taolife.wisdom.vo.AcupointDetailVO;
import com.taolife.wisdom.vo.AcupointListVO;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 穴位控制器
 * 处理穴位相关的HTTP请求
 *
 * @author 文二
 * @date 2026-03-24
 */
@Slf4j
@RestController
@RequestMapping("/api/wisdom/acupoint")
@RequiredArgsConstructor
public class AcupointController {

    private final IAcupointService acupointService;

    /**
     * 获取穴位列表
     * 根据条件分页获取穴位信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getAcupointPage")
    public ExceptionResult<PageResult<AcupointListVO>> getAcupointPage(AcupointQueryParam param) {
        log.info("获取穴位列表，param: {}", param);
        return ExceptionResult.success(acupointService.getAcupointPage(param));
    }

    /**
     * 获取穴位详情
     * 根据穴位ID获取穴位的详细信息
     *
     * @param id 穴位ID
     * @return 穴位详细信息
     */
    @GetMapping("/getAcupointDetail")
    public ExceptionResult<AcupointDetailVO> getAcupointDetail(@RequestParam("id") String id) {
        log.info("获取穴位详情，id: {}", id);
        return ExceptionResult.success(acupointService.getAcupointDetail(id));
    }
}
