package com.taolife.wisdom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.entity.Exercise;
import com.taolife.wisdom.param.ExerciseSaveParam;
import com.taolife.wisdom.service.IExerciseService;
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
 * 运动项目管理控制器（管理后台）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/wisdom/admin/exercise")
@RequiredArgsConstructor
public class ExerciseAdminController {

    private final IExerciseService exerciseService;

    /**
     * 分页查询运动项目列表
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param category 分类（可选）
     * @param keyword  关键词（可选）
     * @return 分页结果
     */
    @GetMapping("/getExercisePage")
    public ExceptionResult<PageResult<Exercise>> getExercisePage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "category", required = false) Integer category,
            @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("分页获取运动项目列表，pageNo：{}，pageSize：{}，category：{}，keyword：{}", pageNo, pageSize, category, keyword);
        return ExceptionResult.success(exerciseService.getExercisePage(pageNo, pageSize, category, keyword));
    }

    /**
     * 获取运动项目详情
     *
     * @param id 运动项目ID
     * @return 运动项目详情
     */
    @GetMapping("/getExerciseDetail")
    public ExceptionResult<Exercise> getExerciseDetail(@RequestParam("id") String id) {
        log.info("获取运动项目详情，id：{}", id);
        return ExceptionResult.success(exerciseService.getExerciseDetail(id));
    }

    /**
     * 创建运动项目
     *
     * @param param 创建参数
     * @return 操作结果
     */
    @PostMapping("/createExercise")
    public ExceptionResult<Void> createExercise(@Valid @RequestBody ExerciseSaveParam param) {
        log.info("创建运动项目，参数：{}", param);
        exerciseService.createExercise(param);
        return ExceptionResult.success();
    }

    /**
     * 修改运动项目信息
     *
     * @param id    运动项目ID
     * @param param 修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyExerciseInfo")
    public ExceptionResult<Void> modifyExerciseInfo(@RequestParam("id") String id,
                                                     @Valid @RequestBody ExerciseSaveParam param) {
        log.info("修改运动项目信息，id：{}，参数：{}", id, param);
        exerciseService.modifyExerciseInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除运动项目（逻辑删除）
     *
     * @param id 运动项目ID
     * @return 操作结果
     */
    @PostMapping("/removeExercise")
    public ExceptionResult<Void> removeExercise(@RequestParam("id") String id) {
        log.info("删除运动项目，id：{}", id);
        exerciseService.removeExercise(id);
        return ExceptionResult.success();
    }

    /**
     * 修改运动项目状态
     *
     * @param id         运动项目ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     * @return 操作结果
     */
    @PostMapping("/modifyExerciseStatus")
    public ExceptionResult<Void> modifyExerciseStatus(@RequestParam("id") String id,
                                                       @RequestParam("isDisabled") Integer isDisabled) {
        log.info("修改运动项目状态，id：{}，isDisabled：{}", id, isDisabled);
        exerciseService.modifyExerciseStatus(id, isDisabled);
        return ExceptionResult.success();
    }
}
