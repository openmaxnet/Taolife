package com.taolife.fee.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.fee.param.ExchangeParam;
import com.taolife.fee.service.IPointsExchangeService;
import com.taolife.fee.vo.PointsExchangeVO;
import com.taolife.fee.vo.PointsGoodsVO;
import com.taolife.fee.vo.PointsSummaryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分兑换控制器
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@RestController
@RequestMapping("/api/fee/points")
@RequiredArgsConstructor
public class PointsExchangeController {

    private final IPointsExchangeService pointsExchangeService;

    /**
     * 获取积分商品列表
     *
     * @return 积分商品列表
     */
    @GetMapping("/getGoodsList")
    public ExceptionResult<List<PointsGoodsVO>> getGoodsList() {
        List<PointsGoodsVO> result = pointsExchangeService.getGoodsList();
        return ExceptionResult.success(result);
    }

    /**
     * 积分兑换商品
     *
     * @param param 兑换参数（含商品编码）
     * @return 兑换结果
     */
    @PostMapping("/exchange")
    public ExceptionResult<PointsExchangeVO> exchange(@Valid @RequestBody ExchangeParam param) {
        String accountId = UserContext.getAccountId();
        PointsExchangeVO result = pointsExchangeService.exchange(accountId, param.getGoodsCode());
        return ExceptionResult.success(result);
    }

    /**
     * 查询当前用户的兑换记录
     *
     * @return 兑换记录列表
     */
    @GetMapping("/getExchangeRecords")
    public ExceptionResult<List<PointsExchangeVO>> getExchangeRecords() {
        String accountId = UserContext.getAccountId();
        List<PointsExchangeVO> result = pointsExchangeService.getExchangeRecords(accountId);
        return ExceptionResult.success(result);
    }

    /**
     * 查询当前用户的积分汇总信息
     *
     * @return 积分汇总信息
     */
    @GetMapping("/getPointsSummary")
    public ExceptionResult<PointsSummaryVO> getPointsSummary() {
        String accountId = UserContext.getAccountId();
        PointsSummaryVO result = pointsExchangeService.getPointsSummary(accountId);
        return ExceptionResult.success(result);
    }
}
