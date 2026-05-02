package com.taolife.aichat.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.aichat.entity.AiModel;
import com.taolife.aichat.entity.AiProvider;
import com.taolife.aichat.mapper.AiModelMapper;
import com.taolife.aichat.mapper.AiProviderMapper;
import com.taolife.aichat.param.AiModelPageParam;
import com.taolife.aichat.param.AiModelSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.service.IAiModelService;
import com.taolife.aichat.vo.AiModelVO;
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
 * AI模型服务实现
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiModelServiceImpl implements IAiModelService {

    private final AiModelMapper aiModelMapper;
    private final AiProviderMapper aiProviderMapper;

    /**
     * 分页查询模型列表
     *
     * @param param 分页查询参数
     * @return 模型分页结果
     */
    @Override
    public PageResult<AiModelVO> getModelPage(AiModelPageParam param) {
        Page<AiModel> page = aiModelMapper.selectPageByParam(
                param.getPageNo(), param.getPageSize(), param.getProviderId(), param.getModelType(), param.getKeyword());

        PageResult<AiModel> pageResult = PageResult.of(page);
        PageResult<AiModelVO> result = new PageResult<>();
        result.setList(pageResult.getList().stream().map(this::convertToVO).toList());
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    @Override
    public AiModelVO getModelDetail(String id) {
        AiModel instance = aiModelMapper.selectOneById(id);
        if (instance == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模型不存在");
        }
        return convertToVO(instance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createModel(AiModelSaveParam param) {
        // 验证厂商存在
        AiProvider provider = aiProviderMapper.selectOneById(param.getProviderId());
        if (provider == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "厂商不存在");
        }

        AiModel instance = new AiModel();
        instance.setProviderId(param.getProviderId());
        instance.setModelCode(param.getModelCode());
        instance.setModelName(param.getModelName());
        instance.setModelType(param.getModelType());
        instance.setTemperature(param.getTemperature());
        instance.setMaxTokens(param.getMaxTokens());
        instance.setTopP(param.getTopP());
        instance.setSupportsThinking(param.getSupportsThinking());
        instance.setSupportsImage(param.getSupportsImage());
        instance.setMaxConcurrency(param.getMaxConcurrency());
        instance.setExtraParamsJson(param.getExtraParamsJson());
        instance.setCapabilitiesJson(param.getCapabilitiesJson());
        instance.setIsDefault(param.getIsDefault() != null ? param.getIsDefault() : 0);
        // 设置为默认时，先取消同类型其他默认
        if (instance.getIsDefault() == 1) {
            aiModelMapper.resetDefaultByType(instance.getModelType());
        }
        instance.setStatus(param.getStatus() != null ? param.getStatus() : 1);
        instance.setCreateTime(LocalDateTime.now());
        instance.setUpdateTime(LocalDateTime.now());
        aiModelMapper.insert(instance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyModelInfo(AiModelSaveParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        AiModel instance = aiModelMapper.selectOneById(param.getId());
        if (instance == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模型不存在");
        }
        instance.setModelCode(param.getModelCode());
        instance.setModelName(param.getModelName());
        instance.setModelType(param.getModelType());
        if (param.getTemperature() != null) {
            instance.setTemperature(param.getTemperature());
        }
        if (param.getMaxTokens() != null) {
            instance.setMaxTokens(param.getMaxTokens());
        }
        if (param.getTopP() != null) {
            instance.setTopP(param.getTopP());
        }
        if (param.getSupportsThinking() != null) {
            instance.setSupportsThinking(param.getSupportsThinking());
        }
        if (param.getSupportsImage() != null) {
            instance.setSupportsImage(param.getSupportsImage());
        }
        if (param.getMaxConcurrency() != null) {
            instance.setMaxConcurrency(param.getMaxConcurrency());
        }
        if (param.getExtraParamsJson() != null) {
            instance.setExtraParamsJson(param.getExtraParamsJson());
        }
        if (param.getCapabilitiesJson() != null) {
            instance.setCapabilitiesJson(param.getCapabilitiesJson());
        }
        if (param.getIsDefault() != null) {
            // 设置为默认时，先取消同类型其他默认
            if (param.getIsDefault() == 1) {
                aiModelMapper.resetDefaultByType(instance.getModelType());
            }
            instance.setIsDefault(param.getIsDefault());
        }
        if (param.getStatus() != null) {
            instance.setStatus(param.getStatus());
        }
        instance.setUpdateTime(LocalDateTime.now());
        aiModelMapper.update(instance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeModel(AiRemoveParam param) {
        AiModel instance = aiModelMapper.selectOneById(param.getId());
        if (instance == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模型不存在");
        }
        aiModelMapper.deleteById(param.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyModelStatus(AiStatusParam param) {
        AiModel instance = aiModelMapper.selectOneById(param.getId());
        if (instance == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "模型不存在");
        }
        instance.setStatus(param.getStatus());
        instance.setUpdateTime(LocalDateTime.now());
        aiModelMapper.update(instance);
    }

    @Override
    public AiModelVO getModelByCode(String modelCode) {
        AiModel instance = aiModelMapper.selectByModelCode(modelCode);
        if (instance == null) {
            return null;
        }
        return convertToVO(instance);
    }

    @Override
    public List<AiModelVO> getModelListByProviderId(String providerId) {
        return aiModelMapper.selectByProviderId(providerId)
                .stream().map(this::convertToVO).toList();
    }

    private AiModelVO convertToVO(AiModel instance) {
        AiModelVO vo = new AiModelVO();
        vo.setId(instance.getId());
        vo.setProviderId(instance.getProviderId());
        vo.setModelCode(instance.getModelCode());
        vo.setModelName(instance.getModelName());
        vo.setModelType(instance.getModelType());
        vo.setTemperature(instance.getTemperature());
        vo.setMaxTokens(instance.getMaxTokens());
        vo.setTopP(instance.getTopP());
        vo.setSupportsThinking(instance.getSupportsThinking());
        vo.setSupportsImage(instance.getSupportsImage());
        vo.setMaxConcurrency(instance.getMaxConcurrency());
        vo.setExtraParamsJson(instance.getExtraParamsJson());
        vo.setCapabilitiesJson(instance.getCapabilitiesJson());
        vo.setIsDefault(instance.getIsDefault());
        vo.setStatus(instance.getStatus());
        vo.setCreateTime(instance.getCreateTime());
        vo.setUpdateTime(instance.getUpdateTime());

        // 填充厂商名称
        if (instance.getProviderId() != null) {
            AiProvider provider = aiProviderMapper.selectOneById(instance.getProviderId());
            if (provider != null) {
                vo.setProviderName(provider.getProviderName());
            }
        }
        return vo;
    }
}
