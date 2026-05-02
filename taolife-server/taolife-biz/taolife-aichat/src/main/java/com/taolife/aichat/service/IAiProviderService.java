package com.taolife.aichat.service;

import com.taolife.aichat.param.AiProviderPageParam;
import com.taolife.aichat.param.AiProviderSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.vo.AiProviderVO;
import com.taolife.common.utils.PageResult;

import java.util.List;

/**
 * AI模型厂商服务接口
 *
 * @author 文二
 * @date 2026-04-14
 */
public interface IAiProviderService {

    /**
     * 分页查询厂商列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<AiProviderVO> getProviderPage(AiProviderPageParam param);

    /**
     * 获取厂商详情
     *
     * @param id 厂商ID
     * @return 厂商详情
     */
    AiProviderVO getProviderDetail(String id);

    /**
     * 创建厂商
     *
     * @param param 创建参数
     */
    void createProvider(AiProviderSaveParam param);

    /**
     * 修改厂商信息
     *
     * @param param 修改参数
     */
    void modifyProviderInfo(AiProviderSaveParam param);

    /**
     * 删除厂商
     *
     * @param param 删除参数
     */
    void removeProvider(AiRemoveParam param);

    /**
     * 修改厂商状态
     *
     * @param param 状态修改参数
     */
    void modifyProviderStatus(AiStatusParam param);

    /**
     * 根据类型获取可用的厂商列表
     *
     * @param providerType 厂商类型：chat/embedding
     * @return 厂商列表
     */
    List<AiProviderVO> getEnabledProvidersByType(String providerType);

    /**
     * 获取解密后的API Key
     *
     * @param id 厂商ID
     * @return 解密后的API Key
     */
    String getDecryptedApiKey(String id);
}
