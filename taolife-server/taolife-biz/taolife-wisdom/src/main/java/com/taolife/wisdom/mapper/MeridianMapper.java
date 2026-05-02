package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.Meridian;
import com.taolife.wisdom.param.MeridianQueryParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 经络Mapper接口
 *
 * @author 文二
 * @date 2026-03-24
 */
@Mapper
public interface MeridianMapper extends BaseMapper<Meridian> {

    /**
     * 根据ID查询经络（带逻辑删除过滤）
     *
     * @param id 经络ID
     * @return 经络对象
     */
    default Meridian selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Meridian::getId).eq(id)
                .and(Meridian::getIsDeleted).eq(0)
                .and(Meridian::getIsDisabled).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询经络（管理后台，仅过滤逻辑删除）
     *
     * @param id 经络ID
     * @return 经络对象
     */
    default Meridian selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Meridian::getId).eq(id)
                .and(Meridian::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 分页查询经络列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<Meridian> selectMeridianPage(MeridianQueryParam param) {
        QueryWrapper wrapper = buildQueryWrapper(param);
        return paginate(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
    }

    /**
     * 构建经络查询条件
     *
     * @param param 查询参数
     * @return 查询条件包装器
     */
    default QueryWrapper buildQueryWrapper(MeridianQueryParam param) {
        QueryWrapper wrapper = QueryWrapper.create();

        // 默认条件：未删除、未禁用
        wrapper.where(Meridian::getIsDeleted).eq(0);
        wrapper.and(Meridian::getIsDisabled).eq(0);

        // 分类筛选（0表示全部，不添加筛选条件）
        if (param.getCategory() != null && param.getCategory() != 0) {
            wrapper.and(Meridian::getCategory).eq(param.getCategory());
        }

        // 经络编码精确匹配
        if (param.getCode() != null && !param.getCode().isEmpty()) {
            wrapper.and(Meridian::getCode).eq(param.getCode());
        }

        // 关键词搜索（名称或拼音模糊匹配）
        if (param.getKeyword() != null && !param.getKeyword().isEmpty()) {
            wrapper.and(q -> {
                QueryWrapper qw = (QueryWrapper) q;
                qw.where(Meridian::getName).like(param.getKeyword());
                qw.or(Meridian::getNamePinyin).like(param.getKeyword());
            });
        }

        // 按排序字段正序排列
        wrapper.orderBy(Meridian::getSortOrder, true);

        return wrapper;
    }

    /**
     * 根据名称精确查询经络
     *
     * @param name 经络名称
     * @return 经络对象
     */
    default Meridian selectByName(String name) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Meridian::getName).eq(name)
                .and(Meridian::getIsDeleted).eq(0)
                .and(Meridian::getIsDisabled).eq(0)
                .limit(1)
        );
    }
}
