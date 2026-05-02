package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.PointsExchangeRecord;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * 积分兑换记录Mapper接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface PointsExchangeRecordMapper extends BaseMapper<PointsExchangeRecord> {

    /**
     * 统计今日兑换次数
     *
     * @param accountId 账号ID
     * @return 今日兑换次数
     */
    default long countTodayByAccountId(String accountId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        QueryWrapper wrapper = QueryWrapper.create()
                .where(PointsExchangeRecord::getAccountId).eq(accountId)
                .and(PointsExchangeRecord::getCreateTime).ge(startOfDay)
                .and(PointsExchangeRecord::getCreateTime).lt(endOfDay);
        return selectCountByQuery(wrapper);
    }

    /**
     * 统计今日某商品的兑换次数
     *
     * @param accountId 账号ID
     * @param goodsId 商品ID
     * @return 今日该商品兑换次数
     */
    default long countTodayByAccountIdAndGoodsId(String accountId, String goodsId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        QueryWrapper wrapper = QueryWrapper.create()
                .where(PointsExchangeRecord::getAccountId).eq(accountId)
                .and(PointsExchangeRecord::getGoodsId).eq(goodsId)
                .and(PointsExchangeRecord::getCreateTime).ge(startOfDay)
                .and(PointsExchangeRecord::getCreateTime).lt(endOfDay);
        return selectCountByQuery(wrapper);
    }

    /**
     * 统计某商品的总兑换次数
     *
     * @param accountId 账号ID
     * @param goodsId 商品ID
     * @return 该商品总兑换次数
     */
    default long countByAccountIdAndGoodsId(String accountId, String goodsId) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(PointsExchangeRecord::getAccountId).eq(accountId)
                .and(PointsExchangeRecord::getGoodsId).eq(goodsId);
        return selectCountByQuery(wrapper);
    }

    /**
     * 分页查询积分兑换记录
     *
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param accountId 账号ID（精确查询，可为null）
     * @param status 状态（精确查询，可为null）
     * @return 分页结果
     */
    default Page<PointsExchangeRecord> selectExchangeRecordPage(Integer pageNo, Integer pageSize, String accountId, Integer status) {
        QueryWrapper wrapper = QueryWrapper.create()
                .orderBy(PointsExchangeRecord::getCreateTime, false);
        if (accountId != null && !accountId.isEmpty()) {
            wrapper.and(PointsExchangeRecord::getAccountId).eq(accountId);
        }
        if (status != null) {
            wrapper.and(PointsExchangeRecord::getStatus).eq(status);
        }
        return paginate(pageNo, pageSize, wrapper);
    }
}
