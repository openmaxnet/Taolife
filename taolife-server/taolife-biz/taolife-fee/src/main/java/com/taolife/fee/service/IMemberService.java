package com.taolife.fee.service;

import com.taolife.fee.vo.MemberStatusVO;

/**
 * 会员服务接口
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IMemberService {

    /**
     * 获取当前用户会员状态
     *
     * @param accountId 账号ID
     * @return 会员状态
     */
    MemberStatusVO getMemberStatus(String accountId);

    /**
     * 检查用户是否为活跃会员
     *
     * @param accountId 账号ID
     * @return true-是会员
     */
    boolean isActiveMember(String accountId);

    /**
     * 获取会员等级
     *
     * @param accountId 账号ID
     * @return 会员等级，0表示非会员
     */
    int getMemberLevel(String accountId);
}
