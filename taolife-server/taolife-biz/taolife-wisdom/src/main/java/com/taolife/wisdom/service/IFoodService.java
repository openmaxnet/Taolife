package com.taolife.wisdom.service;

import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.param.FoodQueryParam;
import com.taolife.wisdom.param.FoodSaveParam;
import com.taolife.wisdom.vo.FoodDetailVO;
import com.taolife.wisdom.vo.FoodListVO;

/**
 * 食材服务接口
 * 定义食材相关的业务操作
 *
 * @author 文二
 * @date 2026-03-20
 */
public interface IFoodService {

    /**
     * 获取食材列表
     * 根据条件分页获取食材信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<FoodListVO> getFoodPage(FoodQueryParam param);

    /**
     * 获取食材详情
     * 根据食材ID获取食材的详细信息
     *
     * @param id 食材ID
     * @return 食材详细信息
     */
    FoodDetailVO getFoodDetail(String id);

    /**
     * 创建食材
     *
     * @param param 食材创建参数
     */
    void createFood(FoodSaveParam param);

    /**
     * 修改食材信息
     *
     * @param id    食材ID
     * @param param 食材修改参数
     */
    void modifyFoodInfo(String id, FoodSaveParam param);

    /**
     * 删除食材（逻辑删除）
     *
     * @param id 食材ID
     */
    void removeFood(String id);

    /**
     * 修改食材状态
     *
     * @param id         食材ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    void modifyFoodStatus(String id, Integer isDisabled);
}
