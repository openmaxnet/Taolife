package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.cipher.Signer;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.properties.WxPayProperties;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.MemberPlan;
import com.taolife.fee.entity.PayOrder;
import com.taolife.fee.enums.OrderStatusEnum;
import com.taolife.fee.enums.GrowthSourceEnum;
import com.taolife.fee.mapper.MemberPlanMapper;
import com.taolife.fee.mapper.PayOrderMapper;
import com.taolife.fee.param.CreateOrderParam;
import com.taolife.fee.service.IMemberGrowthService;
import com.taolife.fee.service.IPaymentService;
import com.taolife.fee.vo.CreateOrderVO;
import com.taolife.fee.vo.PayOrderVO;
import com.taolife.identity.entity.Account;
import com.taolife.identity.mapper.AccountMapper;
import com.taolife.identity.service.IAccountService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 支付服务实现
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PayOrderMapper payOrderMapper;
    private final MemberPlanMapper memberPlanMapper;
    private final AccountMapper accountMapper;
    private final IAccountService accountService;
    private final IMemberGrowthService memberGrowthService;
    private final StringRedisTemplate redisTemplate;
    private final MeterRegistry meterRegistry;

    @Autowired(required = false)
    private JsapiService jsapiService;
    @Autowired(required = false)
    private WxPayProperties wxPayProperties;
    @Autowired(required = false)
    private RSAAutoCertificateConfig rsaCertConfig;
    private static final String ORDER_NO_PREFIX = "TFO";
    private static final int ORDER_EXPIRE_MINUTES = 30;

    /**
     * 创建订单（调用微信预下单）
     * 生成订单号、保存订单记录、调用微信 JSAPI 预下单并生成小程序支付签名参数
     *
     * @param accountId 用户账号ID
     * @param param     创建订单参数（含套餐代码、微信openId等）
     * @return 订单创建结果（含预支付参数）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateOrderVO createOrder(String accountId, CreateOrderParam param) {
        // 校验套餐
        MemberPlan plan = memberPlanMapper.selectByPlanCode(param.getPlanCode());
        if (plan == null || plan.getIsEnabled() != 1) {
            throw new BusinessException(ExceptionCode.MEMBER_PLAN_NOT_FOUND);
        }

        // 获取用户信息
        Account account = accountMapper.selectOneById(accountId);
        if (account == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND);
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 创建订单
        PayOrder order = new PayOrder();
        order.setOrderNo(orderNo);
        order.setAccountId(accountId);
        order.setMemberPlanId(plan.getId());
        order.setPlanCode(plan.getPlanCode());
        order.setMemberLevel(plan.getMemberLevel());
        order.setDurationDays(plan.getDurationDays());
        order.setOrderAmount(plan.getCurrentPrice());
        order.setPayAmount(plan.getCurrentPrice());
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        order.setWxOpenid(param.getWxOpenid());
        order.setExpireTime(LocalDateTime.now().plusMinutes(ORDER_EXPIRE_MINUTES));
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        payOrderMapper.insert(order);
        meterRegistry.counter("biz.order.create", "planCode", plan.getPlanCode()).increment();

        // 调用微信支付预下单
        try {
            if (jsapiService != null) {
                PrepayRequest request = new PrepayRequest();
                request.setAppid(wxPayProperties.getAppId());
                request.setMchid(wxPayProperties.getMchId());
                request.setDescription(plan.getPlanName());
                request.setOutTradeNo(orderNo);
                request.setTimeExpire(LocalDateTime.now().plusMinutes(ORDER_EXPIRE_MINUTES)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                request.setAttach(accountId);
                Amount amount = new Amount();
                amount.setTotal(plan.getCurrentPrice());
                amount.setCurrency("CNY");
                request.setAmount(amount);
                Payer payer = new Payer();
                payer.setOpenid(param.getWxOpenid());
                request.setPayer(payer);

                PrepayResponse response = jsapiService.prepay(request);
                order.setWxPrepayId(response.getPrepayId());
                payOrderMapper.update(order);

                // 生成小程序支付参数签名
                String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
                String nonceStr = UUID.randomUUID().toString().replace("-", "");
                String packageStr = "prepay_id=" + response.getPrepayId();

                Signer signer = rsaCertConfig.createSigner();
                String message = wxPayProperties.getAppId() + "\n"
                        + timeStamp + "\n"
                        + nonceStr + "\n"
                        + packageStr + "\n";
                String paySign = signer.sign(message).getSign();

                CreateOrderVO vo = new CreateOrderVO();
                vo.setOrderNo(orderNo);
                vo.setPrepayId(response.getPrepayId());
                vo.setTimeStamp(timeStamp);
                vo.setNonceStr(nonceStr);
                vo.setPackageStr(packageStr);
                vo.setSignType("RSA");
                vo.setPaySign(paySign);
                return vo;
            }
        } catch (Exception e) {
            log.error("创建微信支付订单失败", e);
            throw new BusinessException(ExceptionCode.PAY_CREATE_FAILED, e.getMessage());
        }

        // 支付服务未启用时返回订单信息
        CreateOrderVO vo = new CreateOrderVO();
        vo.setOrderNo(orderNo);
        return vo;
    }

    /**
     * 处理微信支付回调
     * 更新订单状态、更新用户会员信息（含续费延长逻辑）、发放开通会员成长值
     *
     * @param params 微信回调参数（含订单号、交易号、交易状态等）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleWxPayCallback(Map<String, String> params) {
        String orderNo = params.get("out_trade_no");
        String transactionId = params.get("transaction_id");
        String tradeState = params.get("trade_state");

        PayOrder order = payOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.warn("微信支付回调订单不存在: {}", orderNo);
            throw new BusinessException(ExceptionCode.ORDER_NOT_FOUND);
        }

        // 防止重复处理（微信可能多次回调）
        if (order.getStatus() == OrderStatusEnum.PAID.getCode()) {
            log.info("订单已处理，忽略重复回调: {}", orderNo);
            return;
        }

        if ("SUCCESS".equals(tradeState)) {
            order.setStatus(OrderStatusEnum.PAID.getCode());
            order.setWxTransactionId(transactionId);
            order.setPaidTime(LocalDateTime.now());
            payOrderMapper.update(order);

            // 更新用户会员信息（含续费逻辑：在原到期时间上延长）
            accountService.updateMemberInfo(
                    order.getAccountId(),
                    order.getMemberLevel(),
                    calculateExpireTime(order.getAccountId(), order.getMemberLevel(), order.getDurationDays())
            );

            // 清除会员状态缓存
            redisTemplate.delete("taolife:member:status:" + order.getAccountId());

            // 开通/续费会员一次性成长值（从套餐配置读取）
            MemberPlan plan = memberPlanMapper.selectByPlanCode(order.getPlanCode());
            if (plan != null && plan.getSubGrowthBonus() != null && plan.getSubGrowthBonus() > 0) {
                memberGrowthService.addGrowth(order.getAccountId(), plan.getSubGrowthBonus(),
                        GrowthSourceEnum.SUBSCRIPTION.getValue(), "member_purchase",
                        order.getId(), "开通会员奖励成长值");
            }

            log.info("订单支付成功: {}, 会员等级: {}", orderNo, order.getMemberLevel());
        } else {
            order.setStatus(OrderStatusEnum.CLOSED.getCode());
            payOrderMapper.update(order);
            log.info("订单支付失败: {}, 状态: {}", orderNo, tradeState);
        }
    }

    /**
     * 按订单编号查询订单状态
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    @Override
    public PayOrderVO getOrderStatus(String orderNo) {
        PayOrder order = payOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(ExceptionCode.ORDER_NOT_FOUND);
        }
        return convertToVO(order);
    }

    /**
     * 分页查询当前用户的订单列表
     *
     * @param accountId 用户账号ID
     * @param pageNo    页码
     * @param pageSize  每页条数
     * @return 订单分页结果
     */
    @Override
    public PageResult<PayOrderVO> getOrderPage(String accountId, Integer pageNo, Integer pageSize) {
        Page<PayOrder> page = payOrderMapper.selectPayOrderPage(pageNo, pageSize, accountId, null);
        List<PayOrderVO> list = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, pageNo, pageSize, page.getTotalRow());
    }

    /**
     * 按 ID 查询订单
     *
     * @param id 订单ID
     * @return 订单信息
     */
    @Override
    public PayOrderVO getOrderById(String id) {
        PayOrder order = payOrderMapper.selectOneById(id);
        if (order == null) {
            throw new BusinessException(ExceptionCode.ORDER_NOT_FOUND);
        }
        return convertToVO(order);
    }

    /**
     * 取消订单（仅待支付状态的订单可取消）
     *
     * @param orderNo 订单编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        PayOrder order = payOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(ExceptionCode.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != OrderStatusEnum.PENDING.getCode()) {
            throw new BusinessException(ExceptionCode.ORDER_STATUS_INVALID, "订单状态不允许取消");
        }
        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
        order.setUpdateTime(LocalDateTime.now());
        payOrderMapper.update(order);
    }

    private String generateOrderNo() {
        return ORDER_NO_PREFIX + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private LocalDateTime calculateExpireTime(String accountId, Integer memberLevel, Integer durationDays) {
        Account account = accountMapper.selectOneById(accountId);
        LocalDateTime now = LocalDateTime.now();

        // 续费：在当前到期时间基础上延长
        if (account.getMemberExpireTime() != null && account.getMemberExpireTime().isAfter(now)) {
            return account.getMemberExpireTime().plusDays(durationDays);
        // 新购买：从当前时间开始计算
        } else {
            return now.plusDays(durationDays);
        }
    }

    private PayOrderVO convertToVO(PayOrder order) {
        PayOrderVO vo = new PayOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setAccountId(order.getAccountId());
        vo.setMemberPlanId(order.getMemberPlanId());
        vo.setPlanCode(order.getPlanCode());
        vo.setMemberLevel(order.getMemberLevel());
        vo.setDurationDays(order.getDurationDays());
        vo.setOrderAmount(order.getOrderAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus());
        vo.setWxPrepayId(order.getWxPrepayId());
        vo.setWxTransactionId(order.getWxTransactionId());
        vo.setPaidTime(order.getPaidTime());
        vo.setExpireTime(order.getExpireTime());
        vo.setCreateTime(order.getCreateTime());

        OrderStatusEnum statusEnum = OrderStatusEnum.getByCode(order.getStatus());
        vo.setStatusDesc(statusEnum != null ? statusEnum.getName() : "未知");

        // 查询套餐名称
        if (order.getMemberPlanId() != null) {
            MemberPlan plan = memberPlanMapper.selectOneById(order.getMemberPlanId());
            if (plan != null) {
                vo.setPlanName(plan.getPlanName());
            }
        }

        return vo;
    }
}
