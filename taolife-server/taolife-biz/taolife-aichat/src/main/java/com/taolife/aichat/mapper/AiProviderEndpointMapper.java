package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.AiProviderEndpoint;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI厂商端点配置Mapper接口
 *
 * @author 文二
 * @date 2026-04-14
 */
@Mapper
public interface AiProviderEndpointMapper extends BaseMapper<AiProviderEndpoint> {

    /**
     * 根据厂商ID查询所有启用的端点
     *
     * @param providerId 厂商ID
     * @return 端点列表
     */
    default List<AiProviderEndpoint> selectByProviderId(String providerId) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(AiProviderEndpoint::getProviderId).eq(providerId)
                .and(AiProviderEndpoint::getIsEnabled).eq(1)
                .orderBy(AiProviderEndpoint::getEndpointType, true)
        );
    }

    /**
     * 根据厂商ID和端点类型查询端点
     *
     * @param providerId   厂商ID
     * @param endpointType 端点类型
     * @return 端点列表
     */
    default List<AiProviderEndpoint> selectByProviderIdAndType(String providerId, String endpointType) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(AiProviderEndpoint::getProviderId).eq(providerId)
                .and(AiProviderEndpoint::getEndpointType).eq(endpointType)
                .and(AiProviderEndpoint::getIsEnabled).eq(1)
        );
    }

    /**
     * 根据厂商ID查询单个启用的端点
     *
     * @param providerId   厂商ID
     * @param endpointType 端点类型
     * @return 端点
     */
    default AiProviderEndpoint selectOneByProviderIdAndType(String providerId, String endpointType) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AiProviderEndpoint::getProviderId).eq(providerId)
                .and(AiProviderEndpoint::getEndpointType).eq(endpointType)
                .and(AiProviderEndpoint::getIsEnabled).eq(1)
                .limit(1)
        );
    }

    /**
     * 分页查询端点配置列表
     *
     * @param pageNo     页码
     * @param pageSize   每页大小
     * @param providerId 厂商ID（可选）
     * @param keyword     关键词（可选）
     * @return 分页结果
     */
    default Page<AiProviderEndpoint> selectPageByProviderAndKeyword(Integer pageNo, Integer pageSize, String providerId, String keyword) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (providerId != null && !providerId.trim().isEmpty()) {
            wrapper.and(AiProviderEndpoint::getProviderId).eq(providerId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(AiProviderEndpoint::getDescription).like(keyword);
        }

        wrapper.orderBy(AiProviderEndpoint::getCreateTime, false);
        return paginate(pageNo, pageSize, wrapper);
    }
}