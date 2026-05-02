package com.taolife.fee.service;

import com.taolife.fee.vo.AdConfigVO;

import java.util.List;

/**
 * 广告配置服务接口
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IAdConfigService {

    /**
     * 获取当前用户可见的广告配置
     */
    List<AdConfigVO> getVisibleAdConfigs(String accountId);

    /**
     * 上报广告行为
     */
    void reportAdAction(String accountId, String adConfigKey, Integer action, Integer duration);
}
