package com.taolife.identity.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.identity.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 账号Mapper接口
 *
 * @author 文二
 * @date 2026-03-16
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    /**
     * 根据openid和账号类型查询账号
     * 使用QueryWrapper实现逻辑删除过滤
     *
     * @param openid      微信openid
     * @param accountType 账号类型
     * @return 账号对象
     */
    default Account selectByOpenid(String openid, Integer accountType) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Account::getOpenid).eq(openid)
                .and(Account::getAccountType).eq(accountType)
                .and(Account::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询账号（带逻辑删除过滤）
     *
     * @param id 账号ID
     * @return 账号对象
     */
    default Account selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Account::getId).eq(id)
                .and(Account::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 更新用户基本信息
     * 使用动态SQL，只更新非空字段
     *
     * @param id        账号ID
     * @param nickname  昵称
     * @param avatarUrl 头像URL
     * @param gender    性别
     * @param birthday  生日
     * @return 更新行数
     */
    default int updateUserInfo(String id, String nickname, String avatarUrl, Integer gender, LocalDate birthday) {
        Account update = new Account();
        if (nickname != null) {
            update.setNickname(nickname);
        }
        if (avatarUrl != null) {
            update.setAvatarUrl(avatarUrl);
        }
        if (gender != null) {
            update.setGender(gender);
        }
        if (birthday != null) {
            update.setBirthday(birthday);
        }
        update.setUpdateTime(LocalDateTime.now());

        return updateByQuery(
            update,
            QueryWrapper.create()
                .where(Account::getId).eq(id)
                .and(Account::getIsDeleted).eq(0)
        );
    }

    /**
     * 更新用户头像
     *
     * @param id        账号ID
     * @param avatarUrl 头像URL
     * @return 更新行数
     */
    default int updateAvatar(String id, String avatarUrl) {
        Account update = new Account();
        update.setAvatarUrl(avatarUrl);
        update.setUpdateTime(LocalDateTime.now());

        return updateByQuery(
            update,
            QueryWrapper.create()
                .where(Account::getId).eq(id)
                .and(Account::getIsDeleted).eq(0)
        );
    }

    /**
     * 更新会员信息
     *
     * @param id               账号ID
     * @param memberLevel      会员等级
     * @param memberExpireTime 会员过期时间
     * @return 更新行数
     */
    default int updateMemberInfo(String id, Integer memberLevel, LocalDateTime memberExpireTime) {
        Account update = new Account();
        update.setMemberLevel(memberLevel);
        update.setMemberExpireTime(memberExpireTime);
        update.setUpdateTime(LocalDateTime.now());

        return updateByQuery(
            update,
            QueryWrapper.create()
                .where(Account::getId).eq(id)
                .and(Account::getIsDeleted).eq(0)
        );
    }

    /**
     * 更新会员成长信息
     *
     * @param id          账号ID
     * @param growthValue 成长值
     * @param growthLevel 成长等级
     * @return 更新行数
     */
    default int updateGrowthInfo(String id, Integer growthValue, Integer growthLevel) {
        Account update = new Account();
        update.setGrowthValue(growthValue);
        update.setGrowthLevel(growthLevel);
        update.setUpdateTime(LocalDateTime.now());

        return updateByQuery(
            update,
            QueryWrapper.create()
                .where(Account::getId).eq(id)
                .and(Account::getIsDeleted).eq(0)
        );
    }

    /**
     * 更新账号手机号
     *
     * @param id          账号ID
     * @param phoneNumber 手机号
     * @return 更新行数
     */
    default int updatePhoneById(String id, String phoneNumber) {
        Account update = new Account();
        update.setPhone(phoneNumber);
        update.setUpdateTime(LocalDateTime.now());

        return updateByQuery(
            update,
            QueryWrapper.create()
                .where(Account::getId).eq(id)
                .and(Account::getIsDeleted).eq(0)
        );
    }

    /**
     * 管理后台分页查询账号列表
     *
     * @param page       分页参数
     * @param keyword    关键词（手机号/昵称模糊搜索，可为null）
     * @param memberLevel 会员等级（可选）
     * @param startDate 注册开始时间（可选）
     * @param endDate   注册结束时间（可选）
     * @return 分页结果
     */
    default Page<Account> selectAdminPage(Page<Account> page, String keyword, Integer memberLevel,
                                          String startDate, String endDate) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.where(Account::getIsDeleted).eq(0);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(Account::getPhone).like(keyword)
                   .or(Account::getNickname).like(keyword);
        }
        if (memberLevel != null) {
            wrapper.and(Account::getMemberLevel).eq(memberLevel);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.and(Account::getCreateTime).ge(LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.and(Account::getCreateTime).le(LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX));
        }

        wrapper.orderBy(Account::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
