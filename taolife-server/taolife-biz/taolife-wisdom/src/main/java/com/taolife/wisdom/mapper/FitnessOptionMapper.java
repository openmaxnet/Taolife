package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.FitnessOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 体质问卷选项Mapper接口
 *
 * @author 文二
 * @date 2026-03-22
 */
@Mapper
public interface FitnessOptionMapper extends BaseMapper<FitnessOption> {

    /**
     * 根据题目ID查询选项列表
     *
     * @param questionId 题目ID
     * @return 选项列表
     */
    default List<FitnessOption> selectByQuestionId(String questionId) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(FitnessOption::getQuestionId).eq(questionId)
                .orderBy(FitnessOption::getOptionNo, true)
        );
    }

    /**
     * 根据题目ID列表批量查询选项
     *
     * @param questionIds 题目ID列表
     * @return 选项列表
     */
    default List<FitnessOption> selectByQuestionIds(List<String> questionIds) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(FitnessOption::getQuestionId).in(questionIds)
                .orderBy(FitnessOption::getQuestionId, true)
                .orderBy(FitnessOption::getOptionNo, true)
        );
    }
}
