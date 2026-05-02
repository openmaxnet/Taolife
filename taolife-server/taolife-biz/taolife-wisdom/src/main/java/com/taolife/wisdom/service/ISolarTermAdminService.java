package com.taolife.wisdom.service;

import com.taolife.wisdom.param.*;
import com.taolife.wisdom.vo.SolarTermAdminVO;
import com.taolife.common.utils.PageResult;

/**
 * 管理员节气服务接口
 *
 * @author 文二
 * @date 2026-04-20
 */
public interface ISolarTermAdminService {

    /**
     * 分页查询节气列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<SolarTermAdminVO> getSolarTermPage(SolarTermPageAdminParam param);

    /**
     * 获取节气详情
     *
     * @param param 详情查询参数
     * @return 节气详情
     */
    SolarTermAdminVO getSolarTermDetail(SolarTermDetailAdminParam param);

    /**
     * 创建节气
     *
     * @param param 创建参数
     */
    void createSolarTerm(SolarTermSaveAdminParam param);

    /**
     * 修改节气
     *
     * @param param 修改参数
     */
    void modifySolarTermInfo(SolarTermSaveAdminParam param);

    /**
     * 删除节气
     *
     * @param param 删除参数
     */
    void removeSolarTerm(RemoveSolarTermAdminParam param);
}
