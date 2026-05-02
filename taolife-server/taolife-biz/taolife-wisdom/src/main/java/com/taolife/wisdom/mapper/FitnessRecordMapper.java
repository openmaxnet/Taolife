package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.FitnessRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 体质测评记录Mapper接口
 *
 * @author 文二
 * @date 2026-03-22
 */
@Mapper
public interface FitnessRecordMapper extends BaseMapper<FitnessRecord> {

    /**
     * 查询用户的测评历史记录
     *
     * @param accountId 账号ID
     * @return 测评记录列表
     */
    default List<FitnessRecord> selectByAccountId(String accountId) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(FitnessRecord::getAccountId).eq(accountId)
                .and(FitnessRecord::getIsDeleted).eq(0)
                .orderBy(FitnessRecord::getCreateTime, false)
        );
    }

    /**
     * 查询用户最新的一条测评记录
     *
     * @param accountId 账号ID
     * @return 测评记录
     */
    default FitnessRecord selectLatestByAccountId(String accountId) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(FitnessRecord::getAccountId).eq(accountId)
                .and(FitnessRecord::getIsLatest).eq(1)
                .and(FitnessRecord::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询测评记录
     *
     * @param id 记录ID
     * @return 测评记录
     */
    default FitnessRecord selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(FitnessRecord::getId).eq(id)
                .and(FitnessRecord::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 更新用户的所有记录为非最新
     *
     * @param accountId 账号ID
     * @return 更新行数
     */
    default int updateAllToNotLatest(String accountId) {
        FitnessRecord update = new FitnessRecord();
        update.setIsLatest(0);
        return updateByQuery(
            update,
            QueryWrapper.create()
                .where(FitnessRecord::getAccountId).eq(accountId)
                .and(FitnessRecord::getIsLatest).eq(1)
        );
    }
}
