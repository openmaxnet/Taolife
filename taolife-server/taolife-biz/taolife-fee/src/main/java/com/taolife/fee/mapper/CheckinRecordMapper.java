package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.CheckinRecord;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 打卡记录Mapper接口
 *
 * @author 文二
 * @date 2026-04-02
 */
@Mapper
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {

    /**
     * 通用查询方法
     * 根据可选条件查询打卡记录
     *
     * @param accountId   账号ID（必填）
     * @param startDate   开始日期（可选）
     * @param endDate     结束日期（可选）
     * @param checkinDate 打卡日期（可选，用于精确日期查询）
     * @param asc         是否升序（默认降序）
     * @return 打卡记录列表
     */
    default List<CheckinRecord> selectByConditions(String accountId,
                                                    LocalDate startDate,
                                                    LocalDate endDate, LocalDate checkinDate,
                                                    Boolean asc) {
        QueryWrapper wrapper = QueryWrapper.create()
            .where(CheckinRecord::getAccountId).eq(accountId);

        // 可选条件：精确日期
        if (checkinDate != null) {
            wrapper.and(CheckinRecord::getCheckinDate).eq(checkinDate);
        } else {
            // 可选条件：日期范围
            if (startDate != null) {
                wrapper.and(CheckinRecord::getCheckinDate).ge(startDate);
            }
            if (endDate != null) {
                wrapper.and(CheckinRecord::getCheckinDate).le(endDate);
            }
        }

        // 排序
        wrapper.orderBy(CheckinRecord::getCheckinDate, asc != null && asc);

        return selectListByQuery(wrapper);
    }

    /**
     * 后台管理：分页查询所有用户签到记录
     */
    default Page<CheckinRecord> selectAdminPage(Page<CheckinRecord> page) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(CheckinRecord::getIsDeleted).eq(0)
                .orderBy(CheckinRecord::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
