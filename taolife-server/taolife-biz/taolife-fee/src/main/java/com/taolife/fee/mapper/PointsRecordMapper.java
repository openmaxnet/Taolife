package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.PointsRecord;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户积分记录Mapper接口
 *
 * @author 文二
 * @date 2026-04-02
 */
@Mapper
public interface PointsRecordMapper extends BaseMapper<PointsRecord> {

    /**
     * 查询用户当前可用积分
     */
    default Integer selectAvailablePointsByAccountId(String accountId) {
        List<PointsRecord> records = selectListByQuery(
            QueryWrapper.create()
                .where(PointsRecord::getAccountId).eq(accountId)
                .and(PointsRecord::getIsDeleted).eq(0)
                .orderBy(PointsRecord::getCreateTime, false)
                .limit(1)
        );

        if (records.isEmpty()) {
            return 0;
        }

        return records.get(0).getBalanceAfter() != null ? records.get(0).getBalanceAfter() : 0;
    }

    /**
     * 查询用户历史获取的总积分（仅统计正向变动）
     */
    default Integer selectTotalPointsByAccountId(String accountId) {
        List<PointsRecord> records = selectListByQuery(
            QueryWrapper.create()
                .where(PointsRecord::getAccountId).eq(accountId)
                .and(PointsRecord::getPointsChange).gt(0)
                .and(PointsRecord::getIsDeleted).eq(0)
                .orderBy(PointsRecord::getCreateTime, false)
                .limit(1)
        );

        if (records.isEmpty()) {
            return 0;
        }

        return records.get(0).getBalanceAfter() != null ? records.get(0).getBalanceAfter() : 0;
    }

    /**
     * 管理后台分页查询积分记录
     */
    default Page<PointsRecord> selectAdminPage(Page<PointsRecord> page, String accountId) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.where(PointsRecord::getIsDeleted).eq(0);
        if (accountId != null && !accountId.isEmpty()) {
            wrapper.and(PointsRecord::getAccountId).eq(accountId);
        }
        wrapper.orderBy(PointsRecord::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
