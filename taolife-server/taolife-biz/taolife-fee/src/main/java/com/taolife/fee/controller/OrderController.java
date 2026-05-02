package com.taolife.fee.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.CreateOrderParam;
import com.taolife.fee.service.IPaymentService;
import com.taolife.fee.vo.CreateOrderVO;
import com.taolife.fee.vo.PayOrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器（用户端）
 *
 * @author 文二
 * @date 2026-04-19
 */
@Slf4j
@RestController
@RequestMapping("/api/fee/order")
@RequiredArgsConstructor
public class OrderController {

    private final IPaymentService paymentService;

    /**
     * 创建订单（调用微信预下单）
     *
     * @param param 创建订单参数（含套餐代码、微信openId等）
     * @return 订单创建结果（含预支付参数）
     */
    @PostMapping("/createOrder")
    public ExceptionResult<CreateOrderVO> createOrder(@Valid @RequestBody CreateOrderParam param) {
        String accountId = UserContext.getAccountId();
        log.info("创建订单，accountId：{}，套餐代码：{}", accountId, param.getPlanCode());
        return ExceptionResult.success(paymentService.createOrder(accountId, param));
    }

    /**
     * 按订单编号查询订单状态
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    @GetMapping("/getOrderStatus")
    public ExceptionResult<PayOrderVO> getOrderStatus(@RequestParam("orderNo") String orderNo) {
        log.info("查询订单状态，orderNo：{}", orderNo);
        return ExceptionResult.success(paymentService.getOrderStatus(orderNo));
    }

    /**
     * 按 ID 查询订单
     *
     * @param id 订单ID
     * @return 订单信息
     */
    @GetMapping("/getOrderById")
    public ExceptionResult<PayOrderVO> getOrderById(@RequestParam String id) {
        return ExceptionResult.success(paymentService.getOrderById(id));
    }

    /**
     * 分页查询当前用户的订单列表
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 订单分页结果
     */
    @GetMapping("/getOrderPage")
    public ExceptionResult<PageResult<PayOrderVO>> getOrderPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accountId = UserContext.getAccountId();
        log.info("分页查询订单，accountId：{}，pageNo：{}，pageSize：{}", accountId, pageNo, pageSize);
        return ExceptionResult.success(paymentService.getOrderPage(accountId, pageNo, pageSize));
    }

    /**
     * 取消订单（仅待支付状态的订单可取消）
     *
     * @param orderNo 订单编号
     * @return 操作结果
     */
    @PostMapping("/cancelOrder")
    public ExceptionResult<Void> cancelOrder(@RequestParam String orderNo) {
        paymentService.cancelOrder(orderNo);
        return ExceptionResult.success();
    }
}
