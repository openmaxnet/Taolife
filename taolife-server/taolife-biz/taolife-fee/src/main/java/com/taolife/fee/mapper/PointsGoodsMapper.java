package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.PointsGoods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 积分商品Mapper接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface PointsGoodsMapper extends BaseMapper<PointsGoods> {

    /**
     * 查询已启用的积分商品列表
     *
     * @return 积分商品列表
     */
    default List<PointsGoods> selectEnabled() {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(PointsGoods::getIsEnabled).eq(1)
                .orderBy(PointsGoods::getSortOrder, true);
        return selectListByQuery(wrapper);
    }

    /**
     * 分页查询积分商品
     *
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param goodsCode 商品编码（模糊查询，可为null）
     * @param goodsType 商品类型（精确查询，可为null）
     * @return 分页结果
     */
    default Page<PointsGoods> selectGoodsPage(Integer pageNo, Integer pageSize, String goodsCode, Integer goodsType) {
        QueryWrapper wrapper = QueryWrapper.create()
                .orderBy(PointsGoods::getSortOrder, true)
                .orderBy(PointsGoods::getCreateTime, false);
        if (goodsCode != null && !goodsCode.isEmpty()) {
            wrapper.and(PointsGoods::getGoodsCode).like(goodsCode);
        }
        if (goodsType != null) {
            wrapper.and(PointsGoods::getGoodsType).eq(goodsType);
        }
        return paginate(pageNo, pageSize, wrapper);
    }
}
