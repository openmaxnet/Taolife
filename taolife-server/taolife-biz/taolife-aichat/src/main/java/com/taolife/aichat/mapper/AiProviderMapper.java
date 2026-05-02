package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.AiProvider;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI模型厂商配置Mapper接口
 *
 * @author 文二
 * @date 2026-04-14
 */
@Mapper
public interface AiProviderMapper extends BaseMapper<AiProvider> {

    /**
     * 根据厂商类型查询启用的厂商列表
     *
     * @param providerType 厂商类型：chat/embedding/image
     * @return 厂商列表
     */
    default List<AiProvider> selectByProviderType(String providerType) {
        QueryWrapper wrapper = QueryWrapper.create()
            .where(AiProvider::getStatus).eq(1)
            .orderBy(AiProvider::getPriority, false);

        if (providerType != null && !providerType.trim().isEmpty()) {
            wrapper.where(AiProvider::getProviderType).eq(providerType);
        }

        return selectListByQuery(wrapper);
    }

    /**
     * 查询某类型默认启用的厂商
     *
     * @param providerType 厂商类型
     * @return 默认厂商
     */
    default AiProvider selectDefaultByType(String providerType) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AiProvider::getProviderType).eq(providerType)
                .and(AiProvider::getIsDefault).eq(1)
                .and(AiProvider::getStatus).eq(1)
                .limit(1)
        );
    }

    /**
     * 重置某类型的所有默认厂商为非默认
     *
     * @param providerType 厂商类型
     */
    default void resetDefaultByType(String providerType) {
        updateByQuery(
            new AiProvider() {{ setIsDefault(0); }},
            QueryWrapper.create()
                .where(AiProvider::getProviderType).eq(providerType)
                .and(AiProvider::getIsDefault).eq(1)
        );
    }

    /**
     * 分页查询厂商列表
     *
     * @param pageNo        页码
     * @param pageSize      每页大小
     * @param providerType  厂商类型（可选）
     * @param keyword       关键词（可选）
     * @return 分页结果
     */
    default Page<AiProvider> selectPageByTypeAndKeyword(Integer pageNo, Integer pageSize, String providerType, String keyword) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (providerType != null && !providerType.trim().isEmpty()) {
            wrapper.where(AiProvider::getProviderType).eq(providerType);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(AiProvider::getProviderName).like(keyword);
        }

        wrapper.orderBy(AiProvider::getPriority, false);
        return paginate(pageNo, pageSize, wrapper);
    }
}
