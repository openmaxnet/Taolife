package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.FitnessQuestion;
import com.taolife.wisdom.param.QuestionPageAdminParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 体质问卷题目Mapper接口
 *
 * @author 文二
 * @date 2026-03-22
 */
@Mapper
public interface FitnessQuestionMapper extends BaseMapper<FitnessQuestion> {

    /**
     * 根据模式查询题目列表
     *
     * @param mode 1-简易模式，2-精细模式
     * @return 题目列表
     */
    default List<FitnessQuestion> selectByMode(Integer mode) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(FitnessQuestion::getQuestionMode).eq(mode)
                .and(FitnessQuestion::getIsDisabled).eq(0)
                .and(FitnessQuestion::getIsDeleted).eq(0)
                .orderBy(FitnessQuestion::getQuestionNo, true)
        );
    }

    /**
     * 根据题目ID查询题目
     *
     * @param id 题目ID
     * @return 题目
     */
    default FitnessQuestion selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(FitnessQuestion::getId).eq(id)
                .and(FitnessQuestion::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 分页查询题目列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<FitnessQuestion> selectPageByParam(QuestionPageAdminParam param) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(FitnessQuestion::getIsDeleted).eq(0);

        if (param.getQuestionMode() != null) {
            wrapper.and(FitnessQuestion::getQuestionMode).eq(param.getQuestionMode());
        }

        wrapper.orderBy(FitnessQuestion::getSortOrder, true);
        return paginate(param.getPageNo(), param.getPageSize(), wrapper);
    }
}
