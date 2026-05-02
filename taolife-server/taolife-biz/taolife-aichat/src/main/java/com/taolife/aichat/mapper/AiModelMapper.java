package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.AiModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI模型配置Mapper接口
 *
 * @author 文二
 * @date 2026-04-14
 */
@Mapper
public interface AiModelMapper extends BaseMapper<AiModel> {

    /**
     * 根据厂商ID查询模型列表
     *
     * @param providerId 厂商ID
     * @return 模型列表
     */
    default List<AiModel> selectByProviderId(String providerId) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(AiModel::getProviderId).eq(providerId)
                .and(AiModel::getStatus).eq(1)
                .orderBy(AiModel::getIsDefault, false)
        );
    }

    /**
     * 根据模型类型查询默认模型
     *
     * @param modelType 模型类型：chat/embedding
     * @return 默认模型
     */
    default AiModel selectDefaultByType(String modelType) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AiModel::getModelType).eq(modelType)
                .and(AiModel::getIsDefault).eq(1)
                .and(AiModel::getStatus).eq(1)
                .limit(1)
        );
    }

    /**
     * 重置某类型的所有默认模型为非默认
     *
     * @param modelType 模型类型
     */
    default void resetDefaultByType(String modelType) {
        updateByQuery(
            new AiModel() {{ setIsDefault(0); }},
            QueryWrapper.create()
                .where(AiModel::getModelType).eq(modelType)
                .and(AiModel::getIsDefault).eq(1)
        );
    }

    /**
     * 根据模型类型和厂商ID查询默认模型
     *
     * @param modelType   模型类型：chat/embedding
     * @param providerId  厂商ID
     * @return 默认模型
     */
    default AiModel selectDefaultByTypeAndProviderId(String modelType, String providerId) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AiModel::getModelType).eq(modelType)
                .and(AiModel::getProviderId).eq(providerId)
                .and(AiModel::getIsDefault).eq(1)
                .and(AiModel::getStatus).eq(1)
                .limit(1)
        );
    }

    /**
     * 根据模型编码查询模型
     *
     * @param modelCode 模型编码
     * @return 模型
     */
    default AiModel selectByModelCode(String modelCode) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AiModel::getModelCode).eq(modelCode)
                .and(AiModel::getStatus).eq(1)
                .limit(1)
        );
    }

    /**
     * 分页查询模型列表
     *
     * @param pageNo       页码
     * @param pageSize     每页大小
     * @param providerId   厂商ID（可选）
     * @param modelType    模型类型（可选）
     * @param keyword      关键词（可选）
     * @return 分页结果
     */
    default Page<AiModel> selectPageByParam(Integer pageNo, Integer pageSize, String providerId, String modelType, String keyword) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (providerId != null && !providerId.trim().isEmpty()) {
            wrapper.where(AiModel::getProviderId).eq(providerId);
        }

        if (modelType != null && !modelType.trim().isEmpty()) {
            wrapper.and(AiModel::getModelType).eq(modelType);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(AiModel::getModelName).like(keyword);
        }

        wrapper.orderBy(AiModel::getCreateTime, false);
        return paginate(pageNo, pageSize, wrapper);
    }
}
