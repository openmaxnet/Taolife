package com.taolife.fee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.PointsExchangePageParam;
import com.taolife.fee.param.PointsGoodsPageParam;
import com.taolife.fee.param.PointsGoodsSaveParam;
import com.taolife.fee.service.IPointsExchangeAdminService;
import com.taolife.fee.service.IPointsGoodsAdminService;
import com.taolife.fee.vo.PointsExchangeAdminVO;
import com.taolife.fee.vo.PointsGoodsAdminVO;
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
 * 积分商品管理控制器
 * 负责处理管理端积分商品相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/fee/admin/goods")
@RequiredArgsConstructor
public class PointsGoodsAdminController {

    private final IPointsGoodsAdminService pointsGoodsAdminService;
    private final IPointsExchangeAdminService pointsExchangeAdminService;

    /**
     * 分页获取积分商品列表
     * 根据条件分页获取积分商品信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getGoodsPage")
    public ExceptionResult<PageResult<PointsGoodsAdminVO>> getGoodsPage(PointsGoodsPageParam param) {
        log.info("分页查询积分商品，参数：{}", param);
        return ExceptionResult.success(pointsGoodsAdminService.getGoodsPage(param));
    }

    /**
     * 获取积分商品详情
     * 根据ID获取积分商品详细信息
     *
     * @param id 积分商品ID
     * @return 积分商品详情
     */
    @GetMapping("/getGoods")
    public ExceptionResult<PointsGoodsAdminVO> getGoods(@RequestParam("id") String id) {
        log.info("获取积分商品详情，id：{}", id);
        return ExceptionResult.success(pointsGoodsAdminService.getGoods(id));
    }

    /**
     * 创建积分商品
     * 新增一条积分商品记录
     *
     * @param param 积分商品参数
     * @return 操作结果
     */
    @PostMapping("/createGoods")
    public ExceptionResult<Void> createGoods(@Valid @RequestBody PointsGoodsSaveParam param) {
        log.info("创建积分商品，参数：{}", param);
        pointsGoodsAdminService.createGoods(param);
        return ExceptionResult.success();
    }

    /**
     * 修改积分商品
     * 根据ID修改积分商品信息
     *
     * @param id 积分商品ID
     * @param param 积分商品参数
     * @return 操作结果
     */
    @PostMapping("/modifyGoods")
    public ExceptionResult<Void> modifyGoods(@RequestParam("id") String id,
                                            @Valid @RequestBody PointsGoodsSaveParam param) {
        log.info("修改积分商品，id：{}，参数：{}", id, param);
        pointsGoodsAdminService.modifyGoods(id, param);
        return ExceptionResult.success();
    }

    /**
     * 修改积分商品状态
     * 启用或禁用积分商品
     *
     * @param id 积分商品ID
     * @param isEnabled 是否启用（0-禁用，1-启用）
     * @return 操作结果
     */
    @PostMapping("/modifyGoodsStatus")
    public ExceptionResult<Void> modifyGoodsStatus(@RequestParam("id") String id,
                                                  @RequestParam("isEnabled") Integer isEnabled) {
        log.info("修改积分商品状态，id：{}，isEnabled：{}", id, isEnabled);
        pointsGoodsAdminService.modifyGoodsStatus(id, isEnabled);
        return ExceptionResult.success();
    }

    /**
     * 删除积分商品
     * 根据ID删除积分商品（逻辑删除）
     *
     * @param id 积分商品ID
     * @return 操作结果
     */
    @PostMapping("/removeGoods")
    public ExceptionResult<Void> removeGoods(@RequestParam("id") String id) {
        log.info("删除积分商品，id：{}", id);
        pointsGoodsAdminService.removeGoods(id);
        return ExceptionResult.success();
    }

    /**
     * 分页获取积分兑换记录列表
     * 根据条件分页获取积分兑换记录信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getExchangeRecordPage")
    public ExceptionResult<PageResult<PointsExchangeAdminVO>> getExchangeRecordPage(PointsExchangePageParam param) {
        log.info("分页查询积分兑换记录，参数：{}", param);
        return ExceptionResult.success(pointsExchangeAdminService.getExchangeRecordPage(param));
    }
}
