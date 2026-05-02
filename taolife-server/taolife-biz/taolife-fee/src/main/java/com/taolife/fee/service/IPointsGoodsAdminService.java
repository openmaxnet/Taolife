package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.PointsGoodsPageParam;
import com.taolife.fee.param.PointsGoodsSaveParam;
import com.taolife.fee.vo.PointsGoodsAdminVO;

/**
 * 积分商品管理端服务
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IPointsGoodsAdminService {

    /**
     * 分页获取积分商品列表
     * 根据条件分页获取积分商品信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<PointsGoodsAdminVO> getGoodsPage(PointsGoodsPageParam param);

    /**
     * 获取积分商品详情
     * 根据ID获取积分商品详细信息
     *
     * @param id 积分商品ID
     * @return 积分商品详情
     */
    PointsGoodsAdminVO getGoods(String id);

    /**
     * 创建积分商品
     * 新增一条积分商品记录
     *
     * @param param 积分商品参数
     */
    void createGoods(PointsGoodsSaveParam param);

    /**
     * 修改积分商品
     * 根据ID修改积分商品信息
     *
     * @param id 积分商品ID
     * @param param 积分商品参数
     */
    void modifyGoods(String id, PointsGoodsSaveParam param);

    /**
     * 修改积分商品状态
     * 启用或禁用积分商品
     *
     * @param id 积分商品ID
     * @param isEnabled 是否启用（0-禁用，1-启用）
     */
    void modifyGoodsStatus(String id, Integer isEnabled);

    /**
     * 删除积分商品
     * 根据ID删除积分商品（逻辑删除）
     *
     * @param id 积分商品ID
     */
    void removeGoods(String id);
}
