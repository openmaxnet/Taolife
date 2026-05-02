package com.taolife.identity.service.impl;


import com.taolife.identity.entity.Account;
import com.taolife.identity.entity.User;
import com.taolife.identity.mapper.AccountMapper;
import com.taolife.identity.mapper.UserMapper;
import com.taolife.identity.param.WxLoginParam;
import com.taolife.identity.service.IWxLoginService;
import com.taolife.identity.vo.WxLoginVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.security.JwtManager;
import com.taolife.common.utils.WxMiniAppUtil;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * 微信小程序登录服务实现类
 *
 * @author 文二
 * @date 2026-03-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxLoginServiceImpl implements IWxLoginService {

    private final WxMiniAppUtil wxMiniProgramApi;
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;
    private final JwtManager jwtManager;
    private final MeterRegistry meterRegistry;

    /**
     * 账号类型-微信
     */
    private static final Integer ACCOUNT_TYPE_WX = 1;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxLoginVO wxLogin(WxLoginParam param) {
        // 1. 调用微信code2Session获取openid
        Map<String, String> wxResult = wxMiniProgramApi.code2Session(param.getCode());
        if (wxResult == null) {
            throw new BusinessException(ExceptionCode.LOGIN_FAILED, "微信登录失败，请重试");
        }

        String openid = wxResult.get("openid");
        String sessionKey = wxResult.get("session_key");
        String unionid = wxResult.get("unionid");

        log.info("微信code2Session成功：openid={}", openid);

        // 2. 调用微信接口获取手机号（暂时注释）
        // String accessToken = wxMiniProgramApi.getAccessToken();
        // if (accessToken == null) {
        //     throw new BusinessException(ExceptionCode.LOGIN_FAILED, "获取微信access_token失败，请重试");
        // }
        //
        // String phoneNumber = wxMiniProgramApi.getPhoneNumber(accessToken, param.getPhoneCode());
        // if (phoneNumber == null) {
        //     throw new BusinessException(ExceptionCode.LOGIN_FAILED, "获取手机号失败，请重试");
        // }
        //
        // log.info("获取手机号成功：phoneNumber={}", phoneNumber);

        // 暂时不获取手机号，存NULL避免唯一索引冲突
        String phoneNumber = null;

        // 3. 查询账号是否存在
        Account account = getAccountByOpenid(openid);

        boolean isNewUser = (account == null);

        // 4. 如果是新用户，创建账号
        if (account == null) {
            account = createNewAccount(openid, unionid, phoneNumber, param, wxResult);
        } else {
            // 5. 如果是老用户，暂时不检查手机号
            // 如果手机号不一致，更新手机号（以openid为准，支持一个openid对应多个手机号）
            // if (!phoneNumber.equals(account.getPhone())) {
            //     updateAccountPhone(account, phoneNumber);
            //     log.info("更新账号手机号：accountId={}, oldPhone={}, newPhone={}",
            //              account.getId(), account.getPhone(), phoneNumber);
            // }
        }

        // 6. 如果有加密数据，解密并更新用户信息
        if (param.getEncryptedData() != null && param.getIv() != null) {
            updateUserInfoFromEncryptedData(account, sessionKey, param.getEncryptedData(), param.getIv());
        }

        // 7. 生成双Token
        List<String> roles = List.of("USER");
        String accessToken = jwtManager.generateAccessToken(account.getId(), roles);
        String refreshToken = jwtManager.generateRefreshToken(account.getId(), roles);

        // 8. 构建返回结果
        meterRegistry.counter("biz.user.login", "source", "wx_miniprogram", "status", "success").increment();
        return buildLoginResult(account, accessToken, refreshToken, isNewUser);
    }

    /**
     * 刷新Token
     * 验证refreshToken并签发新的双Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的登录结果
     */
    @Override
    public WxLoginVO refreshToken(String refreshToken) {
        // 验证refreshToken
        if (!jwtManager.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ExceptionCode.REFRESH_TOKEN_INVALID, "刷新令牌无效或已过期");
        }

        String accountId = jwtManager.getUserIdFromToken(refreshToken);
        if (accountId == null) {
            throw new BusinessException(ExceptionCode.REFRESH_TOKEN_INVALID, "无法获取用户信息");
        }

        List<String> roles = jwtManager.getRolesFromToken(refreshToken);
        if (roles == null || roles.isEmpty()) {
            roles = List.of("USER");
        }

        // 签发新的双Token
        String newAccessToken = jwtManager.generateAccessToken(accountId, roles);
        String newRefreshToken = jwtManager.generateRefreshToken(accountId, roles);

        log.info("刷新Token成功：accountId={}", accountId);
        return buildLoginResult(null, newAccessToken, newRefreshToken, false);
    }

    /**
     * 根据openid查询账号
     *
     * @param openid 微信openid
     * @return 账号对象
     */
    private Account getAccountByOpenid(String openid) {
        return accountMapper.selectByOpenid(openid, ACCOUNT_TYPE_WX);
    }

    /**
     * 创建新账号
     *
     * @param openid     微信openid
     * @param unionid    微信unionid
     * @param phoneNumber 手机号
     * @param param      登录参数
     * @param wxResult   微信返回结果
     * @return 账号对象
     */
    private Account createNewAccount(String openid, String unionid, String phoneNumber,
                                     WxLoginParam param, Map<String, String> wxResult) {
        Account account = new Account();
        account.setAccountType(ACCOUNT_TYPE_WX);
        account.setOpenid(openid);
        account.setUnionid(unionid);
        account.setPhone(phoneNumber);
        account.setNickname("微信用户" + openid.substring(0, 8));
        account.setMemberLevel(0);
        account.setIsDeleted(0);
        account.setIsDisabled(0);
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());

        accountMapper.insert(account);
        meterRegistry.counter("biz.user.register", "source", "wx_miniprogram").increment();
        log.info("创建新用户成功：accountId={}, openid={}, phone={}", account.getId(), openid, phoneNumber);

        // 创建User记录
        createUserRecord(account.getId());

        return account;
    }

    /**
     * 更新账号手机号
     *
     * @param account    账号对象
     * @param phoneNumber 手机号
     */
    // 暂时注释，后续完善
    // private void updateAccountPhone(Account account, String phoneNumber) {
    //     accountMapper.updatePhoneById(account.getId(), phoneNumber);
    // }

    /**
     * 创建User记录
     *
     * @param accountId 账号ID
     */
    private void createUserRecord(String accountId) {
        User user = new User();
        user.setAccountId(accountId);
        user.setIsDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        log.info("创建User记录成功：userId={}, accountId={}", user.getId(), accountId);
    }

    /**
     * 从加密数据更新用户信息
     *
     * @param account        账号对象
     * @param sessionKey    会话密钥
     * @param encryptedData 加密数据
     * @param iv            初始向量
     */
    private void updateUserInfoFromEncryptedData(Account account, String sessionKey,
                                                  String encryptedData, String iv) {
        try {
            String userData = wxMiniProgramApi.decryptUserData(sessionKey, encryptedData, iv);
            if (userData == null) {
                log.warn("解密用户数据失败");
                return;
            }

            // 解析并更新用户信息
            // 这里需要根据实际加密数据结构进行解析
            log.debug("解密用户数据成功：{}", userData);
        } catch (Exception e) {
            log.error("更新用户信息失败：{} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 构建登录结果
     *
     * @param account  账号对象
     * @param token    JWT Token
     * @param isNewUser 是否新用户
     * @return 登录结果
     */
    private WxLoginVO buildLoginResult(Account account, String accessToken, String refreshToken, boolean isNewUser) {
        WxLoginVO vo = new WxLoginVO();
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setIsNewUser(isNewUser);

        if (account != null) {
            WxLoginVO.UserBasicInfoVO userInfo = new WxLoginVO.UserBasicInfoVO();
            userInfo.setAccountId(account.getId());
            userInfo.setNickname(account.getNickname());
            userInfo.setAvatarUrl(account.getAvatarUrl());
            userInfo.setGender(account.getGender());
            userInfo.setMemberLevel(account.getMemberLevel());
            vo.setUserInfo(userInfo);
        }

        return vo;
    }
}
