package com.taolife.wisdom.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.wisdom.param.FoodQueryParam;
import com.taolife.wisdom.service.IFoodService;
import com.taolife.wisdom.vo.FoodDetailVO;
import com.taolife.wisdom.vo.FoodListVO;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 食材控制器
 * 处理食材相关的HTTP请求
 *
 * @author 文二
 * @date 2026-03-20
 */
@Slf4j
@RestController
@RequestMapping("/api/wisdom/food")
@RequiredArgsConstructor
public class FoodController {

    private final IFoodService foodService;

    /**
     * 获取食材列表
     * 根据条件分页获取食材信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getFoodPage")
    public ExceptionResult<PageResult<FoodListVO>> getFoodPage(FoodQueryParam param) {
        log.info("获取食材列表，param: {}", param);
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
        log.info("获取食材详情，id: {}", id);
        return ExceptionResult.success(foodService.getFoodDetail(id));
    }
}
