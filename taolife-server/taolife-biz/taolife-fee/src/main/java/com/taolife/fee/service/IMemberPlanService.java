package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.MemberPlanPageParam;
import com.taolife.fee.param.MemberPlanSaveParam;
import com.taolife.fee.vo.MemberPlanVO;

import java.util.List;

/**
 * 会员套餐服务接口
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IMemberPlanService {

    /**
     * 分页获取会员套餐列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<MemberPlanVO> getMemberPlanPage(MemberPlanPageParam param);

    /**
     * 获取会员套餐详情
     *
     * @param id 套餐ID
     * @return 套餐详情
     */
    MemberPlanVO getMemberPlan(String id);

    /**
     * 获取启用状态的套餐列表
     *
     * @return 套餐列表
     */
    List<MemberPlanVO> getEnabledPlans();

    /**
     * 创建会员套餐
     *
     * @param param 保存参数
     * @return 套餐ID
     */
    String createMemberPlan(MemberPlanSaveParam param);

    /**
     * 修改会员套餐
     *
     * @param id 套餐ID
     * @param param 保存参数
     */
    void modifyMemberPlan(String id, MemberPlanSaveParam param);

    /**
     * 删除会员套餐
     *
     * @param id 套餐ID
     */
    void removeMemberPlan(String id);
}
