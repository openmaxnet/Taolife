package com.taolife.wisdom.service;

import com.taolife.wisdom.param.*;
import com.taolife.wisdom.vo.FitnessTypeAdminVO;
import com.taolife.common.utils.PageResult;

/**
 * 管理员体质类型服务接口
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IFitnessAdminService {

    /**
     * 分页查询体质类型列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<FitnessTypeAdminVO> getConstitutionTypePage(FitnessTypePageAdminParam param);

    /**
     * 获取体质类型详情
     *
     * @param param 详情查询参数
     * @return 体质类型详情
     */
    FitnessTypeAdminVO getConstitutionTypeDetail(FitnessTypeDetailAdminParam param);

    /**
     * 创建体质类型
     *
     * @param param 创建参数
     */
    void createConstitutionType(FitnessTypeSaveAdminParam param);

    /**
     * 修改体质类型
     *
     * @param param 修改参数
     */
    void modifyConstitutionTypeInfo(FitnessTypeSaveAdminParam param);

    /**
     * 删除体质类型
     *
     * @param param 删除参数
     */
    void removeConstitutionType(RemoveFitnessTypeAdminParam param);

    /**
     * 修改体质类型状态
     *
     * @param param 状态修改参数
     */
    void modifyConstitutionTypeStatus(ModifyFitnessTypeStatusAdminParam param);
}
