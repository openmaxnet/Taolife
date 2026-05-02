package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.AiSceneConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI场景配置Mapper接口
 *
 * @author 文二
 * @date 2026-04-14
 */
@Mapper
public interface AiSceneConfigMapper extends BaseMapper<AiSceneConfig> {

    /**
     * 根据场景编码查询配置
     *
     * @param sceneCode 场景编码
     * @return 场景配置
     */
    default AiSceneConfig selectBySceneCode(String sceneCode) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AiSceneConfig::getSceneCode).eq(sceneCode)
                .and(AiSceneConfig::getIsEnabled).eq(1)
                .limit(1)
        );
    }

    /**
     * 查询所有启用的场景配置
     *
     * @return 场景配置列表
     */
    default List<AiSceneConfig> selectAllEnabled() {
        return selectListByQuery(
            QueryWrapper.create()
                .where(AiSceneConfig::getIsEnabled).eq(1)
                .orderBy(AiSceneConfig::getSceneCode, true)
        );
    }

    /**
     * 分页查询场景配置列表
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @param keyword  关键词（可选）
     * @return 分页结果
     */
    default Page<AiSceneConfig> selectPageByKeyword(Integer pageNo, Integer pageSize, String keyword) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(AiSceneConfig::getSceneName).like(keyword);
        }

        wrapper.orderBy(AiSceneConfig::getCreateTime, false);
        return paginate(pageNo, pageSize, wrapper);
    }
}
