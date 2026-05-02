package com.taolife.aichat.service;

import com.taolife.aichat.param.AiProviderEndpointPageParam;
import com.taolife.aichat.param.AiProviderEndpointSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.vo.AiProviderEndpointVO;
import com.taolife.common.utils.PageResult;

import java.util.List;

/**
 * AI厂商端点配置服务接口
 *
 * @author 文二
 * @date 2026-04-14
 */
public interface IAiProviderEndpointService {

    /**
     * 分页查询端点配置列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<AiProviderEndpointVO> getEndpointPage(AiProviderEndpointPageParam param);

    /**
     * 获取端点配置详情
     *
     * @param id 端点配置ID
     * @return 端点配置详情
     */
    AiProviderEndpointVO getEndpointDetail(String id);

    /**
     * 创建端点配置
     *
     * @param param 创建参数
     */
    void createEndpoint(AiProviderEndpointSaveParam param);

    /**
     * 修改端点配置信息
     *
     * @param param 修改参数
     */
    void modifyEndpointInfo(AiProviderEndpointSaveParam param);

    /**
     * 删除端点配置
     *
     * @param param 删除参数
     */
    void removeEndpoint(AiRemoveParam param);

    /**
     * 修改端点配置状态
     *
     * @param param 状态修改参数
     */
    void modifyEndpointStatus(AiStatusParam param);

    /**
     * 根据厂商ID获取所有启用的端点
     *
     * @param providerId 厂商ID
     * @return 端点列表
     */
    List<AiProviderEndpointVO> getEndpointsByProviderId(String providerId);

    /**
     * 根据厂商ID和端点类型获取端点
     *
     * @param providerId   厂商ID
     * @param endpointType 端点类型
     * @return 端点
     */
    AiProviderEndpointVO getEndpointByProviderIdAndType(String providerId, String endpointType);
}