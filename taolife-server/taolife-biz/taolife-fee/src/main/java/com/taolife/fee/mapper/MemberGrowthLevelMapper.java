package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.MemberGrowthLevel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员成长等级 Mapper 接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface MemberGrowthLevelMapper extends BaseMapper<MemberGrowthLevel> {

    /**
     * 查询所有已启用的等级列表（按成长值升序）
     *
     * @return 已启用的等级列表
     */
    default List<MemberGrowthLevel> selectEnabledLevels() {
        return selectListByQuery(
            QueryWrapper.create()
                .where(MemberGrowthLevel::getIsEnabled).eq(1)
                .orderBy(MemberGrowthLevel::getMinGrowthValue, true)
        );
    }

    /**
     * 按等级值查询
     *
     * @param level 等级值
     * @return 会员成长等级
     */
    default MemberGrowthLevel selectByLevel(Integer level) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(MemberGrowthLevel::getLevel).eq(level)
                .and(MemberGrowthLevel::getIsEnabled).eq(1)
                .limit(1)
        );
    }

    /**
     * 分页查询（管理员）
     *
     * @param page 分页参数
     * @return 分页结果
     */
    default Page<MemberGrowthLevel> selectAdminPage(Page<MemberGrowthLevel> page) {
        QueryWrapper wrapper = QueryWrapper.create()
            .orderBy(MemberGrowthLevel::getMinGrowthValue, true);
        return paginate(page, wrapper);
    }
}
