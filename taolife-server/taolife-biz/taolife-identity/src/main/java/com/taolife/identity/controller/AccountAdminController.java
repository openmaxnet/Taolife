package com.taolife.identity.controller;

import com.taolife.identity.param.AccountPageAdminParam;
import com.taolife.identity.param.ModifyAccountStatusAdminParam;
import com.taolife.identity.param.ModifyMemberLevelAdminParam;
import com.taolife.identity.vo.AccountDetailAdminVO;
import com.taolife.identity.vo.AccountAdminVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.identity.service.IAccountAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理后台小程序账号控制器
 *
 * @author 文二
 * @date 2026-04-13
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/identity/admin/account")
@RequiredArgsConstructor
public class AccountAdminController {

    private final IAccountAdminService adminAccountService;

    /**
     * 分页查询账号列表
     */
    @GetMapping("/getAccountPage")
    public ExceptionResult<PageResult<AccountAdminVO>> getAccountPage(AccountPageAdminParam param) {
        log.info("分页查询账号列表，参数：{}", param);
        return ExceptionResult.success(adminAccountService.getAccountPage(param));
    }

    /**
     * 获取账号详情
     */
    @GetMapping("/getAccountDetail")
    public ExceptionResult<AccountDetailAdminVO> getAccountDetail(@RequestParam String id) {
        log.info("获取账号详情，id：{}", id);
        return ExceptionResult.success(adminAccountService.getAccountDetail(id));
    }

    /**
     * 修改账号状态
     */
    @PostMapping("/modifyAccountStatus")
    public ExceptionResult<Void> modifyAccountStatus(@RequestBody ModifyAccountStatusAdminParam param) {
        log.info("修改账号状态，id：{}，isDisabled：{}", param.getId(), param.getIsDisabled());
        adminAccountService.modifyAccountStatus(param);
        return ExceptionResult.success();
    }

    /**
     * 修改会员等级
     */
    @PostMapping("/modifyMemberLevel")
    public ExceptionResult<Void> modifyMemberLevel(@RequestBody ModifyMemberLevelAdminParam param) {
        log.info("修改会员等级，id：{}，memberLevel：{}", param.getId(), param.getMemberLevel());
        adminAccountService.modifyMemberLevel(param);
        return ExceptionResult.success();
    }
}
