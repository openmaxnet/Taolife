package com.taolife.fee.service;

import com.taolife.fee.vo.PointsExchangeVO;
import com.taolife.fee.vo.PointsGoodsVO;
import com.taolife.fee.vo.PointsSummaryVO;

import java.util.List;

/**
 * 积分兑换服务接口
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IPointsExchangeService {

    /** 获取积分商品列表 */
    List<PointsGoodsVO> getGoodsList();

    /** 兑换积分商品 */
    PointsExchangeVO exchange(String accountId, String goodsCode);

    /** 获取兑换记录 */
    List<PointsExchangeVO> getExchangeRecords(String accountId);

    /** 获取积分总览 */
    PointsSummaryVO getPointsSummary(String accountId);
}
