package com.taolife.wisdom.service;

import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.param.MeridianQueryParam;
import com.taolife.wisdom.param.MeridianSaveParam;
import com.taolife.wisdom.vo.MeridianDetailVO;
import com.taolife.wisdom.vo.MeridianListVO;

/**
 * 经络服务接口
 * 定义经络相关的业务操作
 *
 * @author 文二
 * @date 2026-03-24
 */
public interface IMeridianService {

    /**
     * 获取经络列表
     * 根据条件分页获取经络信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<MeridianListVO> getMeridianPage(MeridianQueryParam param);

    /**
     * 获取经络详情
     * 根据经络ID获取经络的详细信息
     *
     * @param id 经络ID
     * @return 经络详细信息
     */
    MeridianDetailVO getMeridianDetail(String id);

    /**
     * 创建经络
     *
     * @param param 经络保存参数
     */
    void createMeridian(MeridianSaveParam param);

    /**
     * 修改经络信息
     *
     * @param id    经络ID
     * @param param 经络保存参数
     */
    void modifyMeridianInfo(String id, MeridianSaveParam param);

    /**
     * 删除经络
     *
     * @param id 经络ID
     */
    void removeMeridian(String id);

    /**
     * 修改经络状态
     *
     * @param id         经络ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    void modifyMeridianStatus(String id, Integer isDisabled);
}
