package com.taolife.fee.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.fee.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 支付回调控制器
 * 处理第三方支付平台的异步通知
 *
 * @author 文二
 * @date 2026-04-19
 */
@Slf4j
@RestController
@RequestMapping("/api/fee/pay")
@RequiredArgsConstructor

public class PayNotifyController {

    private final IPaymentService paymentService;

    /**
     * 处理微信支付异步通知
     *
     * @param params 微信回调参数
     * @return 处理结果
     */
    @PostMapping("/wxNotify")
    public ExceptionResult<String> wxNotify(@RequestBody Map<String, String> params) {
        log.info("微信支付回调，参数：{}", params);
        paymentService.handleWxPayCallback(params);
        return ExceptionResult.success("SUCCESS");
    }
}
