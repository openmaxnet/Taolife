package com.taolife.aichat.service;

import com.taolife.aichat.param.AiSceneConfigPageParam;
import com.taolife.aichat.param.AiSceneConfigSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.vo.AiSceneConfigVO;
import com.taolife.common.utils.PageResult;

import java.util.List;

/**
 * AI场景配置服务接口
 *
 * @author 文二
 * @date 2026-04-14
 */
public interface IAiSceneConfigService {

    /**
     * 分页查询场景配置列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<AiSceneConfigVO> getScenePage(AiSceneConfigPageParam param);

    /**
     * 获取场景配置详情
     *
     * @param id 场景配置ID
     * @return 场景配置详情
     */
    AiSceneConfigVO getSceneDetail(String id);

    /**
     * 创建场景配置
     *
     * @param param 创建参数
     */
    void createScene(AiSceneConfigSaveParam param);

    /**
     * 修改场景配置信息
     *
     * @param param 修改参数
     */
    void modifySceneInfo(AiSceneConfigSaveParam param);

    /**
     * 删除场景配置
     *
     * @param param 删除参数
     */
    void removeScene(AiRemoveParam param);

    /**
     * 修改场景配置状态
     *
     * @param param 状态修改参数
     */
    void modifySceneStatus(AiStatusParam param);

    /**
     * 根据场景编码获取场景配置
     *
     * @param sceneCode 场景编码
     * @return 场景配置
     */
    AiSceneConfigVO getBySceneCode(String sceneCode);

    /**
     * 获取所有启用的场景配置
     *
     * @return 场景配置列表
     */
    List<AiSceneConfigVO> getAllEnabled();
}
