package com.taolife.plan.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.plan.entity.UserPlan;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户养生方案Mapper接口
 *
 * @author 文二
 * @date 2026-04-06
 */
@Mapper
public interface UserPlanMapper extends BaseMapper<UserPlan> {

    /**
     * 根据ID查询用户方案（带逻辑删除过滤）
     *
     * @param id 方案ID
     * @return 用户方案对象
     */
    default UserPlan selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(UserPlan::getId).eq(id)
                .and(UserPlan::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据账号ID查询所有方案（按创建时间倒序）
     *
     * @param accountId 账号ID
     * @return 用户方案列表
     */
    default List<UserPlan> selectByAccountId(String accountId) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(UserPlan::getAccountId).eq(accountId)
                .and(UserPlan::getIsDeleted).eq(0)
                .orderBy(UserPlan::getCreateTime, false)
        );
    }

    /**
     * 查询到期且进行中的方案（endDate < 今天 且 status=1 且未删除）
     *
     * @return 到期的用户方案列表
     */
    default List<UserPlan> selectExpiredActivePlans(LocalDate today) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(UserPlan::getStatus).eq(1)
                .and(UserPlan::getEndDate).lt(today)
                .and(UserPlan::getIsDeleted).eq(0)
        );
    }

    /**
     * 查询用户最新的进行中方案（status=1）
     *
     * @param accountId 账号ID
     * @return 进行中的方案，无则返回null
     */
    default UserPlan selectActiveByAccountId(String accountId) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(UserPlan::getAccountId).eq(accountId)
                .and(UserPlan::getStatus).eq(1)
                .and(UserPlan::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 查询最新的养生方案（按创建时间倒序取第一条）
     *
     * @param accountId 账号ID
     * @return 最新的用户方案
     */
    default UserPlan selectLatestPlan(String accountId) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(UserPlan::getAccountId).eq(accountId)
                .and(UserPlan::getIsDeleted).eq(0)
                .orderBy(UserPlan::getCreateTime, false)
                .limit(1)
        );
    }

    /**
     * 分页查询方案历史（已完成的方案，按创建时间倒序）
     *
     * @param page       分页对象
     * @param accountId  账号ID
     * @param startDate  开始日期（可选）
     * @param endDate    结束日期（可选）
     * @return 分页结果
     */
    default Page<UserPlan> selectHistoryPage(Page<UserPlan> page, String accountId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(UserPlan::getAccountId).eq(accountId)
                .and(UserPlan::getIsDeleted).eq(0)
                .and(UserPlan::getStatus).ne(1);
        if (startDate != null) {
            wrapper.and(UserPlan::getPlanDate).ge(startDate);
        }
        if (endDate != null) {
            wrapper.and(UserPlan::getPlanDate).le(endDate);
        }
        wrapper.orderBy(UserPlan::getPlanDate, false);
        return paginate(page, wrapper);
    }

    /**
     * 后台管理：分页查询所有用户方案
     * 仅过滤已删除，不过滤状态
     *
     * @param page    分页参数
     * @param keyword 关键词（方案标题/用户ID模糊搜索）
     * @param status  状态（可为null）
     * @return 分页结果
     */
    default Page<UserPlan> selectAdminPage(Page<UserPlan> page, String keyword, Integer status) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(UserPlan::getIsDeleted).eq(0);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(UserPlan::getPlanTitle).like(keyword);
        }
        if (status != null) {
            wrapper.and(UserPlan::getStatus).eq(status);
        }
        wrapper.orderBy(UserPlan::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
