package com.taolife.identity.service.impl;

import com.taolife.identity.entity.Account;
import com.taolife.identity.mapper.AccountMapper;
import com.taolife.identity.mapper.UserFollowMapper;
import com.taolife.identity.mapper.UserInteractionMapper;
import com.taolife.identity.param.AccountCancelParam;
import com.taolife.identity.param.UserInfoParam;
import com.taolife.identity.service.IAccountService;
import com.taolife.identity.vo.AccountCancelCheckVO;
import com.taolife.identity.vo.UserInfoVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 账号服务实现类
 *
 * @author 文二
 * @date 2026-03-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {

    private final AccountMapper accountMapper;
    private final UserInteractionMapper interactionMapper;
    private final UserFollowMapper followMapper;

    /**
     * 获取用户信息
     * 根据账号ID查询完整的用户信息
     *
     * @param accountId 账号ID
     * @return 用户信息
     */
    @Override
    public UserInfoVO getUserInfo(String accountId) {
        // 根据ID查询账号
        Account account = accountMapper.selectById(accountId);

        if (account == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND, "用户不存在");
        }

        // 检查是否被禁用
        if (account.getIsDisabled() != null && account.getIsDisabled() == 1) {
            throw new BusinessException(ExceptionCode.USER_DISABLED, "账号已被禁用");
        }

        // 转换为VO
        UserInfoVO vo = convertToVO(account);

        // 互动统计
        vo.setLikeCount(interactionMapper.countByAccountAndType(accountId, 1));
        vo.setCollectCount(interactionMapper.countByAccountAndType(accountId, 2));
        vo.setFollowingCount(followMapper.countFollowing(accountId));

        return vo;
    }

    /**
     * 修改用户信息
     * 根据账号ID修改用户基本信息
     *
     * @param accountId 账号ID
     * @param param     用户信息修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserInfo(String accountId, UserInfoParam param) {
        // 查询账号是否存在
        Account account = accountMapper.selectById(accountId);

        if (account == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND, "用户不存在");
        }

        // 调用Mapper更新用户信息
        int rows = accountMapper.updateUserInfo(
            accountId,
            param.getNickname(),
            param.getAvatarUrl(),
            param.getGender(),
            param.getBirthday()
        );

        if (rows <= 0) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "更新用户信息失败");
        }

        log.info("修改用户信息成功：accountId={}", accountId);
    }

    /**
     * 更新用户头像
     *
     * @param accountId  账号ID
     * @param avatarUrl 头像URL
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(String accountId, String avatarUrl) {
        int rows = accountMapper.updateAvatar(accountId, avatarUrl);

        if (rows <= 0) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "更新头像失败");
        }

        log.info("更新用户头像成功：accountId={}, avatarUrl={}", accountId, avatarUrl);
    }

    /**
     * 更新会员信息
     *
     * @param accountId    账号ID
     * @param memberLevel  会员等级
     * @param expireTime   过期时间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberInfo(String accountId, Integer memberLevel, LocalDateTime expireTime) {
        int rows = accountMapper.updateMemberInfo(accountId, memberLevel, expireTime);

        if (rows <= 0) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "更新会员信息失败");
        }

        log.info("更新会员信息成功：accountId={}, memberLevel={}, expireTime={}", accountId, memberLevel, expireTime);
    }

    /**
     * 获取注销状态
     * 查询当前账号的注销申请状态及前置检查结果
     *
     * @param accountId 账号ID
     * @return 注销状态信息（含是否可以注销及阻止原因）
     */
    @Override
    public AccountCancelCheckVO getCancelStatus(String accountId) {
        Account account = getAccountOrThrow(accountId);
        AccountCancelCheckVO vo = new AccountCancelCheckVO();
        vo.setCancellationStatus(account.getCancellationStatus() != null ? account.getCancellationStatus() : 0);
        vo.setCancellationTime(account.getCancellationTime());
        vo.setCancellationSchedule(account.getCancellationSchedule());

        // 前置检查
        String blockReason = checkCancelBlockers(account);
        vo.setCanCancel(blockReason == null);
        vo.setBlockReason(blockReason);
        return vo;
    }

    /**
     * 申请注销账号
     * 提交账号注销申请，设置静默期（7天后自动注销）
     *
     * @param accountId 账号ID
     * @param param     注销参数（可选，包含注销原因）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requestCancel(String accountId, AccountCancelParam param) {
        Account account = getAccountOrThrow(accountId);

        // 已有注销申请
        if (account.getCancellationStatus() != null && account.getCancellationStatus() == 1) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "已有注销申请，请勿重复提交");
        }

        // 前置检查
        String blockReason = checkCancelBlockers(account);
        if (blockReason != null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, blockReason);
        }

        LocalDateTime now = LocalDateTime.now();
        account.setCancellationStatus(1);
        account.setCancellationTime(now);
        account.setCancellationSchedule(now.plusDays(7));
        account.setCancellationReason(param != null ? param.getReason() : null);
        account.setUpdateTime(now);
        accountMapper.update(account);
        log.info("账号注销申请成功：accountId={}, schedule={}", accountId, account.getCancellationSchedule());
    }

    /**
     * 撤销注销申请
     * 取消当前的账号注销申请（仅在静默期内可撤销）
     *
     * @param accountId 账号ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeCancel(String accountId) {
        Account account = getAccountOrThrow(accountId);

        if (account.getCancellationStatus() == null || account.getCancellationStatus() != 1) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "当前无注销申请");
        }

        if (account.getCancellationSchedule() != null && LocalDateTime.now().isAfter(account.getCancellationSchedule())) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "静默期已过，无法撤回");
        }

        account.setCancellationStatus(0);
        account.setCancellationTime(null);
        account.setCancellationSchedule(null);
        account.setCancellationReason(null);
        account.setUpdateTime(LocalDateTime.now());
        accountMapper.update(account);
        log.info("账号注销撤回成功：accountId={}", accountId);
    }

    /**
     * 注销前置检查，返回不通过原因，通过返回 null
     */
    private String checkCancelBlockers(Account account) {
        // 有效会员
        if (account.getMemberLevel() != null && account.getMemberLevel() > 0
                && account.getMemberExpireTime() != null && account.getMemberExpireTime().isAfter(LocalDateTime.now())) {
            return "您有有效的会员权益，请先等待会员到期或联系客服";
        }
        // 已有注销申请
        if (account.getCancellationStatus() != null && account.getCancellationStatus() == 1) {
            return "已有注销申请进行中";
        }
        return null;
    }

    private Account getAccountOrThrow(String accountId) {
        Account account = accountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND, "用户不存在");
        }
        return account;
    }

    /**
     * 转换为VO对象
     *
     * @param account 账号实体
     * @return 用户信息VO
     */
    private UserInfoVO convertToVO(Account account) {
        UserInfoVO vo = new UserInfoVO();
        vo.setAccountId(account.getId());
        vo.setAccountType(account.getAccountType());
        vo.setPhone(account.getPhone());
        vo.setNickname(account.getNickname());
        vo.setAvatarUrl(account.getAvatarUrl());
        vo.setGender(account.getGender());
        vo.setBirthday(account.getBirthday());
        vo.setMemberLevel(account.getMemberLevel());
        vo.setMemberExpireTime(account.getMemberExpireTime());
        vo.setIsDisabled(account.getIsDisabled());
        vo.setCreateTime(account.getCreateTime());
        return vo;
    }
}
