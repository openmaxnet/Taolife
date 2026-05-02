package com.taolife.identity.controller;

import com.taolife.identity.param.AccountCancelParam;
import com.taolife.identity.param.UserInfoParam;
import com.taolife.identity.service.IAccountService;
import com.taolife.identity.service.IWxLoginService;
import com.taolife.identity.vo.AccountCancelCheckVO;
import com.taolife.identity.vo.UserInfoVO;
import com.taolife.identity.vo.WxLoginVO;
import com.taolife.common.annotation.AuthSkip;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.identity.param.WxLoginParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 账号控制器
 * 处理用户登录、获取用户信息等账号相关请求
 *
 * @author 文二
 * @date 2026-03-16
 */
@RestController
@RequestMapping("/api/identity/account")
@RequiredArgsConstructor
public class AccountController {

    private final IWxLoginService wxLoginService;
    private final IAccountService accountService;

    /**
     * 微信小程序登录
     * 通过code获取openid，创建或更新用户信息，返回JWT Token
     *
     * @param param 登录参数
     * @return 登录结果
     */
    @PostMapping("/wxLogin")
    @AuthSkip(reason = "微信登录接口无需认证", loginEndpoint = true)
    public ExceptionResult<WxLoginVO> wxLogin(@Valid @RequestBody WxLoginParam param) {
        WxLoginVO result = wxLoginService.wxLogin(param);
        return ExceptionResult.success(result);
    }

    /**
     * 刷新Token
     * 根据当前Token刷新一个新的Token
     *
     * @param token 当前Token
     * @return 新的Token
     */
    @PostMapping("/refreshToken")
    @AuthSkip(reason = "刷新Token接口")
    public ExceptionResult<WxLoginVO> refreshToken(@RequestBody java.util.Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        WxLoginVO result = wxLoginService.refreshToken(refreshToken);
        return ExceptionResult.success(result);
    }

    /**
     * 获取用户信息
     * 获取当前登录用户的详细信息
     *
     * @return 用户信息
     */
    @GetMapping("/getUserInfo")
    public ExceptionResult<UserInfoVO> getUserInfo() {
        // 从用户上下文获取当前用户ID
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }

        UserInfoVO userInfo = accountService.getUserInfo(accountId);
        return ExceptionResult.success(userInfo);
    }

    /**
     * 修改用户信息
     * 修改当前登录用户的基本信息
     *
     * @param param 用户信息修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyUserInfo")
    public ExceptionResult<Void> modifyUserInfo(@Valid @RequestBody UserInfoParam param) {
        // 从用户上下文获取当前用户ID
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }

        accountService.modifyUserInfo(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 获取注销状态
     * 查询当前用户账号的注销申请状态
     *
     * @return 注销状态信息
     */
    @GetMapping("/getCancelStatus")
    public ExceptionResult<AccountCancelCheckVO> getCancelStatus() {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        return ExceptionResult.success(accountService.getCancelStatus(accountId));
    }

    /**
     * 申请注销账号
     * 提交账号注销申请，可选择填写注销原因
     *
     * @param param 注销参数（可选，包含注销原因）
     * @return 操作结果
     */
    @PostMapping("/requestCancel")
    public ExceptionResult<Void> requestCancel(@RequestBody(required = false) AccountCancelParam param) {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        accountService.requestCancel(accountId, param);
        return ExceptionResult.success();
    }

    /**
     * 撤销注销申请
     * 取消当前的账号注销申请
     *
     * @return 操作结果
     */
    @PostMapping("/revokeCancel")
    public ExceptionResult<Void> revokeCancel() {
        String accountId = UserContext.getAccountId();
        if (accountId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }
        accountService.revokeCancel(accountId);
        return ExceptionResult.success();
    }
}
