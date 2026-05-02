package com.taolife.identity.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.identity.entity.Account;
import com.taolife.identity.entity.User;
import com.taolife.identity.entity.UserPreference;
import com.taolife.identity.mapper.AccountMapper;
import com.taolife.identity.mapper.UserMapper;
import com.taolife.identity.mapper.UserPreferenceMapper;
import com.taolife.identity.param.AccountPageAdminParam;
import com.taolife.identity.param.ModifyAccountStatusAdminParam;
import com.taolife.identity.param.ModifyMemberLevelAdminParam;
import com.taolife.identity.vo.AccountDetailAdminVO;
import com.taolife.identity.vo.AccountAdminVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.identity.service.IAccountAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理后台小程序账号服务实现
 *
 * @author 文二
 * @date 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountAdminServiceImpl implements IAccountAdminService {

    private final AccountMapper accountMapper;
    private final UserMapper userMapper;
    private final UserPreferenceMapper userPreferenceMapper;

    /**
     * 分页查询账号列表
     * 根据关键词、会员等级、日期范围等条件分页查询
     *
     * @param param 查询参数
     * @return 分页后的账号列表
     */
    @Override
    public PageResult<AccountAdminVO> getAccountPage(AccountPageAdminParam param) {
        int pageNo = param.getPageNo() != null ? param.getPageNo() : 1;
        int pageSize = param.getPageSize() != null ? param.getPageSize() : 10;

        Page<Account> page = accountMapper.selectAdminPage(
                new com.mybatisflex.core.paginate.Page<>(pageNo, pageSize),
                param.getKeyword(),
                param.getMemberLevel(),
                param.getStartDate(),
                param.getEndDate()
        );

        return new PageResult<>(page.getRecords().stream().map(this::convertToVO).toList(), pageNo, pageSize, page.getTotalRow());
    }

    /**
     * 获取账号详情
     * 根据ID查询账号详细信息，包含用户信息和健康数据
     *
     * @param id 账号ID
     * @return 账号详细信息
     */
    @Override
    public AccountDetailAdminVO getAccountDetail(String id) {
        Account account = accountMapper.selectById(id);
        if (account == null || account.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "账号不存在");
        }

        AccountDetailAdminVO vo = convertToDetailVO(account);

        User user = userMapper.selectByAccountId(id);
        if (user != null) {
            vo.setRealName(user.getRealName());
            vo.setIdCard(user.getIdCard());
            vo.setEmail(user.getEmail());
            vo.setProvince(user.getProvince());
            vo.setCity(user.getCity());
            vo.setDistrict(user.getDistrict());
            vo.setAddress(user.getAddress());
            vo.setEmergencyContact(user.getEmergencyContact());
            vo.setEmergencyPhone(user.getEmergencyPhone());
        }

        // 健康数据从偏好表读取
        UserPreference preference = userPreferenceMapper.selectByAccountId(id);
        if (preference != null) {
            vo.setHeight(preference.getHeight());
            vo.setWeight(preference.getWeight());
            vo.setBloodType(preference.getBloodType());
            vo.setAllergyHistory(preference.getAllergyHistory());
            vo.setMedicalHistory(preference.getMedicalHistory());
        }

        return vo;
    }

    /**
     * 修改账号状态
     * 启用或禁用指定账号
     *
     * @param param 状态修改参数（包含账号ID和禁用标记）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyAccountStatus(ModifyAccountStatusAdminParam param) {
        Account account = accountMapper.selectById(param.getId());
        if (account == null || account.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "账号不存在");
        }
        account.setIsDisabled(param.getIsDisabled());
        account.setUpdateTime(LocalDateTime.now());
        accountMapper.update(account);
        log.info("修改账号状态：id={}，isDisabled={}", param.getId(), param.getIsDisabled());
    }

    /**
     * 修改会员等级
     * 修改指定账号的会员等级和过期时间
     *
     * @param param 会员等级修改参数（包含账号ID、等级、过期时间）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyMemberLevel(ModifyMemberLevelAdminParam param) {
        Account account = accountMapper.selectById(param.getId());
        if (account == null || account.getIsDeleted() == 1) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "账号不存在");
        }
        account.setMemberLevel(param.getMemberLevel());
        account.setMemberExpireTime(param.getMemberExpireTime());
        account.setUpdateTime(LocalDateTime.now());
        accountMapper.update(account);
        log.info("修改会员等级：id={}，memberLevel={}，memberExpireTime={}",
                param.getId(), param.getMemberLevel(), param.getMemberExpireTime());
    }

    private AccountAdminVO convertToVO(Account account) {
        AccountAdminVO vo = new AccountAdminVO();
        vo.setId(account.getId());
        vo.setNickname(account.getNickname());
        vo.setAvatarUrl(account.getAvatarUrl());
        vo.setPhone(account.getPhone());
        vo.setGender(account.getGender());
        vo.setMemberLevel(account.getMemberLevel());
        vo.setMemberExpireTime(account.getMemberExpireTime());
        vo.setIsDisabled(account.getIsDisabled());
        vo.setCreateTime(account.getCreateTime());
        return vo;
    }

    private AccountDetailAdminVO convertToDetailVO(Account account) {
        AccountDetailAdminVO vo = new AccountDetailAdminVO();
        vo.setId(account.getId());
        vo.setNickname(account.getNickname());
        vo.setAvatarUrl(account.getAvatarUrl());
        vo.setPhone(account.getPhone());
        vo.setGender(account.getGender());
        vo.setMemberLevel(account.getMemberLevel());
        vo.setMemberExpireTime(account.getMemberExpireTime());
        vo.setIsDisabled(account.getIsDisabled());
        vo.setCreateTime(account.getCreateTime());
        return vo;
    }
}
