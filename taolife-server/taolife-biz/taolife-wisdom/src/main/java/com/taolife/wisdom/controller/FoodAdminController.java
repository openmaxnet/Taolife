package com.taolife.wisdom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.param.FoodQueryParam;
import com.taolife.wisdom.param.FoodSaveParam;
import com.taolife.wisdom.service.IFoodService;
import com.taolife.wisdom.vo.FoodDetailVO;
import com.taolife.wisdom.vo.FoodListVO;
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
 * 食材管理控制器（管理后台）
 * 负责处理管理后台食材相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/wisdom/admin/food")
@RequiredArgsConstructor
public class FoodAdminController {

    private final IFoodService foodService;

    /**
     * 分页获取食材列表
     * 根据条件分页获取食材信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getFoodPage")
    public ExceptionResult<PageResult<FoodListVO>> getFoodPage(FoodQueryParam param) {
        log.info("分页获取食材列表，参数：{}", param);
        return ExceptionResult.success(foodService.getFoodPage(param));
    }

    /**
     * 获取食材详情
     * 根据食材ID获取食材的详细信息
     *
     * @param id 食材ID
     * @return 食材详细信息
     */
    @GetMapping("/getFoodDetail")
    public ExceptionResult<FoodDetailVO> getFoodDetail(@RequestParam("id") String id) {
        log.info("获取食材详情，id：{}", id);
        return ExceptionResult.success(foodService.getFoodDetail(id));
    }

    /**
     * 创建食材
     * 新增一条食材记录
     *
     * @param param 食材创建参数
     * @return 操作结果
     */
    @PostMapping("/createFood")
    public ExceptionResult<Void> createFood(@Valid @RequestBody FoodSaveParam param) {
        log.info("创建食材，参数：{}", param);
        foodService.createFood(param);
        return ExceptionResult.success();
    }

    /**
     * 修改食材信息
     * 根据食材ID修改食材信息
     *
     * @param id    食材ID
     * @param param 食材修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyFoodInfo")
    public ExceptionResult<Void> modifyFoodInfo(@RequestParam("id") String id,
                                                 @Valid @RequestBody FoodSaveParam param) {
        log.info("修改食材信息，id：{}，参数：{}", id, param);
        foodService.modifyFoodInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除食材
     * 根据食材ID删除食材（逻辑删除）
     *
     * @param id 食材ID
     * @return 操作结果
     */
    @PostMapping("/removeFood")
    public ExceptionResult<Void> removeFood(@RequestParam("id") String id) {
        log.info("删除食材，id：{}", id);
        foodService.removeFood(id);
        return ExceptionResult.success();
    }

    /**
     * 修改食材状态
     * 启用或禁用食材
     *
     * @param id         食材ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     * @return 操作结果
     */
    @PostMapping("/modifyFoodStatus")
    public ExceptionResult<Void> modifyFoodStatus(@RequestParam("id") String id,
                                                   @RequestParam("isDisabled") Integer isDisabled) {
        log.info("修改食材状态，id：{}，isDisabled：{}", id, isDisabled);
        foodService.modifyFoodStatus(id, isDisabled);
        return ExceptionResult.success();
    }
}
