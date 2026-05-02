package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.AiSceneModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 场景模型绑定 Mapper 接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface AiSceneModelMapper extends BaseMapper<AiSceneModel> {

    /**
     * 按场景配置ID查询绑定的模型列表（按优先级和权重降序）
     *
     * @param sceneConfigId 场景配置ID
     * @return 场景模型绑定列表
     */
    default List<AiSceneModel> selectBySceneConfigId(String sceneConfigId) {
        return selectListByQuery(QueryWrapper.create()
                .where("scene_config_id = ?", sceneConfigId)
                .and("status = 1")
                .orderBy("priority DESC", "weight DESC"));
    }
}
