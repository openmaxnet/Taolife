package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.CreateOrderParam;
import com.taolife.fee.vo.CreateOrderVO;
import com.taolife.fee.vo.PayOrderVO;

import java.util.Map;

/**
 * 支付服务接口
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IPaymentService {

    /**
     * 创建支付订单
     *
     * @param accountId 账号ID
     * @param param 创建订单参数
     * @return 订单信息和支付参数
     */
    CreateOrderVO createOrder(String accountId, CreateOrderParam param);

    /**
     * 处理微信支付回调
     *
     * @param params 回调参数
     * @return 处理结果
     */
    void handleWxPayCallback(Map<String, String> params);

    /**
     * 查询订单状态
     *
     * @param orderNo 订单编号
     * @return 订单详情
     */
    PayOrderVO getOrderStatus(String orderNo);

    /**
     * 分页获取订单列表
     *
     * @param accountId 账号ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<PayOrderVO> getOrderPage(String accountId, Integer pageNo, Integer pageSize);

    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单详情
     */
    PayOrderVO getOrderById(String id);

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     */
    void cancelOrder(String orderNo);
}
