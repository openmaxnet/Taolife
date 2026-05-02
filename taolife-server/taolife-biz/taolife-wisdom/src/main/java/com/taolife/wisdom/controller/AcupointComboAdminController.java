package com.taolife.wisdom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.entity.AcupointCombo;
import com.taolife.wisdom.param.AcupointComboSaveParam;
import com.taolife.wisdom.service.IAcupointComboService;
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
 * 穴位配伍管理控制器（管理后台）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/wisdom/admin/acupointCombine")
@RequiredArgsConstructor
public class AcupointComboAdminController {

    private final IAcupointComboService acupointCombineService;

    /**
     * 分页查询穴位配伍列表
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param category 分类（可选）
     * @param keyword  关键词（可选）
     * @return 分页结果
     */
    @GetMapping("/getAcupointCombinePage")
    public ExceptionResult<PageResult<AcupointCombo>> getAcupointCombinePage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "category", required = false) Integer category,
            @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("分页获取穴位配伍列表，pageNo：{}，pageSize：{}，category：{}，keyword：{}", pageNo, pageSize, category, keyword);
        return ExceptionResult.success(acupointCombineService.getAcupointCombinePage(pageNo, pageSize, category, keyword));
    }

    /**
     * 获取穴位配伍详情
     *
     * @param id 穴位配伍ID
     * @return 穴位配伍详情
     */
    @GetMapping("/getAcupointCombineDetail")
    public ExceptionResult<AcupointCombo> getAcupointCombineDetail(@RequestParam("id") String id) {
        log.info("获取穴位配伍详情，id：{}", id);
        return ExceptionResult.success(acupointCombineService.getAcupointCombineDetail(id));
    }

    /**
     * 创建穴位配伍
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createAcupointCombine")
    public ExceptionResult<Void> createAcupointCombine(@Valid @RequestBody AcupointComboSaveParam param) {
        log.info("创建穴位配伍，参数：{}", param);
        acupointCombineService.createAcupointCombine(param);
        return ExceptionResult.success();
    }

    /**
     * 修改穴位配伍信息
     *
     * @param id    穴位配伍ID
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyAcupointCombineInfo")
    public ExceptionResult<Void> modifyAcupointCombineInfo(@RequestParam("id") String id,
                                                            @Valid @RequestBody AcupointComboSaveParam param) {
        log.info("修改穴位配伍信息，id：{}，参数：{}", id, param);
        acupointCombineService.modifyAcupointCombineInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除穴位配伍（逻辑删除）
     *
     * @param id 穴位配伍ID
     * @return 操作结果
     */
    @PostMapping("/removeAcupointCombine")
    public ExceptionResult<Void> removeAcupointCombine(@RequestParam("id") String id) {
        log.info("删除穴位配伍，id：{}", id);
        acupointCombineService.removeAcupointCombine(id);
        return ExceptionResult.success();
    }

    /**
     * 修改穴位配伍状态
     *
     * @param id         穴位配伍ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     * @return 操作结果
     */
    @PostMapping("/modifyAcupointCombineStatus")
    public ExceptionResult<Void> modifyAcupointCombineStatus(@RequestParam("id") String id,
                                                              @RequestParam("isDisabled") Integer isDisabled) {
        log.info("修改穴位配伍状态，id：{}，isDisabled：{}", id, isDisabled);
        acupointCombineService.modifyAcupointCombineStatus(id, isDisabled);
        return ExceptionResult.success();
    }
}
