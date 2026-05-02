package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.AdDisplayLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告展示日志Mapper接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface AdDisplayLogMapper extends BaseMapper<AdDisplayLog> {

    /**
     * 分页查询广告日志
     *
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param accountId 账号ID（精确查询，可为null）
     * @param adType 广告类型（精确查询，可为null）
     * @param action 动作（精确查询，可为null）
     * @return 分页结果
     */
    default Page<AdDisplayLog> selectAdLogPage(Integer pageNo, Integer pageSize, String accountId, Integer adType, Integer action) {
        QueryWrapper wrapper = QueryWrapper.create()
                .orderBy(AdDisplayLog::getCreateTime, false);
        if (accountId != null && !accountId.isEmpty()) {
            wrapper.and(AdDisplayLog::getAccountId).eq(accountId);
        }
        if (adType != null) {
            wrapper.and(AdDisplayLog::getAdType).eq(adType);
        }
        if (action != null) {
            wrapper.and(AdDisplayLog::getAction).eq(action);
        }
        return paginate(pageNo, pageSize, wrapper);
    }
}
