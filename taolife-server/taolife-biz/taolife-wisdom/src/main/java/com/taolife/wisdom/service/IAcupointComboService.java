package com.taolife.wisdom.service;

import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.entity.AcupointCombo;
import com.taolife.wisdom.param.AcupointComboSaveParam;

/**
 * 穴位配伍服务接口
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IAcupointComboService {

    /**
     * 分页查询穴位配伍列表
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @param category 分类
     * @param keyword  关键词
     * @return 分页结果
     */
    PageResult<AcupointCombo> getAcupointCombinePage(Integer pageNo, Integer pageSize, Integer category, String keyword);

    /**
     * 获取穴位配伍详情
     *
     * @param id 穴位配伍ID
     * @return 穴位配伍详情
     */
    AcupointCombo getAcupointCombineDetail(String id);

    /**
     * 创建穴位配伍
     *
     * @param param 穴位配伍创建参数
     */
    void createAcupointCombine(AcupointComboSaveParam param);

    /**
     * 修改穴位配伍信息
     *
     * @param id    穴位配伍ID
     * @param param 穴位配伍修改参数
     */
    void modifyAcupointCombineInfo(String id, AcupointComboSaveParam param);

    /**
     * 删除穴位配伍（逻辑删除）
     *
     * @param id 穴位配伍ID
     */
    void removeAcupointCombine(String id);

    /**
     * 修改穴位配伍状态
     *
     * @param id         穴位配伍ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    void modifyAcupointCombineStatus(String id, Integer isDisabled);
}
