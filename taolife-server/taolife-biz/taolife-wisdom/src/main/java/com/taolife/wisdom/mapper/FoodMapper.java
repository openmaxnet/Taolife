package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.Food;
import com.taolife.wisdom.param.FoodQueryParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 食材Mapper接口
 *
 * @author 文二
 * @date 2026-03-20
 */
@Mapper
public interface FoodMapper extends BaseMapper<Food> {

    /**
     * 根据ID查询食材（带逻辑删除过滤）
     *
     * @param id 食材ID
     * @return 食材对象
     */
    default Food selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Food::getId).eq(id)
                .and(Food::getIsDeleted).eq(0)
                .and(Food::getIsDisabled).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询食材（管理后台使用，仅过滤已删除，不过滤已禁用）
     *
     * @param id 食材ID
     * @return 食材对象
     */
    default Food selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Food::getId).eq(id)
                .and(Food::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 分页查询食材列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<Food> selectFoodPage(FoodQueryParam param) {
        QueryWrapper wrapper = buildQueryWrapper(param);
        return paginate(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
    }

    /**
     * 构建食材查询条件
     *
     * @param param 查询参数
     * @return 查询条件包装器
     */
    default QueryWrapper buildQueryWrapper(FoodQueryParam param) {
        QueryWrapper wrapper = QueryWrapper.create();

        // 默认条件：未删除、未禁用
        wrapper.where(Food::getIsDeleted).eq(0);
        wrapper.and(Food::getIsDisabled).eq(0);

        // 分类筛选（0表示全部，不添加筛选条件）
        if (param.getCategory() != null && param.getCategory() != 0) {
            wrapper.and(Food::getCategory).eq(param.getCategory());
        }

        // 性质筛选
        if (param.getNature() != null) {
            wrapper.and(Food::getNature).eq(param.getNature());
        }

        // 关键词搜索（名称或拼音模糊匹配）
        if (param.getKeyword() != null && !param.getKeyword().isEmpty()) {
            wrapper.and(Food::getName).like(param.getKeyword());
            wrapper.or(Food::getNamePinyin).like(param.getKeyword());
        }

        // 味道筛选
        if (param.getFlavor() != null && !param.getFlavor().isEmpty()) {
            String[] flavors = param.getFlavor().split(",");
            for (int i = 0; i < flavors.length; i++) {
                if (i == 0) wrapper.and(Food::getFlavor).like(flavors[i].trim());
                else wrapper.or(Food::getFlavor).like(flavors[i].trim());
            }
        }

        // 归经筛选
        if (param.getMeridianEntry() != null && !param.getMeridianEntry().isEmpty()) {
            String[] meridians = param.getMeridianEntry().split(",");
            for (int i = 0; i < meridians.length; i++) {
                if (i == 0) wrapper.and(Food::getMeridianEntry).like(meridians[i].trim());
                else wrapper.or(Food::getMeridianEntry).like(meridians[i].trim());
            }
        }

        return wrapper;
    }

    /**
     * 根据名称精确查询食材
     *
     * @param name 食材名称
     * @return 食材对象
     */
    default Food selectByName(String name) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Food::getName).eq(name)
                .and(Food::getIsDeleted).eq(0)
                .and(Food::getIsDisabled).eq(0)
                .limit(1)
        );
    }
}
