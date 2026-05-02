package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.FitnessType;
import com.taolife.wisdom.param.FitnessTypePageAdminParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 体质类型Mapper接口
 *
 * @author 文二
 * @date 2026-03-22
 */
@Mapper
public interface FitnessTypeMapper extends BaseMapper<FitnessType> {

    /**
     * 查询所有启用的体质类型列表
     *
     * @return 体质类型列表
     */
    default List<FitnessType> selectAllEnabled() {
        return selectListByQuery(
            QueryWrapper.create()
                .where(FitnessType::getIsDisabled).eq(0)
                .and(FitnessType::getIsDeleted).eq(0)
                .orderBy(FitnessType::getSortOrder, true)
        );
    }

    /**
     * 根据编码查询体质类型
     *
     * @param code 体质编码
     * @return 体质类型
     */
    default FitnessType selectByCode(String code) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(FitnessType::getCode).eq(code)
                .and(FitnessType::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 分页查询体质类型列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<FitnessType> selectPageByParam(FitnessTypePageAdminParam param) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(FitnessType::getIsDeleted).eq(0)
                .orderBy(FitnessType::getSortOrder, true);
        return paginate(param.getPageNo(), param.getPageSize(), wrapper);
    }
}
