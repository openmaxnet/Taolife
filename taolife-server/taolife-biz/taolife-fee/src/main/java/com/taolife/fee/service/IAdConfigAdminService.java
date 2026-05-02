package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.AdConfigPageParam;
import com.taolife.fee.param.AdConfigSaveParam;
import com.taolife.fee.vo.AdConfigAdminVO;

/**
 * 广告配置管理端服务
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IAdConfigAdminService {

    /**
     * 分页获取广告配置列表
     * 根据条件分页获取广告配置信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<AdConfigAdminVO> getAdConfigPage(AdConfigPageParam param);

    /**
     * 获取广告配置详情
     * 根据ID获取广告配置详细信息
     *
     * @param id 广告配置ID
     * @return 广告配置详情
     */
    AdConfigAdminVO getAdConfig(String id);

    /**
     * 创建广告配置
     * 新增一条广告配置记录
     *
     * @param param 广告配置参数
     */
    void createAdConfig(AdConfigSaveParam param);

    /**
     * 修改广告配置
     * 根据ID修改广告配置信息
     *
     * @param id 广告配置ID
     * @param param 广告配置参数
     */
    void modifyAdConfig(String id, AdConfigSaveParam param);

    /**
     * 修改广告配置状态
     * 启用或禁用广告配置
     *
     * @param id 广告配置ID
     * @param isEnabled 是否启用（0-禁用，1-启用）
     */
    void modifyAdConfigStatus(String id, Integer isEnabled);

    /**
     * 删除广告配置
     * 根据ID删除广告配置（逻辑删除）
     *
     * @param id 广告配置ID
     */
    void removeAdConfig(String id);
}
