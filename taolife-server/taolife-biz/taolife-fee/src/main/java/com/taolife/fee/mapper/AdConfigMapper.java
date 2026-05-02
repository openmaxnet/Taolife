package com.taolife.fee.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.fee.entity.AdConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 广告配置Mapper接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface AdConfigMapper extends BaseMapper<AdConfig> {

    /**
     * 查询已启用的广告配置列表
     *
     * @return 广告配置列表
     */
    default List<AdConfig> selectEnabled() {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(AdConfig::getIsEnabled).eq(1)
                .orderBy(AdConfig::getPriority, false);
        return selectListByQuery(wrapper);
    }

    /**
     * 分页查询广告配置
     * 根据条件分页获取广告配置列表
     *
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param configKey 配置KEY（模糊查询，可为null）
     * @param adType 广告类型（精确查询，可为null）
     * @return 分页结果
     */
    default Page<AdConfig> selectAdConfigPage(Integer pageNo, Integer pageSize, String configKey, Integer adType) {
        QueryWrapper wrapper = QueryWrapper.create()
                .orderBy(AdConfig::getPriority, false)
                .orderBy(AdConfig::getCreateTime, false);
        if (configKey != null && !configKey.isEmpty()) {
            wrapper.and(AdConfig::getConfigKey).like(configKey);
        }
        if (adType != null) {
            wrapper.and(AdConfig::getAdType).eq(adType);
        }
        return paginate(pageNo, pageSize, wrapper);
    }
}
