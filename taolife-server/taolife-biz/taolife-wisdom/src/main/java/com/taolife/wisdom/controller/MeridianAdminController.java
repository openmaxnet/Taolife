package com.taolife.wisdom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.param.MeridianQueryParam;
import com.taolife.wisdom.param.MeridianSaveParam;
import com.taolife.wisdom.service.IMeridianService;
import com.taolife.wisdom.vo.MeridianDetailVO;
import com.taolife.wisdom.vo.MeridianListVO;
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
 * 经络管理控制器（管理后台）
 * 负责处理经络管理相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/wisdom/admin/meridian")
@RequiredArgsConstructor
public class MeridianAdminController {

    private final IMeridianService meridianService;

    /**
     * 分页获取经络列表
     * 根据条件分页获取经络信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getMeridianPage")
    public ExceptionResult<PageResult<MeridianListVO>> getMeridianPage(MeridianQueryParam param) {
        log.info("分页获取经络列表，参数：{}", param);
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
        log.info("获取经络详情，id：{}", id);
        return ExceptionResult.success(meridianService.getMeridianDetail(id));
    }

    /**
     * 创建经络
     * 新增一条经络记录
     *
     * @param param 经络创建参数
     * @return 操作结果
     */
    @PostMapping("/createMeridian")
    public ExceptionResult<Void> createMeridian(@Valid @RequestBody MeridianSaveParam param) {
        log.info("创建经络，参数：{}", param);
        meridianService.createMeridian(param);
        return ExceptionResult.success();
    }

    /**
     * 修改经络信息
     * 根据经络ID修改经络信息
     *
     * @param id    经络ID
     * @param param 经络修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyMeridianInfo")
    public ExceptionResult<Void> modifyMeridianInfo(@RequestParam("id") String id,
                                                     @Valid @RequestBody MeridianSaveParam param) {
        log.info("修改经络信息，id：{}，参数：{}", id, param);
        meridianService.modifyMeridianInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除经络
     * 根据经络ID删除经络（逻辑删除）
     *
     * @param id 经络ID
     * @return 操作结果
     */
    @PostMapping("/removeMeridian")
    public ExceptionResult<Void> removeMeridian(@RequestParam("id") String id) {
        log.info("删除经络，id：{}", id);
        meridianService.removeMeridian(id);
        return ExceptionResult.success();
    }

    /**
     * 修改经络状态
     * 启用或禁用经络
     *
     * @param id         经络ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     * @return 操作结果
     */
    @PostMapping("/modifyMeridianStatus")
    public ExceptionResult<Void> modifyMeridianStatus(@RequestParam("id") String id,
                                                       @RequestParam("isDisabled") Integer isDisabled) {
        log.info("修改经络状态，id：{}，isDisabled：{}", id, isDisabled);
        meridianService.modifyMeridianStatus(id, isDisabled);
        return ExceptionResult.success();
    }
}
