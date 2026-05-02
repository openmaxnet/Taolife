package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.GrowthLevelPageParam;
import com.taolife.fee.param.GrowthLevelSaveParam;
import com.taolife.fee.vo.GrowthLevelAdminVO;

/**
 * 成长等级管理服务接口
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IGrowthLevelAdminService {

    /**
     * 分页查询成长等级
     *
     * @param param 分页查询参数
     * @return 成长等级分页结果
     */
    PageResult<GrowthLevelAdminVO> getGrowthLevelPage(GrowthLevelPageParam param);

    /**
     * 查询成长等级详情
     *
     * @param id 等级ID
     * @return 成长等级详情
     */
    GrowthLevelAdminVO getGrowthLevelDetail(String id);

    /**
     * 创建成长等级
     *
     * @param param 保存参数
     */
    void createGrowthLevel(GrowthLevelSaveParam param);

    /**
     * 修改成长等级信息
     *
     * @param id    等级ID
     * @param param 保存参数
     */
    void modifyGrowthLevelInfo(String id, GrowthLevelSaveParam param);

    /**
     * 修改成长等级状态
     *
     * @param id        等级ID
     * @param isEnabled 是否启用：0-否，1-是
     */
    void modifyGrowthLevelStatus(String id, Integer isEnabled);

    /**
     * 删除成长等级
     *
     * @param id 等级ID
     */
    void removeGrowthLevel(String id);
}
