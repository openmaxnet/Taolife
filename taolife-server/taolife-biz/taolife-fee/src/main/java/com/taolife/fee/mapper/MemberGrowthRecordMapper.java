package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.MemberGrowthRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员成长值记录 Mapper 接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface MemberGrowthRecordMapper extends BaseMapper<MemberGrowthRecord> {

    /**
     * 分页查询成长值记录（管理员）
     *
     * @param page      分页参数
     * @param accountId 账号ID（可选，为空则查全部）
     * @return 分页结果
     */
    default Page<MemberGrowthRecord> selectAdminPage(Page<MemberGrowthRecord> page, String accountId) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.where(MemberGrowthRecord::getIsDeleted).eq(0);
        if (accountId != null && !accountId.isEmpty()) {
            wrapper.and(MemberGrowthRecord::getAccountId).eq(accountId);
        }
        wrapper.orderBy(MemberGrowthRecord::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
