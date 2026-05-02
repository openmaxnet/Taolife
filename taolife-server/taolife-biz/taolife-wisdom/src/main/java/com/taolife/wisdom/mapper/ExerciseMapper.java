package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.Exercise;
import com.taolife.wisdom.param.ExerciseQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 运动项目Mapper
 *
 * @author 文二
 * @date 2026-03-24
 */
@Mapper
public interface ExerciseMapper extends BaseMapper<Exercise> {

    /**
     * 根据体质编码查询推荐运动项目
     *
     * @param constitutionCode 体质编码
     * @return 推荐运动项目列表
     */
    default List<Exercise> selectByConstitutionCode(String constitutionCode) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.where(Exercise::getTargetConstitutionCodes).like(constitutionCode)
                .and(Exercise::getIsDeleted).eq(0)
                .and(Exercise::getIsDisabled).eq(0)
                .limit(5);

        List<Exercise> exercises = selectListByQuery(wrapper);

        if (exercises.isEmpty()) {
            wrapper = new QueryWrapper();
            wrapper.where(Exercise::getName).in("太极拳", "八段锦", "散步")
                    .and(Exercise::getIsDeleted).eq(0);
            exercises = selectListByQuery(wrapper);
        }

        return exercises;
    }

    /**
     * 根据ID查询运动项目（管理后台使用，仅过滤已删除）
     *
     * @param id 运动项目ID
     * @return 运动项目对象
     */
    default Exercise selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Exercise::getId).eq(id)
                .and(Exercise::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询运动项目（用户端，过滤已删除和已禁用）
     *
     * @param id 运动项目ID
     * @return 运动项目对象
     */
    default Exercise selectByIdForUser(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Exercise::getId).eq(id)
                .and(Exercise::getIsDeleted).eq(0)
                .and(Exercise::getIsDisabled).eq(0)
                .limit(1)
        );
    }

    /**
     * 后台管理：分页查询运动项目列表
     *
     * @param page     分页参数
     * @param category 分类（可选）
     * @param keyword  关键词（可选）
     * @return 分页结果
     */
    default Page<Exercise> selectAdminPage(Page<Exercise> page, Integer category, String keyword) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(Exercise::getIsDeleted).eq(0);
        if (category != null && category != 0) {
            wrapper.and(Exercise::getCategory).eq(category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(Exercise::getName).like(keyword);
        }
        wrapper.orderBy(Exercise::getCreateTime, false);
        return paginate(page, wrapper);
    }

    /**
     * 用户端：分页查询运动项目列表
     *
     * @param page  分页参数
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<Exercise> selectUserPage(Page<Exercise> page, ExerciseQueryParam param) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(Exercise::getIsDeleted).eq(0)
                .and(Exercise::getIsDisabled).eq(0);
        if (param.getCategory() != null) {
            wrapper.and(Exercise::getCategory).eq(param.getCategory());
        }
        if (param.getIntensity() != null) {
            wrapper.and(Exercise::getIntensity).eq(param.getIntensity());
        }
        if (param.getKeyword() != null && !param.getKeyword().isEmpty()) {
            wrapper.and(Exercise::getName).like(param.getKeyword())
                    .or(Exercise::getNamePinyin).like(param.getKeyword());
        }
        wrapper.orderBy(Exercise::getSortOrder, true)
                .orderBy(Exercise::getCreateTime, false);
        return paginate(page, wrapper);
    }

    /**
     * 用户端：查询同分类相关运动
     *
     * @param excludeId 排除的运动ID
     * @param category  分类（可选）
     * @return 相关运动列表
     */
    default List<Exercise> selectRelated(String excludeId, Integer category) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(Exercise::getIsDeleted).eq(0)
                .and(Exercise::getIsDisabled).eq(0)
                .and(Exercise::getId).ne(excludeId);
        if (category != null) {
            wrapper.and(Exercise::getCategory).eq(category);
        }
        wrapper.orderBy(Exercise::getViewCount, false)
                .limit(4);
        return selectListByQuery(wrapper);
    }
}
