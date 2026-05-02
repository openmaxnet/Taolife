package com.taolife.identity.service;

import com.taolife.identity.param.AccountCancelParam;
import com.taolife.identity.param.UserInfoParam;
import com.taolife.identity.vo.AccountCancelCheckVO;
import com.taolife.identity.vo.UserInfoVO;

/**
 * 账号服务接口
 *
 * @author 文二
 * @date 2026-03-16
 */
public interface IAccountService {

    /**
     * 获取用户信息
     * 根据账号ID查询用户基本信息及互动统计
     *
     * @param accountId 账号ID
     * @return 用户信息VO
     */
    UserInfoVO getUserInfo(String accountId);

    /**
     * 修改用户信息
     * 修改用户的昵称、头像、性别、生日等基本信息
     *
     * @param accountId 账号ID
     * @param param     用户信息修改参数
     */
    void modifyUserInfo(String accountId, UserInfoParam param);

    /**
     * 更新用户头像
     *
     * @param accountId  账号ID
     * @param avatarUrl 新头像URL
     */
    void updateAvatar(String accountId, String avatarUrl);

    /**
     * 更新会员信息
     * 更新用户的会员等级和过期时间
     *
     * @param accountId   账号ID
     * @param memberLevel 会员等级
     * @param expireTime  过期时间
     */
    void updateMemberInfo(String accountId, Integer memberLevel, java.time.LocalDateTime expireTime);

    /**
     * 获取注销状态
     * 查询账号的注销申请状态，包含前置检查结果
     *
     * @param accountId 账号ID
     * @return 注销状态VO（含是否可注销及阻止原因）
     */
    AccountCancelCheckVO getCancelStatus(String accountId);

    /**
     * 提交注销申请
     * 提交账号注销申请，设置7天静默期
     *
     * @param accountId 账号ID
     * @param param     注销参数（可选，包含注销原因）
     */
    void requestCancel(String accountId, AccountCancelParam param);

    /**
     * 撤回注销申请
     * 取消当前的账号注销申请（仅在静默期内可撤回）
     *
     * @param accountId 账号ID
     */
    void revokeCancel(String accountId);
}
