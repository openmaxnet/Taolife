package com.taolife.wisdom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.param.AcupointQueryParam;
import com.taolife.wisdom.param.AcupointSaveParam;
import com.taolife.wisdom.service.IAcupointService;
import com.taolife.wisdom.vo.AcupointDetailVO;
import com.taolife.wisdom.vo.AcupointListVO;
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
 * 穴位管理控制器（管理后台）
 * 负责处理穴位管理相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/wisdom/admin/acupoint")
@RequiredArgsConstructor
public class AcupointAdminController {

    private final IAcupointService acupointService;

    /**
     * 分页获取穴位列表
     * 根据条件分页获取穴位信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getAcupointPage")
    public ExceptionResult<PageResult<AcupointListVO>> getAcupointPage(AcupointQueryParam param) {
        log.info("分页获取穴位列表，参数：{}", param);
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
        log.info("获取穴位详情，id：{}", id);
        return ExceptionResult.success(acupointService.getAcupointDetail(id));
    }

    /**
     * 创建穴位
     * 新增一条穴位记录
     *
     * @param param 穴位创建参数
     * @return 操作结果
     */
    @PostMapping("/createAcupoint")
    public ExceptionResult<Void> createAcupoint(@Valid @RequestBody AcupointSaveParam param) {
        log.info("创建穴位，参数：{}", param);
        acupointService.createAcupoint(param);
        return ExceptionResult.success();
    }

    /**
     * 修改穴位信息
     * 根据穴位ID修改穴位信息
     *
     * @param id    穴位ID
     * @param param 穴位修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyAcupointInfo")
    public ExceptionResult<Void> modifyAcupointInfo(@RequestParam("id") String id,
                                                     @Valid @RequestBody AcupointSaveParam param) {
        log.info("修改穴位信息，id：{}，参数：{}", id, param);
        acupointService.modifyAcupointInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除穴位
     * 根据穴位ID删除穴位（逻辑删除）
     *
     * @param id 穴位ID
     * @return 操作结果
     */
    @PostMapping("/removeAcupoint")
    public ExceptionResult<Void> removeAcupoint(@RequestParam("id") String id) {
        log.info("删除穴位，id：{}", id);
        acupointService.removeAcupoint(id);
        return ExceptionResult.success();
    }

    /**
     * 修改穴位状态
     * 启用或禁用穴位
     *
     * @param id         穴位ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     * @return 操作结果
     */
    @PostMapping("/modifyAcupointStatus")
    public ExceptionResult<Void> modifyAcupointStatus(@RequestParam("id") String id,
                                                       @RequestParam("isDisabled") Integer isDisabled) {
        log.info("修改穴位状态，id：{}，isDisabled：{}", id, isDisabled);
        acupointService.modifyAcupointStatus(id, isDisabled);
        return ExceptionResult.success();
    }
}
