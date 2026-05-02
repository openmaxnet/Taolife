package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.Acupoint;
import com.taolife.wisdom.param.AcupointQueryParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 穴位Mapper接口
 *
 * @author 文二
 * @date 2026-03-24
 */
@Mapper
public interface AcupointMapper extends BaseMapper<Acupoint> {

    /**
     * 根据ID查询穴位（带逻辑删除过滤）
     *
     * @param id 穴位ID
     * @return 穴位对象
     */
    default Acupoint selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Acupoint::getId).eq(id)
                .and(Acupoint::getIsDeleted).eq(0)
                .and(Acupoint::getIsDisabled).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据ID查询穴位（管理后台，仅过滤逻辑删除）
     *
     * @param id 穴位ID
     * @return 穴位对象
     */
    default Acupoint selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Acupoint::getId).eq(id)
                .and(Acupoint::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 分页查询穴位列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<Acupoint> selectAcupointPage(AcupointQueryParam param) {
        QueryWrapper wrapper = buildQueryWrapper(param);
        return paginate(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
    }

    /**
     * 根据经络编码查询穴位列表
     *
     * @param meridianCode 经络编码
     * @return 穴位列表
     */
    default Page<Acupoint> selectByMeridianCode(String meridianCode, Integer pageNo, Integer pageSize) {
        return paginate(new Page<>(pageNo, pageSize),
            QueryWrapper.create()
                .where(Acupoint::getIsDeleted).eq(0)
                .and(Acupoint::getIsDisabled).eq(0)
                .and(Acupoint::getMeridianCode).eq(meridianCode)
                .orderBy(Acupoint::getSortOrder, true)
        );
    }

    /**
     * 构建穴位查询条件
     *
     * @param param 查询参数
     * @return 查询条件包装器
     */
    default QueryWrapper buildQueryWrapper(AcupointQueryParam param) {
        QueryWrapper wrapper = QueryWrapper.create();

        // 默认条件：未删除、未禁用
        wrapper.where(Acupoint::getIsDeleted).eq(0);
        wrapper.and(Acupoint::getIsDisabled).eq(0);

        // 经络编码筛选
        if (param.getMeridianCode() != null && !param.getMeridianCode().isEmpty()) {
            wrapper.and(Acupoint::getMeridianCode).eq(param.getMeridianCode());
        }

        // 标注类型筛选
        if (param.getMarkerType() != null && param.getMarkerType() != 0) {
            wrapper.and(Acupoint::getMarkerType).eq(param.getMarkerType());
        }

        // 关键词搜索（名称或拼音模糊匹配）
        if (param.getKeyword() != null && !param.getKeyword().isEmpty()) {
            wrapper.and(Acupoint::getName).like(param.getKeyword())
                   .or(Acupoint::getNamePinyin).like(param.getKeyword());
        }

        // 按排序字段正序排列
        wrapper.orderBy(Acupoint::getSortOrder, true);

        return wrapper;
    }

    /**
     * 根据名称精确查询穴位
     *
     * @param name 穴位名称
     * @return 穴位对象
     */
    default Acupoint selectByName(String name) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Acupoint::getName).eq(name)
                .and(Acupoint::getIsDeleted).eq(0)
                .and(Acupoint::getIsDisabled).eq(0)
                .limit(1)
        );
    }
}
