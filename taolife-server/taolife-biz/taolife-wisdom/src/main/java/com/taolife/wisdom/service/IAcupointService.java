package com.taolife.wisdom.service;

import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.param.AcupointQueryParam;
import com.taolife.wisdom.param.AcupointSaveParam;
import com.taolife.wisdom.vo.AcupointDetailVO;
import com.taolife.wisdom.vo.AcupointListVO;

/**
 * 穴位服务接口
 * 定义穴位相关的业务操作
 *
 * @author 文二
 * @date 2026-03-24
 */
public interface IAcupointService {

    /**
     * 获取穴位列表
     * 根据条件分页获取穴位信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<AcupointListVO> getAcupointPage(AcupointQueryParam param);

    /**
     * 获取穴位详情
     * 根据穴位ID获取穴位的详细信息
     *
     * @param id 穴位ID
     * @return 穴位详细信息
     */
    AcupointDetailVO getAcupointDetail(String id);

    /**
     * 创建穴位
     *
     * @param param 穴位保存参数
     */
    void createAcupoint(AcupointSaveParam param);

    /**
     * 修改穴位信息
     *
     * @param id    穴位ID
     * @param param 穴位保存参数
     */
    void modifyAcupointInfo(String id, AcupointSaveParam param);

    /**
     * 删除穴位
     *
     * @param id 穴位ID
     */
    void removeAcupoint(String id);

    /**
     * 修改穴位状态
     *
     * @param id         穴位ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    void modifyAcupointStatus(String id, Integer isDisabled);
}
