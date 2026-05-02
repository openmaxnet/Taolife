package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.AcupointCombo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 穴位配伍Mapper
 *
 * @author 文二
 * @date 2026-03-24
 */
@Mapper
public interface AcupointComboMapper extends BaseMapper<AcupointCombo> {

    /**
     * 根据体质编码查询推荐穴位配伍
     * 优先返回匹配的穴位配伍，如果没有则返回日常保健配伍
     *
     * @param constitutionCode 体质编码
     * @return 穴位配伍列表
     */
    default List<AcupointCombo> selectByConstitutionCode(String constitutionCode) {
        // 查询匹配的穴位配伍
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.where(AcupointCombo::getTargetConstitutionCodes).like(constitutionCode)
                .and(AcupointCombo::getIsDeleted).eq(0)
                .and(AcupointCombo::getIsDisabled).eq(0)
                .limit(3);

        List<AcupointCombo> combines = selectListByQuery(wrapper);

        // 如果没有匹配数据，返回日常保健配伍
        if (combines.isEmpty()) {
            wrapper = new QueryWrapper();
            wrapper.where(AcupointCombo::getCategory).eq(4)
                    .and(AcupointCombo::getIsDeleted).eq(0);
            combines = selectListByQuery(wrapper);
        }

        return combines;
    }

    /**
     * 根据ID查询穴位配伍（管理后台使用，仅过滤已删除）
     *
     * @param id 穴位配伍ID
     * @return 穴位配伍对象
     */
    default AcupointCombo selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(AcupointCombo::getId).eq(id)
                .and(AcupointCombo::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 后台管理：分页查询穴位配伍列表
     *
     * @param page     分页参数
     * @param category 分类（可为null）
     * @param keyword  关键词（可为null）
     * @return 分页结果
     */
    default Page<AcupointCombo> selectAdminPage(Page<AcupointCombo> page, Integer category, String keyword) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(AcupointCombo::getIsDeleted).eq(0);
        if (category != null && category != 0) {
            wrapper.and(AcupointCombo::getCategory).eq(category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(AcupointCombo::getName).like(keyword);
        }
        wrapper.orderBy(AcupointCombo::getCreateTime, false);
        return paginate(page, wrapper);
    }
}
