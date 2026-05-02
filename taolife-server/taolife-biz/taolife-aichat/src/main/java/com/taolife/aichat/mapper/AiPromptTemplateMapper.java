package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.AiPromptTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI提示词模板Mapper接口
 *
 * @author 文二
 * @date 2026-04-14
 */
@Mapper
public interface AiPromptTemplateMapper extends BaseMapper<AiPromptTemplate> {

    /**
     * 根据模板编码查询最新版本模板
     *
     * @param templateCode 模板编码
     * @return 最新版本的模板
     */
    default AiPromptTemplate selectLatestByCode(String templateCode) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AiPromptTemplate::getTemplateCode).eq(templateCode)
                .and(AiPromptTemplate::getIsEnabled).eq(1)
                .orderBy(AiPromptTemplate::getVersion, false)
                .limit(1)
        );
    }

    /**
     * 根据模板编码和版本查询模板
     *
     * @param templateCode 模板编码
     * @param version       版本号
     * @return 模板
     */
    default AiPromptTemplate selectByCodeAndVersion(String templateCode, Integer version) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AiPromptTemplate::getTemplateCode).eq(templateCode)
                .and(AiPromptTemplate::getVersion).eq(version)
                .and(AiPromptTemplate::getIsEnabled).eq(1)
                .limit(1)
        );
    }

    /**
     * 查询所有启用的模板
     *
     * @return 模板列表
     */
    default List<AiPromptTemplate> selectAllEnabled() {
        return selectListByQuery(
            QueryWrapper.create()
                .where(AiPromptTemplate::getIsEnabled).eq(1)
                .orderBy(AiPromptTemplate::getTemplateCode, true)
        );
    }

    /**
     * 根据模板类型查询所有启用的模板
     *
     * @param templateType 模板类型
     * @return 模板列表
     */
    default List<AiPromptTemplate> selectEnabledByType(String templateType) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(AiPromptTemplate::getTemplateType).eq(templateType)
                .and(AiPromptTemplate::getIsEnabled).eq(1)
                .orderBy(AiPromptTemplate::getUpdateTime, false)
        );
    }

    /**
     * 分页查询模板列表
     *
     * @param pageNo       页码
     * @param pageSize     每页大小
     * @param templateType 模板类型（可选）
     * @param keyword      关键词（可选）
     * @return 分页结果
     */
    default Page<AiPromptTemplate> selectPageByParam(Integer pageNo, Integer pageSize, String templateType, String keyword) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (templateType != null && !templateType.trim().isEmpty()) {
            wrapper.where(AiPromptTemplate::getTemplateType).eq(templateType);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(AiPromptTemplate::getTemplateName).like(keyword);
        }

        wrapper.orderBy(AiPromptTemplate::getTemplateCode, true);
        return paginate(pageNo, pageSize, wrapper);
    }
}
