package com.taolife.wisdom.service;

import com.taolife.wisdom.vo.SolarTermDetailVO;
import com.taolife.wisdom.vo.SolarTermListVO;

import java.util.List;

/**
 * 节气Service接口
 *
 * @author 文二
 * @date 2026-04-20
 */
public interface ISolarTermService {

    /**
     * 获取节气列表
     *
     * @return 节气列表
     */
    List<SolarTermListVO> getSolarTermList();

    /**
     * 获取节气详情
     *
     * @param id 节气ID
     * @return 节气详情
     */
    SolarTermDetailVO getSolarTermDetail(String id);

    /**
     * 获取当前节气
     *
     * @return 当前节气详情
     */
    SolarTermDetailVO getCurrentSolarTerm();
}
