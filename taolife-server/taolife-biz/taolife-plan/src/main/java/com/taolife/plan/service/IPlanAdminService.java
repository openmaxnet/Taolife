package com.taolife.plan.service;

import com.taolife.common.utils.PageResult;
import com.taolife.plan.vo.UserPlanDetailAdminVO;
import com.taolife.plan.vo.UserPlanAdminVO;

/**
 * 用户方案管理服务接口（管理后台）
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IPlanAdminService {

    /**
     * 分页查询用户方案
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param keyword  关键词（可选）
     * @param status   状态（可选）
     * @return 分页结果
     */
    PageResult<UserPlanAdminVO> getUserPlanPage(Integer pageNo, Integer pageSize, String keyword, Integer status);

    /**
     * 获取用户方案详情
     * 查询方案基本信息并聚合5个子方案内容
     *
     * @param id 方案ID
     * @return 方案详细信息
     */
    UserPlanDetailAdminVO getUserPlanDetail(String id);

    /**
     * 修改方案状态
     *
     * @param id     方案ID
     * @param status 状态值
     */
    void modifyUserPlanStatus(String id, Integer status);
}
