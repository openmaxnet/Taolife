package com.taolife.aichat.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.aichat.entity.AiModel;
import com.taolife.aichat.entity.AiPromptTemplate;
import com.taolife.aichat.entity.AiSceneConfig;
import com.taolife.aichat.mapper.AiModelMapper;
import com.taolife.aichat.mapper.AiPromptTemplateMapper;
import com.taolife.aichat.mapper.AiSceneConfigMapper;
import com.taolife.aichat.param.AiSceneConfigPageParam;
import com.taolife.aichat.param.AiSceneConfigSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.service.IAiSceneConfigService;
import com.taolife.aichat.vo.AiSceneConfigVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI场景配置服务实现
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiSceneConfigServiceImpl implements IAiSceneConfigService {

    private final AiSceneConfigMapper aiSceneConfigMapper;
    private final AiModelMapper aiModelMapper;
    private final AiPromptTemplateMapper aiPromptTemplateMapper;

    /**
     * 分页查询场景配置列表
     *
     * @param param 分页查询参数
     * @return 场景分页结果
     */
    @Override
    public PageResult<AiSceneConfigVO> getScenePage(AiSceneConfigPageParam param) {
        Page<AiSceneConfig> page = aiSceneConfigMapper.selectPageByKeyword(
                param.getPageNo(), param.getPageSize(), param.getKeyword());

        PageResult<AiSceneConfig> pageResult = PageResult.of(page);
        PageResult<AiSceneConfigVO> result = new PageResult<>();
        result.setList(pageResult.getList().stream().map(this::convertToVO).toList());
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 获取场景配置详情
     *
     * @param id 场景配置ID
     * @return 场景详情VO
     */
    @Override
    public AiSceneConfigVO getSceneDetail(String id) {
        AiSceneConfig scene = aiSceneConfigMapper.selectOneById(id);
        if (scene == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "场景配置不存在");
        }
        return convertToVO(scene);
    }

    /**
     * 创建场景配置
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createScene(AiSceneConfigSaveParam param) {
        // 验证模型实例存在
        if (param.getModelInstanceId() != null) {
            AiModel instance = aiModelMapper.selectOneById(param.getModelInstanceId());
            if (instance == null) {
                throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模型实例不存在");
            }
        }

        // 验证提示词模板存在
        if (param.getPromptTemplateId() != null) {
            AiPromptTemplate template = aiPromptTemplateMapper.selectOneById(param.getPromptTemplateId());
            if (template == null) {
                throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "提示词模板不存在");
            }
        }

        AiSceneConfig scene = new AiSceneConfig();
        scene.setSceneCode(param.getSceneCode());
        scene.setSceneName(param.getSceneName());
        scene.setModelInstanceId(param.getModelInstanceId());
        scene.setPromptTemplateId(param.getPromptTemplateId());
        scene.setParametersJson(param.getParametersJson());
        scene.setExtraConfigJson(param.getExtraConfigJson());
        scene.setIsEnabled(param.getIsEnabled() != null ? param.getIsEnabled() : 1);
        scene.setDescription(param.getDescription());
        scene.setCreateTime(LocalDateTime.now());
        scene.setUpdateTime(LocalDateTime.now());
        aiSceneConfigMapper.insert(scene);
    }

    /**
     * 修改场景配置信息
     *
     * @param param 修改参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySceneInfo(AiSceneConfigSaveParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        AiSceneConfig scene = aiSceneConfigMapper.selectOneById(param.getId());
        if (scene == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "场景配置不存在");
        }
        scene.setSceneName(param.getSceneName());
        if (param.getModelInstanceId() != null) {
            AiModel instance = aiModelMapper.selectOneById(param.getModelInstanceId());
            if (instance == null) {
                throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模型实例不存在");
            }
            scene.setModelInstanceId(param.getModelInstanceId());
        }
        if (param.getPromptTemplateId() != null) {
            AiPromptTemplate template = aiPromptTemplateMapper.selectOneById(param.getPromptTemplateId());
            if (template == null) {
                throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "提示词模板不存在");
            }
            scene.setPromptTemplateId(param.getPromptTemplateId());
        }
        if (param.getParametersJson() != null) {
            scene.setParametersJson(param.getParametersJson());
        }
        if (param.getExtraConfigJson() != null) {
            scene.setExtraConfigJson(param.getExtraConfigJson());
        }
        if (param.getIsEnabled() != null) {
            scene.setIsEnabled(param.getIsEnabled());
        }
        if (param.getDescription() != null) {
            scene.setDescription(param.getDescription());
        }
        scene.setUpdateTime(LocalDateTime.now());
        aiSceneConfigMapper.update(scene);
    }

    /**
     * 删除场景配置
     *
     * @param param 删除参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeScene(AiRemoveParam param) {
        AiSceneConfig scene = aiSceneConfigMapper.selectOneById(param.getId());
        if (scene == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "场景配置不存在");
        }
        aiSceneConfigMapper.deleteById(param.getId());
    }

    /**
     * 修改场景配置状态
     *
     * @param param 状态修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySceneStatus(AiStatusParam param) {
        AiSceneConfig scene = aiSceneConfigMapper.selectOneById(param.getId());
        if (scene == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "场景配置不存在");
        }
        scene.setIsEnabled(param.getStatus());
        scene.setUpdateTime(LocalDateTime.now());
        aiSceneConfigMapper.update(scene);
    }

    /**
     * 根据场景编码获取场景配置
     *
     * @param sceneCode 场景编码
     * @return 场景配置VO
     */
    @Override
    public AiSceneConfigVO getBySceneCode(String sceneCode) {
        AiSceneConfig scene = aiSceneConfigMapper.selectBySceneCode(sceneCode);
        if (scene == null) {
            return null;
        }
        return convertToVO(scene);
    }

    /**
     * 获取所有启用的场景配置
     *
     * @return 场景配置列表
     */
    @Override
    public List<AiSceneConfigVO> getAllEnabled() {
        return aiSceneConfigMapper.selectAllEnabled()
                .stream().map(this::convertToVO).toList();
    }

    /**
     * 场景配置实体转VO
     *
     * @param scene 场景配置实体
     * @return 场景配置VO
     */
    private AiSceneConfigVO convertToVO(AiSceneConfig scene) {
        AiSceneConfigVO vo = new AiSceneConfigVO();
        vo.setId(scene.getId());
        vo.setSceneCode(scene.getSceneCode());
        vo.setSceneName(scene.getSceneName());
        vo.setModelInstanceId(scene.getModelInstanceId());
        vo.setPromptTemplateId(scene.getPromptTemplateId());
        vo.setParametersJson(scene.getParametersJson());
        vo.setExtraConfigJson(scene.getExtraConfigJson());
        vo.setIsEnabled(scene.getIsEnabled());
        vo.setDescription(scene.getDescription());
        vo.setCreateTime(scene.getCreateTime());
        vo.setUpdateTime(scene.getUpdateTime());

        // 填充模型信息
        if (scene.getModelInstanceId() != null) {
            AiModel instance = aiModelMapper.selectOneById(scene.getModelInstanceId());
            if (instance != null) {
                vo.setModelName(instance.getModelName());
                vo.setModelCode(instance.getModelCode());
            }
        }

        // 填充模板信息
        if (scene.getPromptTemplateId() != null) {
            AiPromptTemplate template = aiPromptTemplateMapper.selectOneById(scene.getPromptTemplateId());
            if (template != null) {
                vo.setPromptTemplateName(template.getTemplateName());
            }
        }

        return vo;
    }
}
