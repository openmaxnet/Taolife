package com.taolife.wisdom.controller;

import com.taolife.wisdom.param.FitnessTypeDetailAdminParam;
import com.taolife.wisdom.param.FitnessTypePageAdminParam;
import com.taolife.wisdom.param.FitnessTypeSaveAdminParam;
import com.taolife.wisdom.param.ModifyFitnessTypeStatusAdminParam;
import com.taolife.wisdom.param.RemoveFitnessTypeAdminParam;
import com.taolife.wisdom.service.IFitnessAdminService;
import com.taolife.wisdom.vo.FitnessTypeAdminVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员体质类型管理控制器
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/wisdom/admin/constitution")
@RequiredArgsConstructor
public class FitnessAdminController {

    private final IFitnessAdminService adminConstitutionService;

    /**
     * 分页查询体质类型列表
     *
     * @param param 分页查询参数
     * @return 体质类型分页结果
     */
    @GetMapping("/getConstitutionTypePage")
    public ExceptionResult<PageResult<FitnessTypeAdminVO>> getConstitutionTypePage(FitnessTypePageAdminParam param) {
        return ExceptionResult.success(adminConstitutionService.getConstitutionTypePage(param));
    }

    /**
     * 获取体质类型详情
     *
     * @param param 详情查询参数（含ID）
     * @return 体质类型详情
     */
    @GetMapping("/getConstitutionTypeDetail")
    public ExceptionResult<FitnessTypeAdminVO> getConstitutionTypeDetail(FitnessTypeDetailAdminParam param) {
        return ExceptionResult.success(adminConstitutionService.getConstitutionTypeDetail(param));
    }

    /**
     * 创建体质类型
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createConstitutionType")
    public ExceptionResult<Void> createConstitutionType(FitnessTypeSaveAdminParam param) {
        adminConstitutionService.createConstitutionType(param);
        return ExceptionResult.success();
    }

    /**
     * 修改体质类型信息
     *
     * @param param 修改参数（含ID）
     * @return 操作结果
     */
    @PostMapping("/modifyConstitutionTypeInfo")
    public ExceptionResult<Void> modifyConstitutionTypeInfo(FitnessTypeSaveAdminParam param) {
        adminConstitutionService.modifyConstitutionTypeInfo(param);
        return ExceptionResult.success();
    }

    /**
     * 删除体质类型（逻辑删除）
     *
     * @param param 删除参数（含ID）
     * @return 操作结果
     */
    @PostMapping("/removeConstitutionType")
    public ExceptionResult<Void> removeConstitutionType(RemoveFitnessTypeAdminParam param) {
        adminConstitutionService.removeConstitutionType(param);
        return ExceptionResult.success();
    }

    /**
     * 修改体质类型状态
     *
     * @param param 状态修改参数（含ID和禁用标记）
     * @return 操作结果
     */
    @PostMapping("/modifyConstitutionTypeStatus")
    public ExceptionResult<Void> modifyConstitutionTypeStatus(ModifyFitnessTypeStatusAdminParam param) {
        adminConstitutionService.modifyConstitutionTypeStatus(param);
        return ExceptionResult.success();
    }
}
