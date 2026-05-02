package com.taolife.identity.service;

import com.taolife.identity.param.AccountPageAdminParam;
import com.taolife.identity.param.ModifyAccountStatusAdminParam;
import com.taolife.identity.param.ModifyMemberLevelAdminParam;
import com.taolife.identity.vo.AccountDetailAdminVO;
import com.taolife.identity.vo.AccountAdminVO;
import com.taolife.common.utils.PageResult;

/**
 * 管理后台小程序账号服务接口
 *
 * @author 文二
 * @date 2026-04-13
 */
public interface IAccountAdminService {

    /**
     * 分页查询账号列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<AccountAdminVO> getAccountPage(AccountPageAdminParam param);

    /**
     * 获取账号详情
     *
     * @param id 账号ID
     * @return 账号详细信息
     */
    AccountDetailAdminVO getAccountDetail(String id);

    /**
     * 修改账号状态
     * 启用或禁用指定账号
     *
     * @param param 状态修改参数（含账号ID和禁用标记）
     */
    void modifyAccountStatus(ModifyAccountStatusAdminParam param);

    /**
     * 修改会员等级
     * 修改指定账号的会员等级和过期时间
     *
     * @param param 会员等级修改参数（含账号ID、等级、过期时间）
     */
    void modifyMemberLevel(ModifyMemberLevelAdminParam param);
}
