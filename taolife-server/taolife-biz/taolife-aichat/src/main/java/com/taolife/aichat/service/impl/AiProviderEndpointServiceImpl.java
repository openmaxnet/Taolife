package com.taolife.aichat.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.aichat.entity.AiProvider;
import com.taolife.aichat.entity.AiProviderEndpoint;
import com.taolife.aichat.enums.AiEndpointTypeEnum;
import com.taolife.aichat.mapper.AiProviderMapper;
import com.taolife.aichat.mapper.AiProviderEndpointMapper;
import com.taolife.aichat.param.AiProviderEndpointPageParam;
import com.taolife.aichat.param.AiProviderEndpointSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.service.IAiProviderEndpointService;
import com.taolife.aichat.vo.AiProviderEndpointVO;
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
 * AI厂商端点配置服务实现
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiProviderEndpointServiceImpl implements IAiProviderEndpointService {

    private final AiProviderEndpointMapper aiProviderEndpointMapper;
    private final AiProviderMapper aiProviderMapper;

    /**
     * 分页查询端点配置列表
     *
     * @param param 分页查询参数
     * @return 端点分页结果
     */
    @Override
    public PageResult<AiProviderEndpointVO> getEndpointPage(AiProviderEndpointPageParam param) {
        Page<AiProviderEndpoint> page = aiProviderEndpointMapper.selectPageByProviderAndKeyword(
                param.getPageNo(), param.getPageSize(), param.getProviderId(), param.getKeyword());

        PageResult<AiProviderEndpoint> pageResult = PageResult.of(page);
        PageResult<AiProviderEndpointVO> result = new PageResult<>();
        result.setList(pageResult.getList().stream().map(this::convertToVO).toList());
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 获取端点配置详情
     *
     * @param id 端点配置ID
     * @return 端点详情VO
     */
    @Override
    public AiProviderEndpointVO getEndpointDetail(String id) {
        AiProviderEndpoint endpoint = aiProviderEndpointMapper.selectOneById(id);
        if (endpoint == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "端点配置不存在");
        }
        return convertToVO(endpoint);
    }

    /**
     * 创建端点配置
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createEndpoint(AiProviderEndpointSaveParam param) {
        // 校验厂商存在
        AiProvider provider = aiProviderMapper.selectOneById(param.getProviderId());
        if (provider == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "厂商不存在");
        }

        AiProviderEndpoint endpoint = new AiProviderEndpoint();
        endpoint.setProviderId(param.getProviderId());
        endpoint.setEndpointType(param.getEndpointType());
        endpoint.setEndpointUri(param.getEndpointUri());
        endpoint.setRequestType(param.getRequestType());
        endpoint.setTimeoutMs(param.getTimeoutMs() != null ? param.getTimeoutMs() : 30000);
        endpoint.setRetryTimes(param.getRetryTimes() != null ? param.getRetryTimes() : 3);
        endpoint.setIsEnabled(param.getIsEnabled() != null ? param.getIsEnabled() : 1);
        endpoint.setDescription(param.getDescription());
        endpoint.setConfigJson(param.getConfigJson());
        endpoint.setCreateTime(LocalDateTime.now());
        endpoint.setUpdateTime(LocalDateTime.now());
        aiProviderEndpointMapper.insert(endpoint);
    }

    /**
     * 修改端点配置信息
     *
     * @param param 修改参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyEndpointInfo(AiProviderEndpointSaveParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        AiProviderEndpoint endpoint = aiProviderEndpointMapper.selectOneById(param.getId());
        if (endpoint == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "端点配置不存在");
        }
        if (param.getEndpointType() != null) {
            endpoint.setEndpointType(param.getEndpointType());
        }
        if (param.getEndpointUri() != null) {
            endpoint.setEndpointUri(param.getEndpointUri());
        }
        if (param.getRequestType() != null) {
            endpoint.setRequestType(param.getRequestType());
        }
        if (param.getTimeoutMs() != null) {
            endpoint.setTimeoutMs(param.getTimeoutMs());
        }
        if (param.getRetryTimes() != null) {
            endpoint.setRetryTimes(param.getRetryTimes());
        }
        if (param.getIsEnabled() != null) {
            endpoint.setIsEnabled(param.getIsEnabled());
        }
        if (param.getDescription() != null) {
            endpoint.setDescription(param.getDescription());
        }
        if (param.getConfigJson() != null) {
            endpoint.setConfigJson(param.getConfigJson());
        }
        endpoint.setUpdateTime(LocalDateTime.now());
        aiProviderEndpointMapper.update(endpoint);
    }

    /**
     * 删除端点配置
     *
     * @param param 删除参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeEndpoint(AiRemoveParam param) {
        AiProviderEndpoint endpoint = aiProviderEndpointMapper.selectOneById(param.getId());
        if (endpoint == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "端点配置不存在");
        }
        aiProviderEndpointMapper.deleteById(param.getId());
    }

    /**
     * 修改端点配置状态
     *
     * @param param 状态修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyEndpointStatus(AiStatusParam param) {
        AiProviderEndpoint endpoint = aiProviderEndpointMapper.selectOneById(param.getId());
        if (endpoint == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "端点配置不存在");
        }
        endpoint.setIsEnabled(param.getStatus());
        endpoint.setUpdateTime(LocalDateTime.now());
        aiProviderEndpointMapper.update(endpoint);
    }

    /**
     * 根据厂商ID获取所有启用的端点
     *
     * @param providerId 厂商ID
     * @return 端点列表
     */
    @Override
    public List<AiProviderEndpointVO> getEndpointsByProviderId(String providerId) {
        return aiProviderEndpointMapper.selectByProviderId(providerId)
                .stream().map(this::convertToVO).toList();
    }

    /**
     * 根据厂商ID和端点类型获取端点
     *
     * @param providerId   厂商ID
     * @param endpointType 端点类型
     * @return 端点详情VO
     */
    @Override
    public AiProviderEndpointVO getEndpointByProviderIdAndType(String providerId, String endpointType) {
        AiProviderEndpoint endpoint = aiProviderEndpointMapper.selectOneByProviderIdAndType(providerId, endpointType);
        if (endpoint == null) {
            return null;
        }
        return convertToVO(endpoint);
    }

    /**
     * 端点实体转VO
     *
     * @param endpoint 端点实体
     * @return 端点VO
     */
    private AiProviderEndpointVO convertToVO(AiProviderEndpoint endpoint) {
        AiProviderEndpointVO vo = new AiProviderEndpointVO();
        vo.setId(endpoint.getId());
        vo.setProviderId(endpoint.getProviderId());
        vo.setEndpointType(endpoint.getEndpointType());
        vo.setEndpointTypeName(AiEndpointTypeEnum.getNameByCode(endpoint.getEndpointType()));
        vo.setEndpointUri(endpoint.getEndpointUri());
        vo.setRequestType(endpoint.getRequestType());
        vo.setTimeoutMs(endpoint.getTimeoutMs());
        vo.setRetryTimes(endpoint.getRetryTimes());
        vo.setIsEnabled(endpoint.getIsEnabled());
        vo.setDescription(endpoint.getDescription());
        vo.setConfigJson(endpoint.getConfigJson());
        vo.setCreateTime(endpoint.getCreateTime());
        vo.setUpdateTime(endpoint.getUpdateTime());

        // 填充厂商名称
        if (endpoint.getProviderId() != null) {
            AiProvider provider = aiProviderMapper.selectOneById(endpoint.getProviderId());
            if (provider != null) {
                vo.setProviderName(provider.getProviderName());
            }
        }

        return vo;
    }
}