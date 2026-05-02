package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.PayOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单Mapper接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {

    /**
     * 根据订单编号查询
     *
     * @param orderNo 订单编号
     * @return 支付订单
     */
    default PayOrder selectByOrderNo(String orderNo) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(PayOrder::getOrderNo).eq(orderNo)
                .limit(1);
        return selectOneByQuery(wrapper);
    }

    /**
     * 根据账号ID查询订单列表
     *
     * @param accountId 账号ID
     * @return 支付订单列表
     */
    default java.util.List<PayOrder> selectByAccountId(String accountId) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(PayOrder::getAccountId).eq(accountId)
                .orderBy(PayOrder::getCreateTime, false);
        return selectListByQuery(wrapper);
    }

    /**
     * 分页查询支付订单
     *
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param accountId 账号ID（精确查询，可为null）
     * @param status 订单状态（精确查询，可为null）
     * @return 分页结果
     */
    default Page<PayOrder> selectPayOrderPage(Integer pageNo, Integer pageSize, String accountId, Integer status) {
        QueryWrapper wrapper = QueryWrapper.create()
                .orderBy(PayOrder::getCreateTime, false);
        if (accountId != null && !accountId.isEmpty()) {
            wrapper.and(PayOrder::getAccountId).eq(accountId);
        }
        if (status != null) {
            wrapper.and(PayOrder::getStatus).eq(status);
        }
        return paginate(pageNo, pageSize, wrapper);
    }
}
