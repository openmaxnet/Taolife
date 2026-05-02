package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.HomeBanner;
import com.taolife.fee.param.BannerPageAdminParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 首页轮播Mapper接口
 *
 * @author 文二
 * @date 2026-04-20
 */
@Mapper
public interface HomeBannerMapper extends BaseMapper<HomeBanner> {

    /**
     * 根据ID查询轮播（带逻辑删除过滤）
     *
     * @param id 轮播ID
     * @return 轮播对象
     */
    default HomeBanner selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(HomeBanner::getId).eq(id)
                .and(HomeBanner::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 查询启用的轮播列表（按排序降序）
     *
     * @return 轮播列表
     */
    default List<HomeBanner> selectEnabledBanners() {
        return selectListByQuery(
            QueryWrapper.create()
                .where(HomeBanner::getIsDeleted).eq(0)
                .and(HomeBanner::getStatus).eq(1)
                .orderBy(HomeBanner::getSortOrder).desc()
        );
    }

    /**
     * 分页查询轮播列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<HomeBanner> selectPageByParam(BannerPageAdminParam param) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(HomeBanner::getIsDeleted).eq(0);

        if (param.getStatus() != null) {
            wrapper.and(HomeBanner::getStatus).eq(param.getStatus());
        }

        wrapper.orderBy(HomeBanner::getSortOrder).desc();
        return paginate(param.getPageNo(), param.getPageSize(), wrapper);
    }
}
